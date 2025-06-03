package com.binhbkfx02295.cshelpdesk.websocket.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("interceptor here");
        log.info("check accessor {}", accessor.toString());
        if (accessor != null && SimpMessageType.CONNECT.equals(accessor.getMessageType())) {

            Principal principal = accessor.getUser();
            if (principal == null) {
                throw new IllegalStateException("Unauthenticated WebSocket connection attempt");
            }
            log.info(principal.toString());
            // Log & expose authorities if available
            if (principal instanceof Authentication auth) {
                Set<String> roles = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                log.info("WebSocket CONNECT by {} with roles {}", auth.getName(), roles);
            } else {
                log.info("WebSocket CONNECT by {}", principal.getName());
            }
            // Principal is already set; no extra work needed.
        }
        return message;
    }
}