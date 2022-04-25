package com.example.newgymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignInActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnSignIn;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText repeatPasswordField;
    private ProgressBar progress;
    private ScrollView scrollView;

    private FirebaseAuth fireAuth;
    private DatabaseReference dbReference;

//    final String DB_URL = "https://gymfirebaselogin-default-rtdb.europe-west1.firebasedatabase.app/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fireAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.login_text_button_signin_view);
        btnSignIn = findViewById(R.id.signin_button_signin_view);
        usernameField = findViewById(R.id.usernameText_signin_view);
        emailField = findViewById(R.id.emailText_signin_view);
        passwordField = findViewById(R.id.passwordText_signin_view);
        repeatPasswordField = findViewById(R.id.repeat_password_signin_view);
        progress = findViewById(R.id.progressBar_signin_view);
        scrollView = findViewById(R.id.layout_signin);

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        scrollView.setOnTouchListener(new OnSwipeTouchListener(SignInActivity.this){
            @SuppressLint("ClickableViewAccessibility")
            public void onSwipeRight() {
                startActivity(new Intent(SignInActivity.this, LoginActivity.class));
            }
        });
    }

    private void signIn() {
        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordRepeated = repeatPasswordField.getText().toString();
        if(username.isEmpty()) {
            usernameField.setError("Username can not be empty!");
        }
        if(email.isEmpty()) {
            emailField.setError("Email can not be empty!");
        }
        if(password.isEmpty()) {
            passwordField.setError("Password can not be empty");
        }
        if(passwordRepeated.isEmpty()) {
            repeatPasswordField.setError("Repeat the password, please");
        }
        else {
            if(!password.equals(passwordRepeated)) {
                Toast.makeText(SignInActivity.this, "Password not matching each other", Toast.LENGTH_SHORT).show();
            }
            else {
               register(email, password, username);
            }
        }
    }

    private void login() {
        startActivity(new Intent(SignInActivity.this, LoginActivity.class));
    }

    private void register(String email, String password, String username) {
        progress.setVisibility(View.VISIBLE);
        fireAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignInActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    FirebaseUser fireUser = fireAuth.getCurrentUser();
                    String fireUserId = fireUser.getUid();
                    dbReference = FirebaseDatabase.getInstance(FirebaseConfig.DB_URL).getReference("Users").child(fireUserId);

                    HashMap<String, String> hashMapToUpload = new HashMap<>();
                    hashMapToUpload.put("userId", fireUserId);
                    hashMapToUpload.put("username", username);
                    hashMapToUpload.put("email", email);
                    hashMapToUpload.put("imageUrl", "default");

                    dbReference.setValue(hashMapToUpload).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progress.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                startActivity(new Intent(SignInActivity.this, LoginActivity.class));
                            }
                            else {
                                Toast.makeText(SignInActivity.this, "Something gone wrong", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }
                else{
                    progress.setVisibility(View.GONE);
                    Toast.makeText(SignInActivity.this, "Registration failed"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}