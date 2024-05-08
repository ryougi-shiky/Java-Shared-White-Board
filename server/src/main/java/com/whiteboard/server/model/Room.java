package com.whiteboard.server.model;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;


public class Room {
    private String id;
    private User owner;
    private Set<User> participants = new HashSet<>();
    private Map<String, Object> boardData = new ConcurrentHashMap<>();
    private List<DrawingAction> drawings;

    public Room(String id, User owner) {
        this.id = id;
        this.owner = owner;
    }
    // Constructors, getters, and setters
    public void addUser(User user) {
        participants.add(user);
    }

    public void removeUser(User user) {
        participants.remove(user);
    }
    public String getId() {
        return this.id;
    }

    public String getOwnerName() {
        return this.owner.getUsername();
    }

    public User getOwner() {
        return owner;
    }

    public Set<User> getParticipants() {
        return new HashSet<>(participants);  // Return a copy to avoid modification outside
    }

    public Map<String, Object> getBoardData() {
        return new HashMap<>(boardData);
    }

    public void addDrawing(DrawingAction drawing) {
        this.drawings.add(drawing);
    }

    public List<DrawingAction> getDrawings() {
        return this.drawings;
    }
}
