package com.example.e_commerce_mart_asssign3;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvAddress, tvGender, tvPhone;
    private Button btnLogout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        tvName = view.findViewById(R.id.tv_name_value);
        tvEmail = view.findViewById(R.id.tv_email_value);
        tvAddress = view.findViewById(R.id.tv_address_value);
        tvGender = view.findViewById(R.id.tv_gender_value);
        tvPhone = view.findViewById(R.id.tv_phone_value);
        btnLogout = view.findViewById(R.id.btn_logout);

        if (user != null) {
            tvEmail.setText(user.getEmail());
            loadUserData(user.getUid());
        }

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserData(String uid) {
        mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    tvName.setText(snapshot.child("name").getValue(String.class));
                    tvAddress.setText(snapshot.child("address").getValue(String.class));
                    tvGender.setText(snapshot.child("gender").getValue(String.class));
                    tvPhone.setText(snapshot.child("phone").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
