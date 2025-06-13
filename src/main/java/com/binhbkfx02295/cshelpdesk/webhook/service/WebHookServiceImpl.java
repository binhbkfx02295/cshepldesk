package com.binhbkfx02295.cshelpdesk.webhook.service;

import com.binhbkfx02295.cshelpdesk.employee_management.employee.entity.Employee;
import com.binhbkfx02295.cshelpdesk.employee_management.employee.mapper.EmployeeMapper;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookUserProfileDTO;
import com.binhbkfx02295.cshelpdesk.infrastructure.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.config.FacebookAPIProperties;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.service.FacebookGraphAPIService;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.mapper.FacebookUserMapper;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserService;
import com.binhbkfx02295.cshelpdesk.message.dto.AttachmentDTO;
import com.binhbkfx02295.cshelpdesk.message.service.MessageServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.mapper.ProgressStatusMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.infrastructure.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.webhook.dto.WebHookEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                String recipient = messaging.getRecipient().getId();
                boolean isSenderEmployee = senderId.equalsIgnoreCase(properties.getPageId());
                FacebookUserDetailDTO facebookUser;
                MessageDTO messageDTO;

                //if sender is employee => get existing ticket, if any -> add message
                if (isSenderEmployee) {
                    APIResultSet<TicketDetailDTO> result = ticketService.findExistingTicket(recipient);

                    if (result.isSuccess()) {
                        TicketDetailDTO ticket = result.getData();
                        if (ticket.getAssignee() == null) {
                            if (autoAssign(ticket)) {
                                ticketService.assignTicket(ticket.getId(), ticket);
                            }

                        }
                        messageDTO = convertToMessageDTO(messaging);
                        messageDTO.setSenderSystem(ticket.getAssignee() == null);
                        log.info("test isSenderSystem? ticket.getAssignee() == null: {} ,isSenderSystem: {}", ticket.getAssignee() == null, messageDTO.isSenderSystem());
                        messageDTO.setTicketId(ticket.getId());
                        messageService.addMessage(messageDTO);
                    }
                } else { // if sender is customer, get existing ticket, if any-> add message, else create.
                    APIResultSet<TicketDetailDTO> result = ticketService.findExistingTicket(senderId);
                    TicketDetailDTO ticket;
                    if (result.isSuccess() && result.getData().getProgressStatus().getId() != 3) {
                        ticket = result.getData();
                        if (ticket.getAssignee() == null) {
                            if (autoAssign(ticket)) {
                                ticketService.assignTicket(ticket.getId(), ticket);
                            }
                        }
                    } else {
                        //if no existign ticket, create, assign, add message -> save ticket to database;
                        facebookUser = getOrCreateFacebookUser(senderId);
                        ticket = new TicketDetailDTO();
                        ticket.setProgressStatus(progressStatusMapper.toDTO(cache.getProgress(1)));
                        ticket.setFacebookUser(facebookUserMapper.toDTO(facebookUser));

                        if (!autoAssign(ticket)) {
                            log.info("assign failed, now inform customer");
                            facebookGraphAPIService.notifyNoAssignee(senderId);
                        }

                        ticket = ticketService.createTicket(ticket).getData();
                    }



                    messageDTO = convertToMessageDTO(messaging);
                    messageDTO.setTicketId(ticket.getId());
                    messageDTO.setSenderSystem(false);
                    messageService.addMessage(messageDTO);

                    if (ticket.getAssignee() == null) {
                        log.info("assign failed, now inform customer");
                        facebookGraphAPIService.notifyNoAssignee(senderId);
                    }


                }
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
        FacebookUserProfileDTO profile = facebookGraphAPIService.getUserProfile(facebookId);
        //save after fetch
        APIResultSet<FacebookUserDetailDTO> result = facebookUserService.save(profile);
        return result.getData();
    }


    private MessageDTO convertToMessageDTO(WebHookEventDTO.Messaging messaging) {
        MessageDTO message = new MessageDTO();
        message.setText(messaging.getMessage().getText());
        message.setSenderEmployee(messaging.getSender().getId().equalsIgnoreCase(properties.getPageId()));
        message.setTimestamp(new Timestamp(messaging.getTimestamp()));

        if (messaging.getMessage().getAttachments() != null) {
            for (WebHookEventDTO.Attachment attachment: messaging.getMessage().getAttachments()) {
                AttachmentDTO attachment1 = new AttachmentDTO();
                attachment1.setType(attachment.getType());
                attachment1.setUrl(attachment.getPayload().getUrl());
                attachment1.setStickerId(attachment.getPayload().getStickerId());
                message.getAttachments().add(attachment1);
            }
        }
        return message;
    }

    private boolean autoAssign(TicketDetailDTO ticket) {
        //get employees with role staff and is online
        List<Employee> employeeList = cache.getAllEmployees().values().stream().filter(employee -> {
            return employee.getStatusLogs().get(employee.getStatusLogs().size()-1).getStatus().getId() == 1 &&
            employee.getUserGroup().getCode().equalsIgnoreCase("staff");
        }).toList();

        if (employeeList.isEmpty()) {
            log.info("no online employee");
            return false;
        }

        //Least recently used first
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

        ticket.setAssignee(employeeMapper.toDTO(least));
        log.info("Ticket: auto assigned OK, username: {}", least.getUsername());
        return true;
    }
}
