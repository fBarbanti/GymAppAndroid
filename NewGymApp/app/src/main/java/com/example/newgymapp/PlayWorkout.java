package com.example.newgymapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayWorkout extends AppCompatActivity {

    private ProgressBar pbInner;
    private ProgressBar pbOuter;

    private Button btnPlay;
    private Button btnStop;
    private ImageView btnBack;

    private TextView textTimerWorkout;
    private TextView textTimerExercise;

    private AlertDialog.Builder alertDialog;
    private Workout workout;
    private View actualCardWorkout;
    private TimerWorker asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);

        textTimerWorkout = findViewById(R.id.textTimerWorkout);
        textTimerExercise = findViewById(R.id.textTimerExercise);
        btnBack = findViewById(R.id.back_to_home_f_play_work);
        btnPlay = findViewById(R.id.play_workout);
        btnStop = findViewById(R.id.stop_workout);
        pbInner = findViewById(R.id.inner_bar);
        pbOuter = findViewById(R.id.outer_bar);

        workout = getIntent().getParcelableExtra("Workout");
        ArrayList<Exercise> exerciseArrayList = getIntent().getParcelableArrayListExtra("ExerciseList");
        workout.setExerciseArrayList(exerciseArrayList);

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        actualCardWorkout = layoutInflater.inflate(R.layout.card_show_exercise, findViewById(R.id.linear_layout3_play_work), true);
        actualCardWorkout.setVisibility(View.INVISIBLE);

        alertDialog = new AlertDialog.Builder(PlayWorkout.this);

        pbOuter.setProgress(0);
        pbInner.setProgress(0);
        pbOuter.setMax(workout.trasformTimeinSecond());

        asyncTask = new TimerWorker(pbOuter, pbInner, textTimerWorkout, textTimerExercise, btnPlay, btnStop,workout, actualCardWorkout);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        pbInner.setMin(0);
        pbOuter.setMin(0);

        textTimerWorkout.setText(workout.getFormattedTimer());
        setupAlertDialog();

        btnBack.setOnClickListener(view -> {
            asyncTask.cancel(true);
            startActivity(new Intent(PlayWorkout.this, HomeActivity.class));
        });


        btnPlay.setOnClickListener(view -> {
            btnStop.setClickable(true);
            btnStop.setTextColor(getResources().getColor(R.color.main_color));

            btnPlay.setClickable(false);
            btnPlay.setTextColor(getResources().getColor(R.color.custom_gray));

            if(asyncTask.getStatus() == AsyncTask.Status.RUNNING){
                Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
            }
            else{
                resetGui();
                if(asyncTask.getStatus() == AsyncTask.Status.FINISHED)
                    asyncTask.cancel(true);
                if(asyncTask.isCancelled())
                    asyncTask = new TimerWorker(pbOuter, pbInner, textTimerWorkout, textTimerExercise, btnPlay, btnStop, workout, actualCardWorkout);

                asyncTask.execute();
            }
        });

        btnStop.setOnClickListener(view -> alertDialog.show());

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        asyncTask.cancel(true);
    }


    private void resetGui() {
        pbInner.setProgress(0);
        pbOuter.setProgress(0);
    }

    private void setupAlertDialog(){
        alertDialog.setTitle("Stop workout?");
        alertDialog.setMessage("Are you sure you want to stop this workout?");
        alertDialog.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            // Delete async thread
            if(asyncTask.getStatus() == AsyncTask.Status.RUNNING){
                asyncTask.cancel(true);
                pbOuter.setProgress(0);
                pbInner.setProgress(0);

                btnPlay.setClickable(true);
                btnPlay.setTextColor(getResources().getColor(R.color.main_color));

                btnStop.setClickable(false);
                btnStop.setTextColor(getResources().getColor(R.color.custom_gray));
            }
        });
        alertDialog.setNegativeButton(android.R.string.no, null);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
    }
}