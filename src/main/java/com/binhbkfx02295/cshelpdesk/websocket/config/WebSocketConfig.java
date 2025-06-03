package com.binhbkfx02295.cshelpdesk.websocket.config;

import com.binhbkfx02295.cshelpdesk.websocket.interceptor.WebSocketAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor authInterceptor;

    @Value("${cshelpdesk.websocket.external-broker.enabled:false}")
    private boolean brokerRelayEnabled;

    @Value("${cshelpdesk.websocket.broker.relay.host:localhost}")
    private String relayHost;

    @Value("${cshelpdesk.websocket.broker.relay.port:61613}")
    private Integer relayPort;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setUserDestinationPrefix("/user"); // for convertAndSendToUser

        if (brokerRelayEnabled) {
            registry.enableStompBrokerRelay("/topic", "/queue")
                    .setRelayHost(relayHost)
                    .setRelayPort(relayPort);
        } else {
            registry.enableSimpleBroker("/topic", "/queue");
        }
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Needed only for the CONNECT frame → to attach authenticated Principal
        registration.interceptors(authInterceptor);
    }
}

