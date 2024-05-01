package com.whiteboard.server.response;

public class Error {
    private boolean success;
    private String message;

    public Error(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
