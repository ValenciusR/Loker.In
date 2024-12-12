package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PelangganProfileActivity extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private TextView tvPageTitle, tvName, tvEmail, tvPhone, tvLocation;
    private ImageView btnBack;
    private AppCompatButton btnEditProfile, btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pelanggan_profile);

        tvName = findViewById(R.id.tv_name_profilePelangganPage);
        tvPhone = findViewById(R.id.tv_phone_profilePelangganPage);
        tvLocation = findViewById(R.id.tv_location_profilePelangganPage);
        tvEmail = findViewById(R.id.tv_email_profilePelangganPage);
        getUserData();

        btnEditProfile = findViewById(R.id.acb_editProfile_profilePelangganPage);
        btnLogOut = findViewById(R.id.acb_logOut_profilePelangganPage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Profil");

        btnEditProfile.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(PelangganProfileActivity.this, EditProfilePelangganActivity.class));
                finish();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PelangganProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
/*
//        Set user profile based on user logged in value
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        if(user != null) {
            String userId = user.getUid(); // Unique user ID

//            Set user profile based on logged in user (gw gk tau caranya gimana, bantu yak ming :) );

        }

*/
    }

    private void getUserData() {
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue().toString();
                String userPhone = dataSnapshot.child("phoneNumber").getValue().toString();
                String userLocation = dataSnapshot.child("location").getValue().toString();
                String userEmail = dataSnapshot.child("email").getValue().toString();

                tvName.setText(userName);
                tvPhone.setText(userPhone);
                tvLocation.setText(userLocation);
                tvEmail.setText(userEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void backPage() {
        startActivity(new Intent(PelangganProfileActivity.this, PelangganMainActivity.class));
        finish();
    }
}