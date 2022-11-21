package com.nomanforhad.finalproject.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    public enum UserType{
        STUDENT,
        TEACHER
    }

    private String uid;
    private String username;
    private String password;
    private String phone;
    private String photoUrl;
    private String about;
    private boolean online;
    private long lastSeen;
    private String userType = UserType.STUDENT.name();

    public User() {
    }

    public User(String uid, String username, String password, String phone, String photoUrl, String about, boolean online, long lastSeen, String userType) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.about = about;
        this.online = online;
        this.lastSeen = lastSeen;
        this.userType = userType;
    }


    protected User(Parcel in) {
        uid = in.readString();
        username = in.readString();
        password = in.readString();
        phone = in.readString();
        photoUrl = in.readString();
        about = in.readString();
        online = in.readByte() != 0;
        lastSeen = in.readLong();
        userType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(phone);
        dest.writeString(photoUrl);
        dest.writeString(about);
        dest.writeByte((byte) (online ? 1 : 0));
        dest.writeLong(lastSeen);
        dest.writeString(userType);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", about='" + about + '\'' +
                ", online=" + online +
                ", lastSeen=" + lastSeen +
                ", userType='" + userType + '\'' +
                '}';
    }
}
