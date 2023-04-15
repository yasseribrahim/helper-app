package com.accident.warning.system.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatId implements Parcelable {
    private User currentUser;
    private User otherUser;

    public ChatId(User currentUser, User otherUser) {
        this.currentUser = currentUser;
        this.otherUser = otherUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public User getOther(User user) {
        return currentUser.equals(user) ? otherUser : currentUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.currentUser, flags);
        dest.writeParcelable(this.otherUser, flags);
    }

    public void readFromParcel(Parcel source) {
        this.currentUser = source.readParcelable(User.class.getClassLoader());
        this.otherUser = source.readParcelable(User.class.getClassLoader());
    }

    protected ChatId(Parcel in) {
        this.currentUser = in.readParcelable(User.class.getClassLoader());
        this.otherUser = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<ChatId> CREATOR = new Parcelable.Creator<ChatId>() {
        @Override
        public ChatId createFromParcel(Parcel source) {
            return new ChatId(source);
        }

        @Override
        public ChatId[] newArray(int size) {
            return new ChatId[size];
        }
    };
}
