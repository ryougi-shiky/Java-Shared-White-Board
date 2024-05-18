package com.whiteboard.server.service;

import com.whiteboard.server.model.User;
import com.whiteboard.server.model.Room;
import com.whiteboard.server.model.DrawingAction;

import com.whiteboard.server.response.Error;

import java.util.concurrent.*;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@org.springframework.stereotype.Service
public class RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public User registerUser(String username) throws Exception {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        User user = new User(username);
        users.put(username, user);
        return user;
    }

    public Room createRoom(String username) throws Exception {
        if (!users.containsKey(username)) {
            throw new Exception("User does not exist");
        }
        String roomId = UUID.randomUUID().toString();
        User user = users.get(username);
        Room room = new Room(roomId, user);

        rooms.put(roomId, room);
        room.addUser(user);
        return room;
    }

    public List<Room> getAllRooms() {
        // This should return the list of all rooms.
        // Here you would typically interact with your database or in-memory data store
        return new ArrayList<>(rooms.values());
    }    

    public Error joinRoom(String roomId, String username) {
        if (!rooms.containsKey(roomId)) {
            return new Error(false,"Join room failed: Room not found");
        }
        Room room = rooms.get(roomId);

        if (!users.containsKey(username)) {
            return new Error(false,"Join room failed: User not exists");
        }
        User user = users.get(username);

        // Check if the user is already a participant of the room
        // if (room.getParticipants().contains(user)) {
        //     return new Error(false,"Join room failed: Username duplicate");
        // }

        // if (!room.addUser(user)) {
        //     return new Error(false, "Join room failed: failed to add user to the room");
        // }
        room.addUser(user);
        return new Error(true, "Join room success");
    }

    public Error leaveRoom(String roomId, String username) {
        if (!rooms.containsKey(roomId)) {
            return new Error(false,"Leave room failed: Room not found");
        }
        Room room = rooms.get(roomId);

        if (!users.containsKey(username)) {
            return new Error(false,"Leave room failed: User not exists");
        }
        User user = users.get(username);
        
        room.removeUser(user);
        return new Error(true, "Leave room success");
    }

    public List<User> getParticipants(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Get room participants failed: Room not found");
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Get room participants failed: Room found but null");
        }
        return new ArrayList<>(room.getParticipants());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void addDrawing(String roomId, DrawingAction action) {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.addDrawing(action);
            logger.info("Added drawing to room: {}", roomId);
            logger.info("Room board data: {}", room.getDrawings());
        } else {
            logger.error("Room not found: {}", roomId);
        }
    }

    public List<DrawingAction> getDrawings(String roomId) {
        Room room = rooms.get(roomId);
        return room != null ? room.getDrawings() : new CopyOnWriteArrayList<>();
    }
}

