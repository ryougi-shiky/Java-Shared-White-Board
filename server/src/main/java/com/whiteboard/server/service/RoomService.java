package com.whiteboard.server.service;

import com.whiteboard.server.model.User;
import com.whiteboard.server.model.Room;
import com.whiteboard.server.response.Error;

import java.util.concurrent.*;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

@org.springframework.stereotype.Service
public class RoomService {
    private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();

    public User registerUser(String username) throws Exception {
        if (users.containsKey(username)) {
            throw new Exception("Username already exists");
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
        if (users.containsKey(username)) {
            return new Error(false,"Join room failed: Username duplicate");
        }
        Room room = rooms.get(roomId);
        User user = users.get(username);
        if (!room.addUser(user)) {
            return new Error(false, "Join room failed: failed to add user to the room");
        }
        return new Error(true, "Join room success");
    }

    public Error leaveRoom(String roomId, String username) {
        if (!rooms.containsKey(roomId)) {
            return new Error(false,"Leave room failed: Room not found");
        }
        if (!users.containsKey(username)) {
            return new Error(false,"Leave room failed: User not found");
        }

        Room room = rooms.get(roomId);
        User user = users.get(username);

        if (!room.removeUser(user)) {
            return new Error(false,"Leave room failed: failed to remove user from the room");
        }
        return new Error(true, "Leave room success");
    }
}

