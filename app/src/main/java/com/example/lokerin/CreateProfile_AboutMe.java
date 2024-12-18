package com.example.lokerin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile_AboutMe extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private EditText etAboutMe;
    private Button btnNext;
    private TextView skipText, tvAboutMeError;
    private String type;
    private ImageView btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile_about_me);

        etAboutMe = findViewById(R.id.input_aboutMe_aboutMePage);
        btnNext = findViewById(R.id.btn_next_aboutMePage);
        skipText = findViewById(R.id.text_skip_aboutMePage);
        btnBack = findViewById(R.id.iv_back_aboutMePage);
        tvAboutMeError = findViewById(R.id.tv_aboutMeError_aboutMePage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                type = snapshot.child("type").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateProfile_AboutMe.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateProfile_PersonalInfo.class));
            finish();
        });

        btnNext.setOnClickListener(view -> {
            Boolean isValid = true;
            if (etAboutMe.getText().toString().trim().length() < 20) {
                etAboutMe.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvAboutMeError.setText("Deskripsi minimal berisi 20 huruf!");
                isValid = false;
            } else {
                etAboutMe.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                tvAboutMeError.setText("");
            }

            if(isValid) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("aboutMe", etAboutMe.getText().toString());
                userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        if(type.equals("pelanggan")){
                            Intent loginIntent = new Intent(this, PelangganMainActivity.class);
                            startActivity(loginIntent);
                            finish();
                        }else{
                            Intent loginIntent = new Intent(this, CreateProfile_Skills.class);
                            startActivity(loginIntent);
                            finish();
                        }
                    } else {
                        // Handle the error here
                        Toast.makeText(this, "Gagal menyimpan data user", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        skipText.setOnClickListener(view -> {
            if(type.equals("pelanggan")){
                Intent loginIntent = new Intent(this, PelangganMainActivity.class);
                startActivity(loginIntent);
            }else{
                Intent loginIntent = new Intent(this, CreateProfile_Skills.class);
                startActivity(loginIntent);
            }
        });

    }
}