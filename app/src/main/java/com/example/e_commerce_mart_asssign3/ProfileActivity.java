package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText fullNameEditText, addressEditText, countryCodeEditText, phoneNumberEditText;
    private Spinner countrySpinner, accountTypeSpinner;
    private TextView btnMale, btnFemale;
    private CheckBox termsCheckBox;
    private Button saveProfileButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private String selectedGender = "Male";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        initViews();
        setupSpinners();
        setupClickListeners();
    }

    private void initViews() {
        fullNameEditText = findViewById(R.id.et_full_name);
        addressEditText = findViewById(R.id.et_address);
        countrySpinner = findViewById(R.id.spinner_country);
        accountTypeSpinner = findViewById(R.id.spinner_account_type);
        countryCodeEditText = findViewById(R.id.et_country_code);
        phoneNumberEditText = findViewById(R.id.et_phone);
        btnMale = findViewById(R.id.btn_male);
        btnFemale = findViewById(R.id.btn_female);
        termsCheckBox = findViewById(R.id.cb_agree);
        saveProfileButton = findViewById(R.id.btn_save_profile);
        progressBar = findViewById(R.id.profile_progress);
    }

    private void setupSpinners() {
        // Country Spinner
        String[] countries = {"United States", "United Kingdom", "Canada", "Australia", 
                            "Pakistan", "India", "China", "Japan", "Germany", "France"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_dropdown_item_1line, countries);
        countrySpinner.setAdapter(countryAdapter);

        // Account Type Spinner
        String[] accountTypes = {"Buyer", "Seller"};
        ArrayAdapter<String> accountTypeAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, accountTypes);
        accountTypeSpinner.setAdapter(accountTypeAdapter);
    }

    private void setupClickListeners() {
        btnMale.setOnClickListener(v -> {
            selectedGender = "Male";
            updateGenderUI();
        });

        btnFemale.setOnClickListener(v -> {
            selectedGender = "Female";
            updateGenderUI();
        });

        saveProfileButton.setOnClickListener(v -> saveProfile());
        
        countryCodeEditText.setText("+1");
    }

    private void updateGenderUI() {
        if (selectedGender.equals("Male")) {
            btnMale.setBackgroundResource(R.drawable.tab_selected_bg);
            btnMale.setTextColor(getResources().getColor(R.color.text_main));
            btnMale.setTypeface(null, android.graphics.Typeface.BOLD);
            
            btnFemale.setBackgroundResource(R.drawable.field_bg_rounded);
            btnFemale.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white)));
            btnFemale.setTextColor(getResources().getColor(R.color.text_sub));
            btnFemale.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            btnFemale.setBackgroundResource(R.drawable.tab_selected_bg);
            btnFemale.setTextColor(getResources().getColor(R.color.text_main));
            btnFemale.setTypeface(null, android.graphics.Typeface.BOLD);
            
            btnMale.setBackgroundResource(R.drawable.field_bg_rounded);
            btnMale.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white)));
            btnMale.setTextColor(getResources().getColor(R.color.text_sub));
            btnMale.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    private void saveProfile() {
        if (!validateInput()) {
            return;
        }

        showProgress(true);

        String fullName = fullNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String country = countrySpinner.getSelectedItem().toString();
        String accountType = accountTypeSpinner.getSelectedItem().toString();
        String countryCode = countryCodeEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", fullName);
        userData.put("address", address);
        userData.put("country", country);
        userData.put("accountType", accountType.toLowerCase());
        userData.put("phone", countryCode + " " + phoneNumber);
        userData.put("gender", selectedGender);
        userData.put("email", currentUser.getEmail());
        userData.put("uid", currentUser.getUid());

        mDatabase.child(currentUser.getUid()).setValue(userData)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error: " + task.getException().getMessage(), 
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString().trim())) {
            fullNameEditText.setError("Required");
            return false;
        }
        if (TextUtils.isEmpty(phoneNumberEditText.getText().toString().trim())) {
            phoneNumberEditText.setError("Required");
            return false;
        }
        if (!termsCheckBox.isChecked()) {
            Toast.makeText(this, "Please agree to terms", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        saveProfileButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
