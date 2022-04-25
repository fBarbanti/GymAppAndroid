package com.example.newgymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ModifyPassword extends AppCompatActivity implements TextWatcher {

    private EditText currentPasswordField;
    private EditText newPasswordField;
    private Button btnDone;
    private ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);


        currentPasswordField = findViewById(R.id.current_password_modify_view);
        newPasswordField = findViewById(R.id.new_password_modify_view);
        btnDone = findViewById(R.id.done_button_modify_password);
        btnBack = findViewById(R.id.back_to_profile_f_password);
        btnDone.setEnabled(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        newPasswordField.addTextChangedListener(this);


        btnDone.setOnClickListener(view -> {
            if(!currentPasswordField.getText().toString().isEmpty())
                changePasswordFirebase();
        });

        btnBack.setOnClickListener(view -> startActivity(new Intent(ModifyPassword.this, ProfileActivity.class)));
    }

    private void changePasswordFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(user.getEmail()), currentPasswordField.getText().toString());

        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                user.updatePassword(newPasswordField.getText().toString()).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        startActivity(new Intent(ModifyPassword.this, ProfileActivity.class));
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Update failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String newPassword = charSequence.toString();

        btnDone.setTextColor(getResources().getColor(R.color.custom_gray));
        btnDone.setEnabled(false);

        if(newPassword.length() >= 6){
            if(!currentPasswordField.getText().toString().isEmpty()){
                btnDone.setTextColor(getResources().getColor(R.color.orange));
                btnDone.setEnabled(true);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}