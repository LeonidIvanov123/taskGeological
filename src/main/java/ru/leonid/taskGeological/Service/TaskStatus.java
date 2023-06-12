package ru.leonid.taskGeological.Service;

public enum TaskStatus {
    DONE("DONE"),
    INPROGRESS("IN PROGRESS"),
    ERROR("ERROR");
    private String title;
    TaskStatus(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
