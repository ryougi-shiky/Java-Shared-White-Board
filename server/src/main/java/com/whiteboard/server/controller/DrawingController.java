package com.whiteboard.server.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.whiteboard.server.model.DrawingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.whiteboard.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;


@Controller
public class DrawingController {
    private static final Logger logger = LoggerFactory.getLogger(DrawingController.class);

    @Autowired
    private RoomService roomService;

    @MessageMapping("/draw/{roomId}")
    @SendTo("/board/room/{roomId}")
    public DrawingAction broadcastDrawing(@DestinationVariable String roomId, DrawingAction action) throws Exception {
        logger.info("Broadcasting drawing action in room: {}", roomId);
        logger.info("Action details: {}", action);

        // 将绘图动作保存到相应的房间
        roomService.addDrawing(roomId, action);
        logger.info("Action saved: {}", action);

        // 将动作转发给订阅了 "/board/room/{roomId}" 的客户端
        return action;
    }

    @MessageMapping("/finalizeDraw/{roomId}")
    @SendTo("/board/room/{roomId}")
    public DrawingAction finalizeDrawing(@DestinationVariable String roomId, DrawingAction action) throws Exception {
        logger.info("Finalizing drawing action in room: {}", roomId);
        logger.info("Final action details: {}", action);
        // 将绘图动作保存到相应的房间
        roomService.addDrawing(roomId, action);
        logger.info("Final action saved: {}", action);
        // 将动作转发给订阅了"/board/room/{roomId}"的客户端
        return action;  
    }
}
