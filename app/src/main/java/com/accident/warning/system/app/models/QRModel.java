package com.accident.warning.system.app.models;

import java.util.Objects;

public class QRModel {
    private String lectureId;
    private String courseId;
    private String location;

    public QRModel() {
    }

    public QRModel(String lectureId, String courseId, String location) {
        this.lectureId = lectureId;
        this.courseId = courseId;
        this.location = location;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QRModel qrModel = (QRModel) o;
        return lectureId.equals(qrModel.lectureId) && courseId.equals(qrModel.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lectureId, courseId);
    }

    @Override
    public String toString() {
        return "QRModel{" +
                "lectureId='" + lectureId + '\'' +
                ", courseId='" + courseId + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
