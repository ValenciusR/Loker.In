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

import org.checkerframework.checker.units.qual.A;

import java.util.HashMap;
import java.util.Map;

public class CreateProfile_PersonalInfo extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private EditText etName, etPhone, etAge;
    private Button btnNext;
    private String type;
    private TextView tvNameError, tvPhoneError, tvLocationError, tvAgeError, tvGenderError;

    private AutoCompleteTextView etLocation;
    private ArrayAdapter<String> adapterItems, adapterItemsLocation;

    private Spinner spnGender;
    private ArrayAdapter<CharSequence> genderAdapter;

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
//        etGender = findViewById(R.id.et_gender_createProfilePersonalInfoPage);
        btnNext = findViewById(R.id.acb_next_createProfilePersonalInfoPage);
        spnGender = findViewById(R.id.spinner_gender_createProfilePersonalInfoPage);


        tvNameError = findViewById(R.id.tv_nameError_createProfilePersonalInfoPage);
        tvPhoneError = findViewById(R.id.tv_phoneError_createProfilePersonalInfoPage);
        tvLocationError = findViewById(R.id.tv_locationError_createProfilePersonalInfoPage);
        tvAgeError = findViewById(R.id.tv_ageError_createProfilePersonalInfoPage);
        tvGenderError = findViewById(R.id.tv_genderError_createProfilePersonalInfoPage);

        genderAdapter = ArrayAdapter.createFromResource(CreateProfile_PersonalInfo.this,
                R.array.gender, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnGender.setAdapter(genderAdapter);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        adapterItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        String[] provinces = getResources().getStringArray(R.array.province);
        adapterItemsLocation = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, provinces);
        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String tempGender = "";
                if(user != null){
                    type = snapshot.child("type").getValue().toString();
                    etName.setText(snapshot.child("name").getValue().toString());
                    etPhone.setText(snapshot.child("phoneNumber").getValue().toString());
                    etLocation.setText(snapshot.child("location").getValue().toString());
                    etAge.setText(snapshot.child("age").getValue().toString());
                    tempGender = snapshot.child("gender").getValue().toString();
                }

                if(etAge.getText().toString().equals("0")) etAge.setText("");
                
                int index = -1;
                for (int i = 0; i < genderAdapter.getCount(); i++) {
                    if (genderAdapter.getItem(i).equals(tempGender)) {
                        index = i;
                        break;
                    }
                }

                // Set the Spinner selection if the value is found
                if (index != -1) {
                    spnGender.setSelection(index);
                }
//                etGender.setText(snapshot.child("gender").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateProfile_PersonalInfo.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });

//        etGender.setAdapter(adapterItems);
//        etGender.setOnClickListener(v -> {
//            etGender.showDropDown();
//        });
//        etGender.setOnFocusChangeListener((v, hasFocus) -> etGender.showDropDown());

        etLocation.setAdapter(adapterItemsLocation);
        etLocation.setOnClickListener(v -> {
            etLocation.showDropDown();
        });
        etLocation.setOnFocusChangeListener((v, hasFocus) -> etLocation.showDropDown());
        btnNext.setOnClickListener(view -> {
            Boolean isValid = true;

            String nameInput = etName.getText().toString();
            String phoneInput = etPhone.getText().toString();
            String locationInput = etLocation.getText().toString();
            String ageInput = etAge.getText().toString();
//            String genderInput = etGender.getText().toString();
            
            if (nameInput.isEmpty()) {
                etName.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvNameError.setText("Nama harus diisi!");
                isValid = false;
            } else {
                for (char c : nameInput.toCharArray()) {
                    if (!Character.isLetter(c) && !Character.isWhitespace(c)) {
                        etName.setBackgroundResource(R.drawable.shape_rounded_red_border);
                        tvNameError.setText("Nama hanya boleh diisi dengan huruf!");
                        isValid = false;
                        break;
                    }
                }
                if(isValid) {
                    etName.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvNameError.setText("");
                }
            }

            if (phoneInput.length() < 10) {
                etPhone.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvPhoneError.setText("Nomor telepon minimal memiliki 10 angka!");
                isValid = false;
            } else if (!phoneInput.matches("^08\\d{8,11}$")) {
                etPhone.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvPhoneError.setText("Format nomor telepon tidak valid! Gunakan format: 08XXXXXXXXXX");
                isValid = false;
            } else {
                etPhone.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                tvPhoneError.setText("");
            }


            if (locationInput.isEmpty()) {
                etLocation.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvLocationError.setText("Lokasi harus diisi!");
                isValid = false;
            } else {
                etLocation.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                tvLocationError.setText("");
            }

            if (ageInput.isEmpty()) {
                etAge.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvAgeError.setText("Umur harus diisi!");
                isValid = false;
            }
            else if (Integer.valueOf(etAge.getText().toString()) < 18) {
                etAge.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvAgeError.setText("Umur minimal 18 tahun!");
                isValid = false;
            }
            else if (!ageInput.matches("\\d+(?:\\.\\d+)?")) {
                etAge.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvAgeError.setText("Umur hanya boleh diisi dengan angka!");
                isValid = false;
            }
            else {
                etAge.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                tvAgeError.setText("");
            }

            if (spnGender.getSelectedItem().toString().equals("Pilih Jenis")) {
                spnGender.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvGenderError.setText("Jenis kelamin harus dipilih!");
                isValid = false;
            }
            else {
                spnGender.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                tvGenderError.setText("");
            }

            if(isValid) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", etName.getText().toString());
                updates.put("nameLowerCase", etName.getText().toString().toLowerCase());
                updates.put("phoneNumber", etPhone.getText().toString());
                updates.put("location", etLocation.getText().toString());
                updates.put("age", Integer.parseInt(etAge.getText().toString()));
                updates.put("gender", spnGender.getSelectedItem().toString());
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
            }
        });

    }
}