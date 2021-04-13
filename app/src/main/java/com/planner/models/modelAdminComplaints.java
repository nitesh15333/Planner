package com.planner.models;

public class modelAdminComplaints {
    String name,sender,message,timestamp;

    public modelAdminComplaints() {
    }

    public modelAdminComplaints(String name, String sender, String message, String timestamp) {
        this.name = name;
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
