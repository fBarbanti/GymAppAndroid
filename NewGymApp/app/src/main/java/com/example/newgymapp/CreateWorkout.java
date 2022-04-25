package com.example.newgymapp;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateWorkout extends AppCompatActivity {

    private EditText editWorkoutName;
    private ImageButton btnAddExercise;
    private Button btnCreateWorkout;
    private RadioGroup radioGroupTrainingType;
    private ImageView btnBack;
    private LinearLayout linearLayoutWorkout;

    private int exerciseCounter;

    private DatabaseReference dbReference;
    private String fireUserId;


    private List<Exercise> exerciseList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        if((savedInstanceState != null) && (savedInstanceState.containsKey("exerciseList")))
            exerciseList = savedInstanceState.getParcelableArrayList("exerciseList");
        else
            exerciseList = new ArrayList<>();



//        exerciseCounter = 0;
        exerciseCounter = exerciseList.size();

        fireUserId = FirebaseAuth.getInstance().getUid();
        dbReference = FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference("Users").
                child(fireUserId).child("workouts");

        linearLayoutWorkout = findViewById(R.id.linear_layout_add_ex);
        editWorkoutName = findViewById(R.id.editWorkoutName);
        radioGroupTrainingType = findViewById(R.id.radio_group_button);
        btnAddExercise = findViewById(R.id.btn_add_exercise);
        btnBack = findViewById(R.id.back_to_profile_f_workout);
        btnCreateWorkout = findViewById(R.id.btn_create_workout);


    }


    @Override
    protected void onStart() {
        super.onStart();

        btnAddExercise.setOnClickListener(view -> showAddExerciseDialog());

        btnBack.setOnClickListener(view -> startActivity(new Intent(CreateWorkout.this, HomeActivity.class)));

        btnCreateWorkout.setOnClickListener(view -> {
            uploadWorkoutToFirebase();
            Toast.makeText(getApplicationContext(), "Create new workout",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(CreateWorkout.this, HomeActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showExercise();
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList("exerciseList", (ArrayList<? extends Parcelable>) exerciseList);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void uploadWorkoutToFirebase() {
        String workoutName = editWorkoutName.getText().toString();
        // check if workout name is not empty
        if (workoutName.isEmpty())
            return;

        // check if there is a workout with the same name
        dbReference.child(workoutName).get().addOnCompleteListener(task -> {
            Log.d("DB",task.getResult().toString());
            if (task.isSuccessful() && task.getResult().getValue() == null){

                // Upload Workout on Firebase if contains at least one exercise
                if (exerciseList.isEmpty())
                    Toast.makeText(getApplicationContext(), "The training must contain at least one exercise!", Toast.LENGTH_SHORT).show();
                else {
                    // Add training type
                    int radioButtonId = radioGroupTrainingType.getCheckedRadioButtonId();
                    RadioButton radioButton = radioGroupTrainingType.findViewById(radioButtonId);
                    String selectedText = radioButton.getText().toString();

                    DatabaseReference dbReferenceSingleWorkout = dbReference.child(workoutName).child("Type");
                    dbReferenceSingleWorkout.setValue(selectedText).addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Something gone wrong", Toast.LENGTH_SHORT).show();
                    });
                    for (Exercise ex : exerciseList) {
                        DatabaseReference dbReferenceSingleExercise = dbReference.child(workoutName).child(String.valueOf(ex.getNum()));
                        HashMap<String, String> hashMapToUpload = new HashMap<>();
                        hashMapToUpload.put("name", ex.getName());
                        hashMapToUpload.put("min", String.valueOf(ex.getMin()));
                        hashMapToUpload.put("sec", String.valueOf(ex.getSec()));
                        dbReferenceSingleExercise.setValue(hashMapToUpload).addOnCompleteListener(task12 -> {
                            if (!task12.isSuccessful())
                                Toast.makeText(getApplicationContext(), "Something gone wrong", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "A workout with this name already exists!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void showAddExerciseDialog() {
        final Dialog dialog = new Dialog(CreateWorkout.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_create_exercise);

        final EditText exerciseNameField = dialog.findViewById(R.id.exercise_name_edit_text);
        final Button btnAddExerciseDialog = dialog.findViewById(R.id.btn_add_exercise_dialog);
        final NumberPicker minutePicker = dialog.findViewById(R.id.minutes_picker);
        final NumberPicker secondPicker = dialog.findViewById(R.id.seconds_picker);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        };
        minutePicker.setMinValue(0);
        secondPicker.setMinValue(0);
        minutePicker.setMaxValue(60);
        secondPicker.setMaxValue(60);
        minutePicker.setValue(0);
        secondPicker.setValue(0);
        minutePicker.setFormatter(formatter);
        secondPicker.setFormatter(formatter);

        btnAddExerciseDialog.setOnClickListener(view -> {
            String name = exerciseNameField.getText().toString();
            int minutes = minutePicker.getValue();
            int seconds = secondPicker.getValue();
            //if the exercise lasts at least one second
            if ((minutes + seconds) > 0) {
                if (!name.isEmpty()) {
                    exerciseCounter++;
                    // Put data in exercise list
                    Exercise ex = new Exercise(exerciseCounter, name, minutes, seconds);
                    exerciseList.add(ex);
                    createCardExercise(ex);
                    dialog.dismiss();
                } else
                    Toast.makeText(getApplicationContext(), "Set exercise name", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Set an acceptable duration", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    private void showExercise(){
        linearLayoutWorkout.removeAllViewsInLayout();
        for(Exercise ex : exerciseList){
            createCardExercise(ex);
        }
    }

    private void createCardExercise(Exercise ex){
        View card = getLayoutInflater().inflate(R.layout.card_show_exercise, null);
        TextView numExercise = card.findViewById(R.id.textNumberExercise);
        TextView nameExercise = card.findViewById(R.id.exerciseNameCard);
        TextView timeExercise = card.findViewById(R.id.TimerValueCard);
        numExercise.setText(String.valueOf(ex.getNum()));
        nameExercise.setText(ex.getName());
        timeExercise.setText(ex.getFormattedTimer());
        linearLayoutWorkout.addView(card);
    }
}