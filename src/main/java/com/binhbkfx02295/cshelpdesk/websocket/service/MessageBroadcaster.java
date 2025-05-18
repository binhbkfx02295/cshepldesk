package com.binhbkfx02295.cshelpdesk.websocket.service;

import com.binhbkfx02295.cshelpdesk.websocket.config.WebSocketConfig;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@EnableScheduling
public class MessageBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketConfig webSocketConfig;

    public MessageBroadcaster(SimpMessagingTemplate messagingTemplate, WebSocketConfig webSocketConfig) {
        this.messagingTemplate = messagingTemplate;
        this.webSocketConfig = webSocketConfig;
    }

    @Scheduled(fixedRate = 1000) // Gửi mỗi giây
    public void broadcastMessage() {
        String message = "Server time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        messagingTemplate.convertAndSend(webSocketConfig.getDestination(), message);
    }
}
