package com.whiteboard.server.model;

public class Room {
    private String id;
    private User owner;
    private java.util.Set<User> participants = new java.util.HashSet<>();
    private java.util.Map<String, Object> boardData = new java.util.concurrent.ConcurrentHashMap<>();

    public Room(String id, User owner) {
        this.owner = owner;
        this.id = id;
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
}
