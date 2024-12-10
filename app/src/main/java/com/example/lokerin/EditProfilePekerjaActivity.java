package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditProfilePekerjaActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageView btnBack, ivProfileNavbar;
    private EditText etName, etPhone, etLocation, etJob, etJobDescription;
    private AppCompatButton btnSaveChanges;
    private Boolean isValid;
    private TextView tvPageTitle, tvNameError, tvPhoneError, tvLocationError, tvJobError, tvJobDescriptionError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_pekerja);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivProfileNavbar = findViewById(R.id.iv_profile2_editProfilePekerjaPage);
        etName = findViewById(R.id.et_name_editProfilePekerjaPage);
        etPhone = findViewById(R.id.et_phone_editProfilePekerjaPage);
        etLocation = findViewById(R.id.et_location_editProfilePekerjaPage);
        etJob = findViewById(R.id.et_job_editProfilePekerjaPage);
        etJobDescription = findViewById(R.id.et_jobDescription_editProfilePekerjaPage);
        btnSaveChanges = findViewById(R.id.btn_saveChanges_editProfilePekerjaPage);

        tvNameError = findViewById(R.id.tv_nameError_editProfilePekerjaPage);
        tvPhoneError = findViewById(R.id.tv_phoneError_editProfilePekerjaPage);
        tvLocationError = findViewById(R.id.tv_locationError_editProfilePekerjaPage);
        tvJobError = findViewById(R.id.tv_jobError_editProfilePekerjaPage);
        tvJobDescriptionError = findViewById(R.id.tv_jobDescriptionError_editProfilePekerjaPage);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfilePage();
            }
        });
        tvPageTitle.setText("Edit Profil");
        ivProfileNavbar.setImageResource(R.drawable.settings_icon);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                etName.setText(user.getName());
                etJob.setText(user.getJob());
                etJobDescription.setText(user.getJobDesc());
                etLocation.setText(user.getLocation());
                etPhone.setText(user.getPhoneNumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
//                check for if fields are empty
                if (etName.getText().toString().trim().length() < 1) {
                    etName.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvNameError.setText("Nama harus diisi!");
                    isValid = false;
                } else {
                    etName.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvNameError.setText("");
                }

//                check phone length
                if (etPhone.getText().toString().trim().length() < 10) {
                    etPhone.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvPhoneError.setText("Nomor telepon minimal memiliki 10 angka!");
                    isValid = false;
                }
                else if (!etPhone.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    etPhone.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvPhoneError.setText("Nomor telepon hanya boleh memiliki angka!");
                    isValid = false;
                }
                else {
                    etPhone.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvPhoneError.setText("");
                }

//                Check if location is empty
                if (etLocation.getText().toString().trim().length() < 1) {
                    etLocation.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvLocationError.setText("Lokasi harus diisi!");
                    isValid = false;
                } else {
                    etLocation.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvLocationError.setText("");
                }

//                Check if job is empty
                if (etJob.getText().toString().trim().length() < 1) {
                    etJob.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvJobError.setText("Profesi harus diisi!");
                    isValid = false;
                } else {
                    etJob.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvJobError.setText("");
                }

//                Check if job description is empty
                if (etJobDescription.getText().toString().trim().length() < 20) {
                    etJobDescription.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvJobDescriptionError.setText("Deskripsi profesi minimal berisi 20 huruf!");
                    isValid = false;
                } else {
                    etJobDescription.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvJobDescriptionError.setText("");
                }

                if(isValid) {
                    DatabaseReference userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", etName.getText().toString());
                    updates.put("phoneNumber", etPhone.getText().toString());
                    updates.put("location", etLocation.getText().toString());
                    updates.put("job", etJob.getText().toString());
                    updates.put("jobDesc", etJobDescription.getText().toString());
                    userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Toast.makeText(EditProfilePekerjaActivity.this, "Profil berhasil di ubah!", Toast.LENGTH_SHORT).show();
                            startProfilePage();
                        }
                    });


                }
            }
        });
    }

    private void startProfilePage() {
        startActivity(new Intent(EditProfilePekerjaActivity.this, ProfilePekerjaActivity.class));
        finish();
    }
}