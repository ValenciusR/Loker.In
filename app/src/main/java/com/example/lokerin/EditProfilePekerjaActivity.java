package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditProfilePekerjaActivity extends AppCompatActivity {

    private ImageView btnBack, ivProfileNavbar;
    private EditText etName, etPhone, etLocation, etEmail, etJob, etJobDescription;
    private AppCompatButton btnSaveChanges;
    private Boolean isValid;
    private TextView tvPageTitle, tvNameError, tvPhoneError, tvLocationError, tvEmailError, tvJobError, tvJobDescriptionError;

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
        etEmail = findViewById(R.id.et_email_editProfilePekerjaPage);
        etJob = findViewById(R.id.et_job_editProfilePekerjaPage);
        etJobDescription = findViewById(R.id.et_jobDescription_editProfilePekerjaPage);
        btnSaveChanges = findViewById(R.id.btn_saveChanges_editProfilePekerjaPage);

        tvNameError = findViewById(R.id.tv_nameError_editProfilePekerjaPage);
        tvPhoneError = findViewById(R.id.tv_phoneError_editProfilePekerjaPage);
        tvLocationError = findViewById(R.id.tv_locationError_editProfilePekerjaPage);
        tvEmailError = findViewById(R.id.tv_emailError_editProfilePekerjaPage);
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

//                check if email is empty
                if (etEmail.getText().toString().trim().length() < 1) {
                    etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvEmailError.setText("Email harus diisi!");
                    isValid = false;
                    Toast.makeText(v.getContext(), "Test", Toast.LENGTH_SHORT).show();
                } else if (!etEmail.getText().toString().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvEmailError.setText("Email tidak valid!");
                    isValid = false;
                }
                else {
                    etEmail.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvEmailError.setText("");
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
                    Toast.makeText(EditProfilePekerjaActivity.this, "Profil berhasil di ubah!", Toast.LENGTH_SHORT).show();
                    //Logic update profile
                    startProfilePage();
                }
            }
        });
    }

    private void startProfilePage() {
        startActivity(new Intent(EditProfilePekerjaActivity.this, ProfilePekerjaActivity.class));
        finish();
    }
}