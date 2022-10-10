package com.helper.app.presenters;

import com.helper.app.models.Course;

import java.util.List;

public interface CoursesCallback extends BaseCallback {
    default void onGetCoursesComplete(List<Course> courses) {
    }
}
