package com.nomanforhad.finalproject.models;

public class Status {

    private String uid;
    private StatusItem statusItem;
    private int statuscount;

    public Status() {
    }

    public String getUid() {
        return uid;
    }

    public StatusItem getStatusItem() {
        return statusItem;
    }

    public int getStatuscount() {
        return statuscount;
    }

    @Override
    public String toString() {
        return "Status{" +
                "uid='" + uid + '\'' +
                ", statusItem=" + statusItem +
                ", statuscount=" + statuscount +
                '}';
    }
}
