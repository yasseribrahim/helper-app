package com.accident.warning.system.app.presenters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.accident.warning.system.app.models.Notification;
import com.accident.warning.system.app.models.PushNotification;
import com.accident.warning.system.app.utils.Constants;
import com.accident.warning.system.app.utils.StorageHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsPresenter implements BasePresenter {
    private DatabaseReference reference;
    private ValueEventListener listener;
    private NotificationsCallback callback;

    public NotificationsPresenter(NotificationsCallback callback) {
        String userId = StorageHelper.getCurrentUser().getId();
        reference = FirebaseDatabase.getInstance().getReference().child(Constants.NODE_NAME_NOTIFICATIONS + "/" + userId).getRef();
        this.callback = callback;
    }

    public void save(Notification notification, String... userIds) {
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_NOTIFICATIONS);
        node.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PushNotification pushNotification = new PushNotification(notification.getLocation(), notification.getMessage());
                for (String userId : userIds) {
                    node.child(userId).push().setValue(pushNotification);
                }
                if (callback != null) {
                    callback.onSaveNotificationComplete();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete(Notification notification, int position) {
        String userId = StorageHelper.getCurrentUser().getId();
        FirebaseDatabase dp = FirebaseDatabase.getInstance();
        DatabaseReference node = dp.getReference(Constants.NODE_NAME_NOTIFICATIONS + "/" + userId);
        node.child(notification.getId()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (callback != null) {
                    callback.onDeleteNotificationComplete(position);
                }
            }
        });
    }

    public void getNotifications() {
        callback.onShowLoading();
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Notification> notifications = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Notification notification = child.getValue(Notification.class);
                    notification.setId(child.getKey());
                    notifications.add(notification);
                }

                if (callback != null) {
                    callback.onGetNotificationsComplete(notifications);
                    callback.onHideLoading();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (callback != null) {
                    callback.onFailure("Unable to get message: " + databaseError.getMessage(), null);
                    callback.onHideLoading();
                }
            }
        };
        reference.addListenerForSingleValueEvent(listener);
    }

    @Override
    public void onDestroy() {
        if (reference != null && listener != null) {
            reference.removeEventListener(listener);
        }
    }
}
