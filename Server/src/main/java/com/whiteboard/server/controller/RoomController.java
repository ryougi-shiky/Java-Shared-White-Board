package com.whiteboard.server.controller;

@org.springframework.web.bind.annotation.RestController
@org.springframework.web.bind.annotation.RequestMapping("/api")
public class RoomController {
    @org.springframework.beans.factory.annotation.Autowired
    private com.whiteboard.server.service.RoomService roomService;

    @org.springframework.web.bind.annotation.PostMapping("/register")
    public org.springframework.http.ResponseEntity<?> registerUser(@org.springframework.web.bind.annotation.RequestParam String username) {
        try {
            com.whiteboard.server.model.User user = roomService.registerUser(username);
            return org.springframework.http.ResponseEntity.ok(user);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.PostMapping("/createRoom")
    public org.springframework.http.ResponseEntity<?> createRoom(@org.springframework.web.bind.annotation.RequestParam String username) {
        try {
            com.whiteboard.server.model.Room room = roomService.createRoom(username);
            return org.springframework.http.ResponseEntity.ok(room);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add more endpoints as needed
}

