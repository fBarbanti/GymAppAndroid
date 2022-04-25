package com.example.newgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import worker.Availability;
import worker.PersonalTrainer;
import worker.Workers;


public class CallPersonalTrainer extends AppCompatActivity {


    final String DB_URL = "https://gymfirebaselogin-default-rtdb.europe-west1.firebasedatabase.app/";
    private DatabaseReference dbRef;

    private TextView workersView;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_personal_trainer);

        workersView = findViewById(R.id.workers);
        workersView.setMovementMethod(new ScrollingMovementMethod());

        btnBack = findViewById(R.id.back_to_home_f_pt);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CallPersonalTrainer.this, HomeActivity.class));
            }
        });

        dbRef = FirebaseDatabase.getInstance(DB_URL).getReference("Pt");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Workers w = createWorkers(snapshot);
                updateGui(w);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private Workers createWorkers(DataSnapshot snapshot){
        Workers workers = new Workers();

        if(snapshot.exists()){
            for(DataSnapshot ptTemp : snapshot.getChildren()){
                String name = ptTemp.child("name").getValue(String.class);
                String surname = ptTemp.child("surname").getValue(String.class);
                String phone = ptTemp.child("phone").getValue(String.class);
                PersonalTrainer pt = new PersonalTrainer(name, surname, phone);

                for(DataSnapshot avTempOne : ptTemp.getChildren()){
                    if(avTempOne.getKey().equals("avaiable")){
                        for(DataSnapshot avTemp : avTempOne.getChildren()){

                            int endHour = avTemp.child("endHour").getValue(Integer.class);
                            int startHour = avTemp.child("startHour").getValue(Integer.class);
                            int startMinute = avTemp.child("startMinute").getValue(Integer.class);
                            int endMinute = avTemp.child("endMinute").getValue(Integer.class);
                            String day = avTemp.child("day").getValue(String.class);
                            Availability availability = new Availability(day, startHour, startMinute, endHour, endMinute);

                            pt = pt.plus(availability);
                        }
                    }
                }
                workers = workers.plus(pt);
            }
        }
        return workers;
    }

    private void updateGui(Workers workers){
        workersView.setText(workers.toString());

    }

}