package com.whiteboard.server.controller;

import com.whiteboard.server.model.Room;
import com.whiteboard.server.model.User;
import com.whiteboard.server.response.Error;
import com.whiteboard.server.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String username) {
        try {
            User user = roomService.registerUser(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Error(false, e.getMessage()));
        }
    }

    @PostMapping("/rooms/create")
    public ResponseEntity<?> createRoom(@RequestParam String username) {
        try {
            Room room = roomService.createRoom(username);
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Error(false, e.getMessage()));
        }
    }

    @PostMapping("/rooms/join")
    public ResponseEntity<Error> joinRoom(@RequestParam String roomId, @RequestParam String username) {
        Error result = roomService.joinRoom(roomId, username);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/rooms/leave")
    public ResponseEntity<Error> leaveRoom(@RequestParam String roomId, @RequestParam String username) {
        Error result = roomService.leaveRoom(roomId, username);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
