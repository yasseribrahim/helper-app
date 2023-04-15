package com.accident.warning.system.app.presenters;

import android.view.View;

public interface OnBaseViewCallback {
    default void onShowConnectionError(View.OnClickListener onClickListener) {
    }

    default void onShowError(String message, View.OnClickListener onClickListener) {
    }

    default void onShowProgress() {
    }

    default void onHideProgress() {
    }

    default void onUnAuthorized() {
    }

    default String onGetErrorMessage(Throwable throwable) {
        return "";
    }
}