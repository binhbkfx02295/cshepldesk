package com.binhbkfx02295.cshelpdesk.webhook.service;

import com.binhbkfx02295.cshelpdesk.common.cache.MasterDataCache;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.config.FacebookAPIProperties;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.dto.FacebookUserProfileDTO;
import com.binhbkfx02295.cshelpdesk.facebookgraphapi.service.FacebookGraphAPIService;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.dto.FacebookUserDetailDTO;
import com.binhbkfx02295.cshelpdesk.facebookuser.entity.FacebookUser;
import com.binhbkfx02295.cshelpdesk.facebookuser.mapper.FacebookUserMapper;
import com.binhbkfx02295.cshelpdesk.facebookuser.service.FacebookUserService;
import com.binhbkfx02295.cshelpdesk.message.service.MessageServiceImpl;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.dto.ProgressStatusDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.progress_status.mapper.ProgressStatusMapper;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDetailDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketServiceImpl;
import com.binhbkfx02295.cshelpdesk.util.APIResultSet;
import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.message.service.MessageService;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.dto.TicketDTO;
import com.binhbkfx02295.cshelpdesk.ticket_management.ticket.service.TicketService;
import com.binhbkfx02295.cshelpdesk.webhook.dto.WebHookEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

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
    private final MasterDataCache cache;

    @Override
    public void handleWebhook(WebHookEventDTO event) {
        for (WebHookEventDTO.Entry entry : event.getEntry()) {
            for (WebHookEventDTO.Messaging messaging : entry.getMessaging()) {
                String senderId = messaging.getSender().getId();
                String recipientId = messaging.getRecipient().getId();
                String messageText = messaging.getMessage().getText();
                Timestamp timestamp = new Timestamp(messaging.getTimestamp());
                FacebookUserDTO facebookUser = senderId.equalsIgnoreCase(properties.getPageId()) ?
                        getOrCreateFacebookUser(recipientId) : getOrCreateFacebookUser(senderId);

                TicketDetailDTO ticket = getOrCreateActiveTicket(facebookUser);
                addMessageToTicket(ticket.getId(), senderId.equalsIgnoreCase(properties.getPageId()), messageText, timestamp);
            }
        }
    }

    private FacebookUserDTO getOrCreateFacebookUser(String facebookId) {
        if (facebookId.equalsIgnoreCase(properties.getPageId())) {
            return null;
        }
        APIResultSet<FacebookUserDTO> existing = facebookUserService.get(facebookId);
        if (existing.getHttpCode() == 200) return existing.getData();

        FacebookUserDTO profile = facebookGraphAPIService.getUserProfile(facebookId);
        APIResultSet<FacebookUserDTO> result = facebookUserService.save(profile);
        return result.getData();
    }

    private TicketDetailDTO getOrCreateActiveTicket(FacebookUserDTO facebookUser) {
        APIResultSet<TicketDetailDTO> latestResult = ticketService.findLatestByFacebookUserId(facebookUser.getFacebookId());
        if (latestResult.getHttpCode() != 200 || latestResult.getData() == null || latestResult.getData().getProgressStatus().getCode().equalsIgnoreCase("resolved")) {
            TicketDetailDTO dto = new TicketDetailDTO();
            dto.setProgressStatus(progressStatusMapper.toDTO(cache.getProgress("pending")));
            dto.setFacebookUser(facebookUser);
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
}
