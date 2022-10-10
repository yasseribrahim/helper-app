package com.helper.app.presenters;

import com.helper.app.models.Lecture;

import java.util.List;

public interface LecturesCallback extends BaseCallback {
    default void onGetLecturesComplete(List<Lecture> lectures) {
    }

    default void onSaveLectureComplete() {
    }
}
