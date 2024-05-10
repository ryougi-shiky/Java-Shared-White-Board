package com.whiteboard.server.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.whiteboard.server.model.DrawingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class DrawingController {
    private static final Logger logger = LoggerFactory.getLogger(DrawingController.class);

    @MessageMapping("/draw")
    @SendTo("/board/room/{roomId}")
    public DrawingAction broadcastDrawing(DrawingAction action, org.springframework.messaging.simp.SimpMessageHeaderAccessor headerAccessor) throws Exception {
        String username = headerAccessor.getUser().getName(); // Assuming username is set as Principal
        String roomId = headerAccessor.getSessionAttributes().get("roomId").toString(); // Assuming roomId is stored in session attributes
        logger.info("User: {} joined Room ID: {}", username, roomId);

        // 这里可以添加一些逻辑，例如保存画板动作到数据库。
        return action;  // 将动作转发给订阅了"/board/room/{roomId}"的客户端。
    }

}
