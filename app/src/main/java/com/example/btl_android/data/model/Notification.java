package com.example.btl_android.data.model;

public class Notification {
    private String id;
    private String title;
    private String message;
    private long timestamp;
    private String type; // "SCHEDULE", "NEW_ORDER", "ORDER_STATUS"
    private boolean isRead;

    public Notification() {
        // Required empty constructor for Firebase
    }

    public Notification(String title, String message, String type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
} 