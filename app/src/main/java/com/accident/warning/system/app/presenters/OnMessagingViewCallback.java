package com.accident.warning.system.app.presenters;

import com.accident.warning.system.app.models.Message;

public interface OnMessagingViewCallback extends OnBaseViewCallback {
    void onSendMessageSuccess();

    void onSendMessageFailure(String message);

    void onGetMessageSuccess(Message message);

    void onGetMessageFailure(String message);

    void onEmptyMessaging();
}
