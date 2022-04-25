package com.example.newgymapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth fireAuth;
    private Button btnLogin;
    private Button btnSignIn;
    private EditText emailField;
    private EditText passwordField;
    private ProgressBar progress;
    private ConstraintLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fireAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btn_login);
        btnSignIn = findViewById(R.id.signin_text_button);
        emailField = findViewById(R.id.emailText);
        passwordField = findViewById(R.id.passwordText);
        progress = findViewById(R.id.progressBar);
        layout = findViewById(R.id.layout_login);

    }

    @Override
    protected void onStart() {
        super.onStart();
        progress.setVisibility(View.GONE);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onResume() {
        super.onResume();

        btnLogin.setOnClickListener(view -> login());

        btnSignIn.setOnClickListener(view -> signIn());

        layout.setOnTouchListener(new OnSwipeTouchListener(LoginActivity.this) {
            public void onSwipeLeft() {
                startActivity(new Intent(LoginActivity.this, SignInActivity.class));
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        fireAuth.signOut();
    }

    private void login() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        if(email.isEmpty()) {
            emailField.setError("Email can not be empty!");
        }
        if(password.isEmpty()) {
            passwordField.setError("Password can not be empty");
        }
        else {
            progress.setVisibility(View.VISIBLE);
            fireAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progress.setVisibility(View.GONE);
                Log.i("LOGIN", email);
                if(task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this, "Login failed" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void signIn() {
        startActivity(new Intent(LoginActivity.this, SignInActivity.class));
    }

}