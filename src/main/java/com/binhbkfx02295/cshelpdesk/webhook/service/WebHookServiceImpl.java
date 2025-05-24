package com.binhbkfx02295.cshelpdesk.webhook.service;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.EmployeeMapper;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.config.FacebookAPIProperties;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.service.FacebookGraphAPIService;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserFetchDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.mapper.FacebookUserMapper;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserService;
import com.binhbkfx02295.cshelpdesk.message.service.MessageServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.mapper.ProgressStatusMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDashboardDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.entity.Ticket;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.webhook.dto.WebHookEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebHookServiceImpl implements WebHookService {

    private final FacebookUserService facebookUserService;
    private final FacebookGraphAPIService facebookGraphAPIService;
    private final TicketServiceImpl ticketService;
    private final MessageServiceImpl messageService;
    private final FacebookAPIProperties properties;
    private final FacebookUserMapper facebookUserMapper;
    private final ProgressStatusMapper progressStatusMapper;
    private final EmployeeMapper employeeMapper;
    private final MasterDataCache cache;

    @Override
    public void handleWebhook(WebHookEventDTO event) {
        for (WebHookEventDTO.Entry entry : event.getEntry()) {
            for (WebHookEventDTO.Messaging messaging : entry.getMessaging()) {

                //extract json fields
                String senderId = messaging.getSender().getId();
                String recipientId = messaging.getRecipient().getId();
                String messageText = messaging.getMessage().getText();
                Timestamp timestamp = new Timestamp(messaging.getTimestamp());

                //get or create new FacbookUser
                FacebookUserDetailDTO facebookUser = senderId.equalsIgnoreCase(properties.getPageId()) ?
                        getOrCreateFacebookUser(recipientId) : getOrCreateFacebookUser(senderId);

                //get pending or create new
                TicketDetailDTO ticket = getOrCreateActiveTicket(facebookUser);

                //add message
                addMessageToTicket(ticket.getId(), senderId.equalsIgnoreCase(properties.getPageId()), messageText, timestamp);
            }
        }
    }

    private FacebookUserDetailDTO getOrCreateFacebookUser(String facebookId) {
        //if sender is employee - skip
        if (facebookId.equalsIgnoreCase(properties.getPageId())) {
            return null;
        }
        //check if exists
        APIResultSet<FacebookUserDetailDTO> existing = facebookUserService.get(facebookId);
        if (existing.getHttpCode() == 200) return existing.getData();

        //fetch if not exists
        FacebookUserFetchDTO profile = facebookGraphAPIService.getUserProfile(facebookId);

        //save after fetch
        APIResultSet<FacebookUserDetailDTO> result = facebookUserService.save(profile);
        return result.getData();
    }

    private TicketDetailDTO getOrCreateActiveTicket(FacebookUserDetailDTO facebookUser) {
        //retrieve latest ticket
        APIResultSet<TicketDetailDTO> latestResult = ticketService.findLatestByFacebookUserId(facebookUser.getFacebookId());

        //if ticket pending.. skip
        if (latestResult.getHttpCode() != 200 || latestResult.getData() == null || latestResult.getData().getProgressStatus().getCode().equalsIgnoreCase("resolved")) {

            //create new ticket
            TicketDetailDTO dto = new TicketDetailDTO();
            dto.setProgressStatus(progressStatusMapper.toDTO(cache.getProgress("pending")));
            dto.setFacebookUser(facebookUserMapper.toDTO(facebookUser));

            //TODO: implement auto assign
            autoAssign(dto);
            return ticketService.createTicket(dto).getData();
        }
        return latestResult.getData();
    }

    private void addMessageToTicket(int ticketId, boolean senderEmployee, String text, Timestamp timestamp) {
        MessageDTO message = new MessageDTO();
        message.setTicketId(ticketId);
        message.setText(text);
        message.setSenderEmployee(senderEmployee);
        message.setTimestamp(timestamp);

        messageService.addMessage(message);
    }

    private void autoAssign(TicketDetailDTO ticket) {
        //get employees with role staff and is online
        List<Employee> employeeList = cache.getAllEmployees().values().stream().filter(employee -> {
            return employee.getStatusLogs().get(employee.getStatusLogs().size()-1).getStatus().getId() == 1 &&
            employee.getUserGroup().getName().equalsIgnoreCase("staff");
        }).toList();

        //get their current tickets and get least
        Employee least = employeeList.get(0);
        int minTickets = Integer.MAX_VALUE;

        for (Employee employee : employeeList) {
            long count = cache.getDashboardTickets().values().stream()
                    .filter(dto -> dto.getAssignee() != null &&
                            dto.getAssignee().getUsername().equalsIgnoreCase(employee.getUsername()))
                    .count();
            if (count < minTickets) {
                minTickets = (int) count;
                least = employee;
            }
        }

        //assign to least ticket
        ticket.setAssignee(employeeMapper.toDTO(least));
        log.info("Ticket: auto assigned OK, username: {}", least.getUsername());
    }
}
