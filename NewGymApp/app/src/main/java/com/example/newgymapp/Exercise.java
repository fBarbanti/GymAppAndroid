package com.example.newgymapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private int num;
    private String name;
    private int min;
    private int sec;

    public Exercise(int num, String name, int min, int sec) {
        this.num = num;
        this.name = name;
        this.min = min;
        this.sec = sec;
    }

    public Exercise(){
        this.name = "None";
        this.min = 0;
        this.sec = 0;
    }

    protected Exercise(Parcel in) {
        num = in.readInt();
        name = in.readString();
        min = in.readInt();
        sec = in.readInt();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public String getFormattedTimer(){
        return String.format("Time: %02d:%02d", min, sec);
    }

    public int trasformTimeinSecond(){
        int minToSec = min * 60;
        return (sec + minToSec);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(num);
        parcel.writeString(name);
        parcel.writeInt(min);
        parcel.writeInt(sec);
    }
}
