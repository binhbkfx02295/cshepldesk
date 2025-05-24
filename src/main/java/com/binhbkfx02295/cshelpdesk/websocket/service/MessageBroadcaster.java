package com.binhbkfx02295.cshelpdesk.websocket.service;

import com.binhbkfx02295.cshelpdesk.message.dto.MessageDTO;
import com.binhbkfx02295.cshelpdesk.websocket.config.WebSocketConfig;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@EnableScheduling
public class MessageBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketConfig webSocketConfig;

    public MessageBroadcaster(SimpMessagingTemplate messagingTemplate, WebSocketConfig webSocketConfig) {
        this.messagingTemplate = messagingTemplate;
        this.webSocketConfig = webSocketConfig;
    }

    @Scheduled(fixedRate = 3000) // Gửi mỗi giây
    public void broadcastMessage() {
        String message50 = "Server time: Ticket#50 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String message49 = "Server time: Ticket#49 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Random random = new Random();
        boolean randomBoolean = random.nextBoolean();
        MessageDTO message50DTO = new MessageDTO();
        message50DTO.setTicketId(50);
        message50DTO.setText(message50);
        message50DTO.setSenderEmployee(randomBoolean);
        message50DTO.setTimestamp(Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        messagingTemplate.convertAndSend(webSocketConfig.getDestination(), message50DTO);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setTicketId(49);
        messageDTO.setText(message49);
        messageDTO.setSenderEmployee(randomBoolean);
        messageDTO.setTimestamp(Timestamp.valueOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        messagingTemplate.convertAndSend(webSocketConfig.getDestination(), messageDTO);
        //System.out.println(message);
    }
}
