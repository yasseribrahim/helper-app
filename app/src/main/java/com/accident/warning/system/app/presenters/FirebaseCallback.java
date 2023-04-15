package com.accident.warning.system.app.presenters;

import java.util.List;

public interface FirebaseCallback extends BaseCallback {
    default void onSaveTokenComplete() {
    }

    default void onGetTokenComplete(String token) {
    }

    default void onGetTokensComplete(List<String> tokens) {
    }

    default void onSendNotificationComplete() {
    }
}
