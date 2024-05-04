package com.whiteboard.server.model;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room {
    private String id;
    private User owner;
    private .Set<User> participants = new HashSet<>();
    private Map<String, Object> boardData = new concurrent.ConcurrentHashMap<>();

    public Room(String id, User owner) {
        this.id = id;
        this.owner = owner;
    }
    // Constructors, getters, and setters
    public boolean addUser(User user) {
        return participants.add(user);
    }

    public boolean removeUser(User user) {
        return participants.remove(user);
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
}
