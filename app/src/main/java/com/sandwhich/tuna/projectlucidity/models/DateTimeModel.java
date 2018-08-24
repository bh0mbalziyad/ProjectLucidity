package com.sandwhich.tuna.projectlucidity.models;

public class DateTimeModel {
    String date,time;

    public DateTimeModel() {
    }

    public DateTimeModel(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
