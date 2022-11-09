package com.helper.app.models;

import com.google.firebase.database.ServerValue;

import java.util.Map;

public class PushMessage {
    private String senderId;
    private String senderName;
    private String receiveName;
    private String message;
    private Map<String, String> timestamp;

    public PushMessage() {
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public PushMessage(String senderId, String senderName, String receiveName, String message) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiveName = receiveName;
        this.message = message;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
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
        return "Message{" +
                "senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", receiveName=" + receiveName +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
