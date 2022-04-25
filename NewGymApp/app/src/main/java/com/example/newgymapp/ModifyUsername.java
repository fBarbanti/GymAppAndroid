package com.example.newgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ModifyUsername extends AppCompatActivity implements TextWatcher {

    private TextView currentUsernameField;
    private EditText updateUsernameField;
    private Button btnDone;
    private ImageView btnBack;

    private DatabaseReference dbReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_username);

        currentUsernameField = findViewById(R.id.current_username_modify_username_view);
        updateUsernameField = findViewById(R.id.usernameText_modify_view);
        btnBack = findViewById(R.id.back_to_profile_f_username);
        btnDone = findViewById(R.id.done_button_modify_username);
        btnDone.setEnabled(false);


        user = FirebaseAuth.getInstance().getCurrentUser();
        dbReference = FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference("Users").child(user.getUid()).child("username");

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateCurrentUsernameField();

        updateUsernameField.addTextChangedListener(this);

        btnBack.setOnClickListener(view -> startActivity(new Intent(ModifyUsername.this, ProfileActivity.class)));

        btnDone.setOnClickListener(view -> modifyUsername());
    }

    private void modifyUsername() {
        assert user != null;
        String newUsername = updateUsernameField.getText().toString();
        if(!newUsername.isEmpty()) {
            dbReference.setValue(newUsername);
            startActivity(new Intent(ModifyUsername.this, ProfileActivity.class));
        }
        else
            Toast.makeText(getApplicationContext(), "ERROR! You provide an empty username!", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentUsernameField() {
        assert user != null;
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                String formattedString = "Your current username is " + "<b>" + username + "</b>" + ".\n To change it, fill in the field below!";
                currentUsernameField.setText(Html.fromHtml(formattedString));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String formettedString = "You haven't set any username. Please fill in the field below!";
                currentUsernameField.setText(Html.fromHtml(formettedString));
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(!charSequence.toString().isEmpty()){
            btnDone.setTextColor(getResources().getColor(R.color.orange));
            btnDone.setEnabled(true);
        }
        else {
            btnDone.setTextColor(getResources().getColor(R.color.custom_gray));
            btnDone.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}