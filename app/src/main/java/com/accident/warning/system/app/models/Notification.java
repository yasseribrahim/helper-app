package com.accident.warning.system.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class Notification implements Parcelable {
    @Expose
    private String id;
    @Expose
    private String message;
    @Expose
    private long timestamp;
    @Expose
    private String location;

    public Notification() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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
        return "Notification{" + "message='" + message + '\'' + ", timestamp=" + timestamp + ", location='" + location + '\'' + '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.message);
        dest.writeLong(this.timestamp);
        dest.writeString(this.location);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.message = source.readString();
        this.timestamp = source.readLong();
        this.location = source.readString();
    }

    protected Notification(Parcel in) {
        this.id = in.readString();
        this.message = in.readString();
        this.timestamp = in.readLong();
        this.location = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel source) {
            return new Notification(source);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };
}