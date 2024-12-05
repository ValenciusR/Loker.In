package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.imageview.ShapeableImageView;

public class EditProfilePelangganActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ShapeableImageView ivProfileNavbar;
    private TextView tvPageTitle, tvNameError, tvPhoneError, tvLocationError, tvEmailError;
    private EditText etName, etPhone, etLocation, etEmail;
    private AppCompatButton btnSaveChanges;
    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_edit_profile);

        ivProfileNavbar = findViewById(R.id.iv_profile);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etLocation = findViewById(R.id.et_location);
        etEmail = findViewById(R.id.et_email);
        tvNameError = findViewById(R.id.tv_nameError);
        tvPhoneError = findViewById(R.id.tv_phoneError);
        tvLocationError = findViewById(R.id.tv_locationError);
        tvEmailError = findViewById(R.id.tv_emailError);
        btnSaveChanges = findViewById(R.id.btn_saveChanges);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar.setOnClickListener(v -> Toast.makeText(this, "Profile picture clicked!", Toast.LENGTH_SHORT).show());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfilePage();
            }
        });
        tvPageTitle.setText("Edit Profil");

        btnSaveChanges.setOnClickListener(v -> {
            isValid = true;

            if (etName.getText().toString().trim().isEmpty()) {
                tvNameError.setText("Nama harus diisi!");
                tvNameError.setVisibility(View.VISIBLE);
                etName.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            } else {
                tvNameError.setVisibility(View.GONE);
                etName.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty() || phone.length() < 10 || !phone.matches("\\d+")) {
                tvPhoneError.setText("Nomor telepon tidak valid!");
                tvPhoneError.setVisibility(View.VISIBLE);
                etPhone.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            } else {
                tvPhoneError.setVisibility(View.GONE);
                etPhone.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            if (etLocation.getText().toString().trim().isEmpty()) {
                tvLocationError.setText("Lokasi harus diisi!");
                tvLocationError.setVisibility(View.VISIBLE);
                etLocation.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            } else {
                tvLocationError.setVisibility(View.GONE);
                etLocation.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            String email = etEmail.getText().toString().trim();
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tvEmailError.setText("Email tidak valid!");
                tvEmailError.setVisibility(View.VISIBLE);
                etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            } else {
                tvEmailError.setVisibility(View.GONE);
                etEmail.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            if (isValid) {
                Toast.makeText(this, "Profil berhasil di ubah!", Toast.LENGTH_SHORT).show();
                //Logic update profile
                startProfilePage();
            }
        });
    }

    private void startProfilePage() {
        startActivity(new Intent(EditProfilePelangganActivity.this, PelangganProfileActivity.class));
        finish();
    }
}
