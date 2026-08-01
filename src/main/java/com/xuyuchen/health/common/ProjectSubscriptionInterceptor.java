package com.xuyuchen.health.common;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class ProjectSubscriptionInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() != StompCommand.SUBSCRIBE && accessor.getCommand() != StompCommand.SEND) return message;
        String authenticatedProject = accessor.getSessionAttributes() == null ? null : String.valueOf(accessor.getSessionAttributes().get("projectId"));
        String destination = accessor.getDestination();
        if (authenticatedProject == null || destination == null) throw new IllegalArgumentException("websocket project authentication required");
        String marker = "/projects/";
        int start = destination.indexOf(marker);
        if (start < 0 || !destination.substring(start + marker.length()).startsWith(authenticatedProject + "/")) throw new IllegalArgumentException("websocket project access denied");
        return message;
    }
}
