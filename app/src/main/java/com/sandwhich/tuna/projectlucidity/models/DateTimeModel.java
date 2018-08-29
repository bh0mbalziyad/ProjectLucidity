package com.sandwhich.tuna.projectlucidity.models;

import android.os.Parcel;
import android.os.Parcelable;

public class DateTimeModel implements Parcelable{
    String date,time;

    public DateTimeModel() {
    }

    public DateTimeModel(String date, String time) {
        this.date = date;
        this.time = time;
    }

    protected DateTimeModel(Parcel in) {
        date = in.readString();
        time = in.readString();
    }

    public static final Creator<DateTimeModel> CREATOR = new Creator<DateTimeModel>() {
        @Override
        public DateTimeModel createFromParcel(Parcel in) {
            return new DateTimeModel(in);
        }

        @Override
        public DateTimeModel[] newArray(int size) {
            return new DateTimeModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(time);
    }
}
