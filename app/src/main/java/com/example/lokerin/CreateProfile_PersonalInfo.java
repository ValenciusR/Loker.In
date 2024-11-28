package com.example.lokerin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile_PersonalInfo extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private EditText etName, etPhone, etLocation, etAge, etGender;
    private Button btnNext;

    private AutoCompleteTextView etGender;
    private ArrayAdapter<String> adapterItems;

    private String[] item = {"Male", "Female"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile_personal_info);

        etName = findViewById(R.id.input_name_personalInfoPage);
        etPhone = findViewById(R.id.input_phone_personalInfoPage);
        etLocation = findViewById(R.id.input_location_personalInfoPage);
        etAge = findViewById(R.id.input_age_personalInfoPage);
        etGender = findViewById(R.id.input_gender_personalInfoPage);
        btnNext = findViewById(R.id.btn_next_personalInfoPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        etGender = findViewById(R.id.input_gender_personalInfoPage);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);

        etGender.setAdapter(adapterItems);

        btnNext.setOnClickListener(view -> {
            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", etName.getText().toString());
            updates.put("phoneNumber", etPhone.getText().toString());
            updates.put("location", etLocation.getText().toString());
            updates.put("age", Integer.parseInt(etAge.getText().toString()));
            updates.put("gender", etGender.getText().toString());
            userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    Intent loginIntent = new Intent(this, CreateProfile_AboutMe.class);
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