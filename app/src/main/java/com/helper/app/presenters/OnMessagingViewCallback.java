package com.helper.app.presenters;

import com.helper.app.models.Message;

public interface OnMessagingViewCallback extends OnBaseViewCallback {
    void onSendMessageSuccess();

    void onSendMessageFailure(String message);

    void onGetMessageSuccess(Message message);

    void onGetMessageFailure(String message);

    void onEmptyMessaging();
}
