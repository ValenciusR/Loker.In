package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile_Skills extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private EditText etSkill, etSkillDesc;
    private Button btnNext;
    private TextView skipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile_skills);

        etSkill = findViewById(R.id.input_skill_skillsPage);
        etSkillDesc = findViewById(R.id.input_skillDesc_skillsPage);
        btnNext = findViewById(R.id.btn_next_skillsPage);
        skipText = findViewById(R.id.text_skip_skillsPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btnNext.setOnClickListener(view -> {
            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("skill", etSkill.getText().toString());
            updates.put("skillDesc", etSkillDesc.getText().toString());
            userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    Intent loginIntent = new Intent(this, CreateProfile_WorkExperience.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    // Handle the error here
                    Toast.makeText(this, "Gagal menyimpan data user", Toast.LENGTH_SHORT).show();
                }
            });
        });

        skipText.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, CreateProfile_WorkExperience.class);
            startActivity(loginIntent);
            finish();
        });
    }
}