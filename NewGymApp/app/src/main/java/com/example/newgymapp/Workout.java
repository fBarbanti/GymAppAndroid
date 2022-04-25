package com.example.newgymapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Workout implements Parcelable {
    private ArrayList<Exercise> exerciseArrayList;
    private String type;
    private String name;
    private int minTot;
    private int secTot;

    public Workout(ArrayList<Exercise> exerciseArrayList, String type, String name, int minTot, int secTot) {
        this.exerciseArrayList = exerciseArrayList;
        this.name = name;
        this.type = type;
        this.minTot = minTot;
        this.secTot = secTot;
    }

    public Workout(ArrayList<Exercise> exerciseArrayList, String type, String name) {
        this.exerciseArrayList = exerciseArrayList;
        this.name = name;
        this.type = type;
        this.minTot = 0;
        this.secTot = 0;
        updateDuration();
    }

    public Workout(){
        this.name = "None";
        this.type = "None";
        this.minTot = 0;
        this.secTot = 0;
    }

    protected Workout(Parcel in) {
        type = in.readString();
        name = in.readString();
        minTot = in.readInt();
        secTot = in.readInt();
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    public ArrayList<Exercise> getExerciseArrayList() {
        return exerciseArrayList;
    }

    public void setExerciseArrayList(ArrayList<Exercise> exerciseArrayList) {
        this.exerciseArrayList = exerciseArrayList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinTot() {
        return minTot;
    }

    public void setMinTot(int minTot) {
        this.minTot = minTot;
    }

    public int getSecTot() {
        return secTot;
    }

    public void setSecTot(int secTot) {
        this.secTot = secTot;
    }

    public String getFormattedTimeTotal(){
        String stringTemp = String.valueOf(minTot) + ":" + String.valueOf(secTot);
        return stringTemp;
    }

    public String getFormattedTimer(){
        return String.format("%02d:%02d", minTot, secTot);
    }

    public int trasformTimeinSecond(){
        int minToSec = minTot * 60;
        return (secTot + minToSec);
    }

    public void updateDuration(){
        secTot = 0;
        minTot = 0;
        for(Exercise exercise : exerciseArrayList){
            secTot = exercise.getSec() + secTot;
            minTot = exercise.getMin() + minTot;
        }
        minTot = minTot + ((int) secTot / 60);
        secTot = secTot % 60;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeString(name);
        parcel.writeInt(minTot);
        parcel.writeInt(secTot);
    }
}
