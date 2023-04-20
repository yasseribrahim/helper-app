package com.accident.warning.system.app.models;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class PushNotification {
    private String location;
    private String message;
    private Map<String, String> timestamp;

    public PushNotification() {
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public PushNotification(String location, String message) {
        this.location = location;
        this.message = message;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PushNotification{" +
                "location='" + location + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
