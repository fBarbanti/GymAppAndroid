package com.example.newgymapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIMEOUT = 2000;
    private static int START_OFFSET_ANIMATION = 500;
    private FirebaseAuth fireAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // User logout
        fireAuth = FirebaseAuth.getInstance();
        fireAuth.signOut();

        TextView gymStyleText = findViewById(R.id.gymStyleText);
        ImageView logo = findViewById(R.id.logo);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(START_OFFSET_ANIMATION);
        fadeOut.setDuration(SPLASH_SCREEN_TIMEOUT);

        gymStyleText.setAnimation(fadeOut);
        logo.setAnimation(fadeOut);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN_TIMEOUT);

    }


}