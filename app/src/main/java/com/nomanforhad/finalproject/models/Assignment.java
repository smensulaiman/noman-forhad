package com.nomanforhad.finalproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Assignment implements Parcelable {

    private String assignmentId;
    private String fileName;
    private String userName;
    private String userId;
    private String fileUrl;
    private int grade = 0;
    private String remarks = "No Comment";

    public Assignment() {
    }

    public Assignment(String assignmentId, String fileName, String userName, String userId, String fileUrl, int grade, String remarks) {
        this.assignmentId = assignmentId;
        this.fileName = fileName;
        this.userName = userName;
        this.userId = userId;
        this.fileUrl = fileUrl;
        this.grade = grade;
        this.remarks = remarks;
    }

    protected Assignment(Parcel in) {
        assignmentId = in.readString();
        fileName = in.readString();
        userName = in.readString();
        userId = in.readString();
        fileUrl = in.readString();
        grade = in.readInt();
        remarks = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(assignmentId);
        dest.writeString(fileName);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(fileUrl);
        dest.writeInt(grade);
        dest.writeString(remarks);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Assignment> CREATOR = new Creator<Assignment>() {
        @Override
        public Assignment createFromParcel(Parcel in) {
            return new Assignment(in);
        }

        @Override
        public Assignment[] newArray(int size) {
            return new Assignment[size];
        }
    };

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }
}
