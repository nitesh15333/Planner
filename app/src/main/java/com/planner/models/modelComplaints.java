package com.planner.models;

public class modelComplaints {
    String id,message,timestamp;

    public modelComplaints() {

    }

    public modelComplaints(String id, String message, String timestamp) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
