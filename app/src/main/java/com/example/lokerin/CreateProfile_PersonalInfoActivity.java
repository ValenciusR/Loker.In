package com.example.lokerin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile_PersonalInfoActivity extends AppCompatActivity {

    EditText nameET, phoneET, locationET, ageET, genderET;
    Button nextBtn;

    FirebaseApp firebaseApp;
    FirebaseAuth mAuth ;
    FirebaseDatabase firebaseDatabase ;
    DatabaseReference userReference;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile_personal_info);

        nameET = findViewById(R.id.input_name_personalInfoPage);
        phoneET = findViewById(R.id.input_phone_personalInfoPage);
        locationET = findViewById(R.id.input_location_personalInfoPage);
        ageET = findViewById(R.id.input_age_personalInfoPage);
        genderET = findViewById(R.id.input_gender_personalInfoPage);
        nextBtn = findViewById(R.id.btn_next_personalInfoPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        nextBtn.setOnClickListener(view -> {
            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", nameET.getText().toString());
            updates.put("phoneNumber", phoneET.getText().toString());
            updates.put("location", locationET.getText().toString());
            updates.put("age", Integer.parseInt(ageET.getText().toString()));
            updates.put("gender", genderET.getText().toString());
            userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    Intent loginIntent = new Intent(this, CreateProfile_AboutMeActivity.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    // Handle the error here
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}