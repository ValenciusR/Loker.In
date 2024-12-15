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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile_PersonalInfo extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private EditText etName, etPhone, etLocation, etAge;
    private Button btnNext;
    private String type;

    private AutoCompleteTextView etGender;
    private ArrayAdapter<String> adapterItems;

    private String[] item = {"Laki-Laki", "Perempuan"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(CreateProfile_PersonalInfo.this);
        setContentView(R.layout.activity_create_profile_personal_info);

        etName = findViewById(R.id.et_name_createProfilePersonalInfoPage);
        etPhone = findViewById(R.id.et_phone_createProfilePersonalInfoPage);
        etLocation = findViewById(R.id.et_location_createProfilePersonalInfoPage);
        etAge = findViewById(R.id.et_age_createProfilePersonalInfoPage);
        etGender = findViewById(R.id.et_gender_createProfilePersonalInfoPage);
        btnNext = findViewById(R.id.acb_next_createProfilePersonalInfoPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        adapterItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                type = snapshot.child("type").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateProfile_PersonalInfo.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });

        etGender.setAdapter(adapterItems);
        etGender.setOnClickListener(v -> {
            etGender.showDropDown();
        });
        etGender.setOnFocusChangeListener((v, hasFocus) -> etGender.showDropDown());
        btnNext.setOnClickListener(view -> {
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", etName.getText().toString());
            updates.put("phoneNumber", etPhone.getText().toString());
            updates.put("location", etLocation.getText().toString());
            updates.put("age", Integer.parseInt(etAge.getText().toString()));
            updates.put("gender", etGender.getText().toString());
            userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    if(type.equals("pelanggan")) {
                        startActivity(new Intent(this, PelangganMainActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(this, CreateProfile_AboutMe.class));
                    }
                } else {
                    // Handle the error here
                    Toast.makeText(this, "Gagal menyimpan data user", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}