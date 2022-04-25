package com.example.newgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements WorkoutRecyclerViewInterface{

    private ImageButton btnAdd;
    private ImageButton btnSignOut;
    private ImageButton btnProfile;
    private ImageButton btnSearchPt;
    private RecyclerView recyclerView;

    private DatabaseReference dbRef;

    private ArrayList<Workout> workoutArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        workoutArrayList = new ArrayList<>();


        recyclerView = findViewById(R.id.recycler_view);
        btnAdd = findViewById(R.id.btn_add_workouts);
        btnSignOut = findViewById(R.id.btn_sign_out_home_view);
        btnProfile = findViewById(R.id.btn_profile_home_view);
        btnSearchPt = findViewById(R.id.btn_search_pt);

        FirebaseAuth fireAuth = FirebaseAuth.getInstance();
        FirebaseUser fireUser = fireAuth.getCurrentUser();

        // If user is not logged in
        if(fireUser == null)
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));

        assert fireUser != null;
        dbRef = FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference("Users").child(fireUser.getUid()).child("workouts");
    }

    @Override
    protected void onStart() {
        super.onStart();

        showWorkout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        btnSignOut.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, LoginActivity.class)));

        btnProfile.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        btnAdd.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, CreateWorkout.class)));

        btnSearchPt.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, CallPersonalTrainer.class)));
    }

    private void showWorkout() {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot work : snapshot.getChildren()){
                        // Instance workout
                        Workout newWorkout = new Workout();

                        // Instance exercise array list
                        ArrayList<Exercise> exerciseArrayList = new ArrayList<>();

                        // For each exercise
                        for(DataSnapshot exer : work.getChildren()) {
                            Log.d("PROVA", exer.getKey());
                            if(exer.getKey().equals("Type"))
                                newWorkout.setType(exer.getValue(String.class));
                            else {
                                HashMap<String, String> mapEx = (HashMap<String, String>) exer.getValue();
                                assert mapEx != null;
                                Exercise newExerc = new Exercise(
                                        Integer.parseInt(Objects.requireNonNull(exer.getKey())),
                                        mapEx.get("name"),
                                        Integer.parseInt(Objects.requireNonNull(mapEx.get("min"))),
                                        Integer.parseInt(Objects.requireNonNull(mapEx.get("sec"))));
                                exerciseArrayList.add(newExerc);
                            }
                        }

                        newWorkout.setName(work.getKey());
                        newWorkout.setExerciseArrayList(exerciseArrayList);
                        newWorkout.updateDuration();
                        Log.d("PROVA", "WORK "+newWorkout.getName()+": "+newWorkout.getMinTot());

                        // Add the workout to Workout List
                        workoutArrayList.add(newWorkout);

                    }
                    // Create the adapter
                    WorkoutRecyclerViewAdapter adapter = new WorkoutRecyclerViewAdapter(getApplicationContext(), workoutArrayList, HomeActivity.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onWorkoutClick(int position) {
        Intent intent = new Intent(HomeActivity.this, PlayWorkout.class);
        intent.putExtra("Workout", workoutArrayList.get(position));
        intent.putExtra("ExerciseList", (ArrayList<? extends Parcelable>) workoutArrayList.get(position).getExerciseArrayList());
        startActivity(intent);
    }
}