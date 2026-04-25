package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button actionButton;
    private ProgressBar progressBar;
    private TextView tabLogin, tabSignup;
    private LinearLayout signupFields;

    private FirebaseAuth mAuth;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupClickListeners();
        setupTabSwitching();
    }

    private void initViews() {
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        actionButton = findViewById(R.id.btn_action);
        progressBar = findViewById(R.id.login_progress);
        
        tabLogin = findViewById(R.id.tab_login);
        tabSignup = findViewById(R.id.tab_signup);
        signupFields = findViewById(R.id.signup_fields);
    }

    private void setupClickListeners() {
        actionButton.setOnClickListener(v -> {
            if (isLoginMode) {
                performLogin();
            } else {
                performSignup();
            }
        });

        findViewById(R.id.iv_password_toggle).setOnClickListener(v -> togglePasswordVisibility(passwordEditText, (ImageView) v));
        findViewById(R.id.iv_confirm_password_toggle).setOnClickListener(v -> togglePasswordVisibility(confirmPasswordEditText, (ImageView) v));
    }

    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon) {
        if (editText.getTransformationMethod() instanceof android.text.method.PasswordTransformationMethod) {
            editText.setTransformationMethod(android.text.method.HideReturnsTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.ic_visibility);
        } else {
            editText.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.ic_visibility_off);
        }
        editText.setSelection(editText.getText().length());
    }

    private void setupTabSwitching() {
        tabLogin.setOnClickListener(v -> {
            isLoginMode = true;
            updateTabUI();
        });

        tabSignup.setOnClickListener(v -> {
            isLoginMode = false;
            updateTabUI();
        });
    }

    private void updateTabUI() {
        if (isLoginMode) {
            tabLogin.setBackgroundResource(R.drawable.tab_selected_bg);
            tabLogin.setTextColor(getResources().getColor(R.color.text_main));
            tabLogin.setTypeface(null, android.graphics.Typeface.BOLD);
            
            tabSignup.setBackground(null);
            tabSignup.setTextColor(getResources().getColor(R.color.text_sub));
            tabSignup.setTypeface(null, android.graphics.Typeface.NORMAL);
            
            signupFields.setVisibility(View.GONE);
            actionButton.setText("Log In");
        } else {
            tabSignup.setBackgroundResource(R.drawable.tab_selected_bg);
            tabSignup.setTextColor(getResources().getColor(R.color.text_main));
            tabSignup.setTypeface(null, android.graphics.Typeface.BOLD);
            
            tabLogin.setBackground(null);
            tabLogin.setTextColor(getResources().getColor(R.color.text_sub));
            tabLogin.setTypeface(null, android.graphics.Typeface.NORMAL);
            
            signupFields.setVisibility(View.VISIBLE);
            actionButton.setText("Sign Up");
        }
    }

    private void performLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void performSignup() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        showProgress(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        navigateToProfileActivity();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        actionButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToProfileActivity() {
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Usually we'd check if profile is complete here, but for now just go to main
            navigateToMainActivity();
        }
    }
}
