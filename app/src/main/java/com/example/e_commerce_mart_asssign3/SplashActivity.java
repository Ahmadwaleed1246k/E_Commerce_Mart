package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        
        View onboardingImage = findViewById(R.id.onboarding_image);
        View textContainer = findViewById(R.id.text_container);
        Button btnGetStarted = findViewById(R.id.btn_get_started);

        // Load and start animation
        android.view.animation.Animation anim = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        onboardingImage.startAnimation(anim);
        textContainer.startAnimation(anim);
        btnGetStarted.startAnimation(anim);
        
        setupClickListeners(btnGetStarted);
    }

    private void setupClickListeners(Button btnGetStarted) {
        btnGetStarted.setOnClickListener(v -> {
            // Note: If you are already logged in, this will go to MainActivity.
            // If you want to see the Login screen, click Logout in the Home screen.
            checkUserStatus();
        });
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, navigate to Home
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            // No user signed in, navigate to Login/Signup
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
