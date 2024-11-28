package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePelangganActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView tvName, tvEmail, tvPhone, tvLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_pelanggan);
        ImageView backButton = findViewById(R.id.btn_back_toolbar);
        AppCompatButton btnEditProfile = findViewById(R.id.acb_editProfile_profilePelangganPage);
        AppCompatButton btnLogOut = findViewById(R.id.acb_logOut_profilePelangganPage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Handle back button on click event
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(ProfilePelangganActivity.this, PelangganMainActivity.class));
                finish();
            }
        });

//        Handle edit profile button on click event
        btnEditProfile.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(ProfilePelangganActivity.this, EditProfilePelangganActivity.class));
                finish();
            }
        });

//        Handle log out button on click event
        btnLogOut.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfilePelangganActivity.this, LoginActivity.class));
                finish();
            }
        });

//        Set user profile based on user logged in value
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        tvName = findViewById(R.id.tv_name_profilePelangganPage);
        tvPhone = findViewById(R.id.tv_phone_profilePelangganPage);
        tvLocation = findViewById(R.id.tv_location_profilePelangganPage);
        tvEmail = findViewById(R.id.tv_email_profilePelangganPage);

        if(user != null) {
            String userId = user.getUid(); // Unique user ID

//            Set user profile based on logged in user (gw gk tau caranya gimana, bantu yak ming :) );

        }


    }

    private void getUserData() {

    }
}