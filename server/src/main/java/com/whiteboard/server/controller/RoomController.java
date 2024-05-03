package com.whiteboard.server.controller;

import com.whiteboard.server.model.Room;
import com.whiteboard.server.model.User;
import com.whiteboard.server.response.Error;
import com.whiteboard.server.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


import java.util.List; 

@RestController
@RequestMapping("/api")
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private RoomService roomService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String username) {
        logger.info("Registering user with username: {}", username);
        try {
            User user = roomService.registerUser(username);
            logger.info("Registration successful for username: {}", username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Registration failed for username: {}. Error: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(new Error(false, e.getMessage()));
        }
    }

    @PostMapping("/rooms/create")
    public ResponseEntity<?> createRoom(@RequestParam String username) {
        logger.info("Creating room for username: {}", username);
        try {
            Room room = roomService.createRoom(username);
            logger.info("Room creation successful for username: {}", username);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            logger.error("Room creation failed for username: {}. Error: {}", username, e.getMessage());
            return ResponseEntity.badRequest().body(new Error(false, e.getMessage()));
        }
    }

    @PostMapping("/rooms/join")
    public ResponseEntity<Error> joinRoom(@RequestParam String roomId, @RequestParam String username) {
        logger.info("User {} attempting to join room {}", username, roomId);
        Error result = roomService.joinRoom(roomId, username);
        if (result.isSuccess()) {
            logger.info("Join room successful for username: {} in room: {}", username, roomId);
            return ResponseEntity.ok(result);
        } else {
            logger.error("Join room failed for username: {} in room: {}. Error: {}", username, roomId, result.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/rooms/leave")
    public ResponseEntity<Error> leaveRoom(@RequestParam String roomId, @RequestParam String username) {
        logger.info("User {} attempting to leave room {}", username, roomId);
        Error result = roomService.leaveRoom(roomId, username);
        if (result.isSuccess()) {
            logger.info("Leave room successful for username: {} from room: {}", username, roomId);
            return ResponseEntity.ok(result);
        } else {
            logger.error("Leave room failed for username: {} from room: {}. Error: {}", username, roomId, result.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/listrooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        try {
            List<Room> allRooms = roomService.getAllRooms();
            return ResponseEntity.ok(allRooms);
        } catch (Exception e) {
            logger.error("Failed to fetch rooms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
