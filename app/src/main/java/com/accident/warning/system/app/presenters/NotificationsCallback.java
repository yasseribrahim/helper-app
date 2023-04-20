package com.accident.warning.system.app.presenters;

import com.accident.warning.system.app.models.Message;
import com.accident.warning.system.app.models.Notification;

import java.util.List;

public interface NotificationsCallback extends BaseCallback {
    default void onGetNotificationsComplete(List<Notification> notifications) {
    }

    default void onSaveNotificationComplete() {
    }

    default void onDeleteNotificationComplete(int position) {
    }
}
