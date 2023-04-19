package com.accident.warning.system.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Message implements Parcelable {
    @Expose
    private String senderId;
    @Expose
    private String senderName;
    @Expose
    private String receiveName;
    @Expose
    private String message;
    @Expose
    private long timestamp;

    private String location;

    public Message() {
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.senderId);
        dest.writeString(this.senderName);
        dest.writeString(this.receiveName);
        dest.writeString(this.message);
        dest.writeLong(this.timestamp);
        dest.writeString(this.location);
    }

    public void readFromParcel(Parcel source) {
        this.senderId = source.readString();
        this.senderName = source.readString();
        this.receiveName = source.readString();
        this.message = source.readString();
        this.timestamp = source.readLong();
        this.location = source.readString();
    }

    protected Message(Parcel in) {
        this.senderId = in.readString();
        this.senderName = in.readString();
        this.receiveName = in.readString();
        this.message = in.readString();
        this.timestamp = in.readLong();
        this.location = in.readString();
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}