package com.xuyuchen.health.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final String allowedOrigins;
    private final ProjectWebSocketAuthInterceptor auth;
    private final ProjectSubscriptionInterceptor subscriptions;
    public WebSocketConfig(@Value("${health.websocket.allowed-origins:http://localhost:3000,http://localhost:8082}") String allowedOrigins, ProjectWebSocketAuthInterceptor auth, ProjectSubscriptionInterceptor subscriptions) { this.allowedOrigins = allowedOrigins; this.auth = auth; this.subscriptions = subscriptions; }
    @Override public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
    @Override public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) { registration.interceptors(subscriptions); }
    @Override public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/{projectId}").addInterceptors(auth).setAllowedOriginPatterns(allowedOrigins.split(","));
    }
}
