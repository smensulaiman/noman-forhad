package com.nomanforhad.finalproject.models;

import java.util.List;

public class Room {

    private String roomId;
    private String roomName;
    private String instructorId;
    private String instructorName;
    private String deadLine;
    private List<User> students;

    public Room() {
    }

    public Room(String roomId, String roomName, String instructorId, String instructorName, List<User> students) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.students = students;
    }

    public Room(String roomId, String roomName, String instructorId, String instructorName, String deadLine, List<User> students) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.deadLine = deadLine;
        this.students = students;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public List<User> getStudents() {
        return students;
    }

    public void setStudents(List<User> students) {
        this.students = students;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }
}
