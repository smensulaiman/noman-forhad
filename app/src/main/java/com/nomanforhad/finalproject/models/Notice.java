package com.nomanforhad.finalproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Notice implements Parcelable {

    private String noticeId;
    private String roomId;
    private String teacherName;
    private String teacherImage;
    private String notice;
    private String fileUrl;

    public Notice() {
    }

    public Notice(String noticeId, String roomId, String teacherName, String teacherImage, String notice, String fileUrl) {
        this.noticeId = noticeId;
        this.roomId = roomId;
        this.teacherName = teacherName;
        this.teacherImage = teacherImage;
        this.notice = notice;
        this.fileUrl = fileUrl;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherImage() {
        return teacherImage;
    }

    public void setTeacherImage(String teacherImage) {
        this.teacherImage = teacherImage;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    protected Notice(Parcel in) {
        noticeId = in.readString();
        roomId = in.readString();
        teacherName = in.readString();
        teacherImage = in.readString();
        notice = in.readString();
        fileUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noticeId);
        dest.writeString(roomId);
        dest.writeString(teacherName);
        dest.writeString(teacherImage);
        dest.writeString(notice);
        dest.writeString(fileUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Notice> CREATOR = new Creator<Notice>() {
        @Override
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };
}
