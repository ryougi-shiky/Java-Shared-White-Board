package com.whiteboard.server.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import java.util.Map;

@Controller
public class LogController {
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @SubscribeMapping("/welcome")
    public String sendWelcomeMessage(@Headers Map<String, Object> headers) {
        logger.info("Client connected: " + StompHeaderAccessor.wrap(headers).getSessionId());
        return "Welcome to the WebSocket server!";
    }
}
