package com.example.lokerin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile_AboutMe extends AppCompatActivity {

    EditText aboutMeET;
    Button nextBtn;
    TextView skipText;

    FirebaseApp firebaseApp;
    FirebaseAuth mAuth ;
    FirebaseDatabase firebaseDatabase ;
    DatabaseReference userReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile_about_me);

        aboutMeET = findViewById(R.id.input_aboutMe_aboutMePage);
        nextBtn = findViewById(R.id.btn_next_aboutMePage);
        skipText = findViewById(R.id.text_skip_aboutMePage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        nextBtn.setOnClickListener(view -> {
            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("aboutMe", aboutMeET.getText().toString());
            userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    Intent loginIntent = new Intent(this, CreateProfile_Skills.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    // Handle the error here
                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                }
            });
        });

        skipText.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, CreateProfile_Skills.class);
            startActivity(loginIntent);
            finish();
        });

    }
}