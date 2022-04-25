package com.example.newgymapp;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("StaticFieldLeak")
public class TimerWorker extends AsyncTask<Integer, Integer, Integer> {

    private final ProgressBar pbOuter;
    private final ProgressBar pbInner;
    private final TextView textTimerWorkout;
    private final TextView textTimerExercise;
    private final Button btnPlay;
    private final Button btnStop;
    private final Workout workout;
    private final View actualCardWorkout;
    private final TextView textNumberExercise;
    private final TextView exerciseNameCard;
    private final TextView timerValueCard;


    public TimerWorker(ProgressBar pbOuter, ProgressBar pbInner, TextView textTimerWorkout, TextView textTimerExercise, Button btnPlay, Button btnStop, Workout workout, View actualCardWorkout) {
        this.pbOuter = pbOuter;
        this.pbInner = pbInner;
        this.textTimerWorkout = textTimerWorkout;
        this.textTimerExercise = textTimerExercise;
        this.btnPlay = btnPlay;
        this.btnStop = btnStop;
        this.workout = workout;

        this.actualCardWorkout = actualCardWorkout;
        this.textNumberExercise = this.actualCardWorkout.findViewById(R.id.textNumberExercise);
        this.exerciseNameCard = this.actualCardWorkout.findViewById(R.id.exerciseNameCard);
        this.timerValueCard = this.actualCardWorkout.findViewById(R.id.TimerValueCard);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        textTimerWorkout.setText("DONE!");
        btnPlay.setClickable(true);
        btnPlay.setTextColor(Color.parseColor("#D0FD3E"));
        btnStop.setClickable(false);
        btnStop.setTextColor(Color.parseColor("#909095"));
    }

    @Override
    protected void onPreExecute() {
        pbInner.setProgress(0);
        pbOuter.setProgress(0);
        pbOuter.setMax(workout.trasformTimeinSecond());
        workout.updateDuration();
    }

    public String getFormattedTimeTotal(int seconds){
        int min = (int) seconds/60;
        int sec = (int) seconds % 60;
        String minuteString = String.format("%02d", min);
        String secondsString = String.format("%02d", sec);
        return minuteString + ":" + secondsString;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        pbInner.setMax(values[4]);
        pbOuter.setProgress(values[0]);
        pbInner.setProgress(values[1]);
        textTimerExercise.setText(getFormattedTimeTotal(values[3]));
        textTimerWorkout.setText(getFormattedTimeTotal(values[2]));

        // SHOW ACTUAL EXERCISE ON SCREEN
        int numberExercise = values[5] + 1;
        textNumberExercise.setText(String.valueOf(numberExercise));
        exerciseNameCard.setText(workout.getExerciseArrayList().get(values[5]).getName());
        timerValueCard.setText(workout.getExerciseArrayList().get(values[5]).getFormattedTimer());
        actualCardWorkout.setVisibility(View.VISIBLE);
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        int partialSecond = 0;
        int exerciseIndex = 0;
        int totSec = workout.trasformTimeinSecond();

        for(Exercise ex : workout.getExerciseArrayList()){
            int second = ex.trasformTimeinSecond();
            int count = 0;
            while (count < second){
                if(isCancelled()) break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count ++;
                partialSecond ++;
                publishProgress(partialSecond, count, totSec-partialSecond, second - count, second, exerciseIndex);
            }
            exerciseIndex ++;
        }
        return 0;

    }


}
