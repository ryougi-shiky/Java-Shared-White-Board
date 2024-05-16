package com.whiteboard.server.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LogController {
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @SubscribeMapping("/welcome")
    public String sendWelcomeMessage(Message<?> message) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        logger.info("Client connected: " + headerAccessor.getSessionId());
        return "Welcome to the WebSocket server!";
    }
}
