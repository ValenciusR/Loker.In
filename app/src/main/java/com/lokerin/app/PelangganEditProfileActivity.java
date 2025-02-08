package com.lokerin.app;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Map;

public class PelangganEditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, ivProfileNavbar;
    private ShapeableImageView ivProfile;
    private TextView tvPageTitle, tvNameError, tvPhoneError, tvLocationError, tvAgeError, tvGenderError;
    private EditText etName, etPhone, etAge;
    private AppCompatButton btnSaveChanges;
    private boolean isValid;
    private DatabaseReference userReference;
    private Spinner spnLocation, spnGender;
    private ArrayAdapter<CharSequence> provinceAdapter, genderAdapter;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_edit_profile);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        ivProfile = findViewById(R.id.iv_profile);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAge = findViewById(R.id.et_age_editPelangganProfilePage);
//        etLocation = findViewById(R.id.et_location);
//        etEmail = findViewById(R.id.et_email);
        tvNameError = findViewById(R.id.tv_nameError);
        tvPhoneError = findViewById(R.id.tv_phoneError);
        tvLocationError = findViewById(R.id.tv_locationError);
        tvAgeError = findViewById(R.id.tv_ageError_editPelangganProfilePage);
        tvGenderError = findViewById(R.id.tv_genderError_editPelangganProfilePage);
//        tvEmailError = findViewById(R.id.tv_emailError);
        btnSaveChanges = findViewById(R.id.btn_saveChanges);
        spnLocation = findViewById(R.id.spinner_location);
        spnGender = findViewById(R.id.spinner_gender_editPelangganProfilePage);

        provinceAdapter = ArrayAdapter.createFromResource(PelangganEditProfileActivity.this,
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnLocation.setAdapter(provinceAdapter);

        genderAdapter = ArrayAdapter.createFromResource(PelangganEditProfileActivity.this,
                R.array.gender, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnGender.setAdapter(genderAdapter);

        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
        getUserData();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfilePage();
            }
        });
        tvPageTitle.setText("Ubah Profil");
        ivProfileNavbar.setVisibility(View.GONE);

        btnSaveChanges.setOnClickListener(v -> {
            isValid = true;

            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                tvNameError.setText("Nama harus diisi!");
                tvNameError.setVisibility(View.VISIBLE);
                etName.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            } else {
                tvNameError.setVisibility(View.GONE);
                etName.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            String phoneInput = etPhone.getText().toString().trim();
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


//            String location = etLocation.getText().toString().trim();
            if (spnLocation.getSelectedItem().toString().equals("Pilih Provinsi")) {
                tvLocationError.setText("Lokasi harus dipilih!");
                tvLocationError.setVisibility(View.VISIBLE);
                spnLocation.setBackgroundResource(R.drawable.shape_rounded_red_border);
//                etLocation.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            } else {
                tvLocationError.setVisibility(View.GONE);
                spnLocation.setBackgroundResource(R.drawable.shape_rounded_blue_border);
//                etLocation.setBackgroundResource(R.drawable.shape_rounded_blue_border);
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

            if (etAge.getText().toString().isEmpty()) {
                etAge.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvAgeError.setText("Umur harus diisi!");
                isValid = false;
            }
            else if (Integer.valueOf(etAge.getText().toString()) < 18) {
                etAge.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvAgeError.setText("Umur minimal 18 tahun!");
                isValid = false;
            }
            else if (!etAge.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                etAge.setBackgroundResource(R.drawable.shape_rounded_red_border);
                tvAgeError.setText("Umur hanya boleh diisi dengan angka!");
                isValid = false;
            }
            else {
                etAge.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                tvAgeError.setText("");
            }

//            String email = etEmail.getText().toString().trim();
//            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                tvEmailError.setText("Email tidak valid!");
//                tvEmailError.setVisibility(View.VISIBLE);
//                etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
//                isValid = false;
//            } else {
//                tvEmailError.setVisibility(View.GONE);
//                etEmail.setBackgroundResource(R.drawable.shape_rounded_blue_border);
//            }

            if (isValid) {
                if (imageUri != null) {
                    uploadImageAndSaveProfile();
                } else {
                    saveProfileData(null); // No image to upload
                }
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startProfilePage();
        super.onBackPressed();
    }

    private void uploadImageAndSaveProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.show();

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    progressDialog.dismiss();
                    saveProfileData(imageUrl); // Save profile with image URL
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                })
        ).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void saveProfileData(String imageUrl) {
        DatabaseReference userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", etName.getText().toString());
        updates.put("phoneNumber", etPhone.getText().toString());
        updates.put("location", spnLocation.getSelectedItem().toString());
        updates.put("age", Integer.parseInt(etAge.getText().toString()));
        updates.put("gender", spnGender.getSelectedItem().toString());
//        updates.put("location", etLocation.getText().toString());
//        updates.put("email", etEmail.getText().toString());

        if (imageUrl != null) {
            updates.put("imageUrl", imageUrl); // Include image URL only if an image was uploaded
        }

        userReference.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Profil berhasil diubah!", Toast.LENGTH_SHORT).show();
                startProfilePage();
            } else {
                Toast.makeText(this, "Failed to save profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();

            Glide.with(PelangganEditProfileActivity.this).load(imageUri).into(ivProfile);
        }
    }

    private void getUserData() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                etName.setText(user.getName());
                etPhone.setText(user.getPhoneNumber());
                etAge.setText(Integer.toString(user.getAge()));
//                etLocation.setText(user.getLocation());
//                etEmail.setText(user.getEmail());

                int index = -1;
                for (int i = 0; i < provinceAdapter.getCount(); i++) {
                    if (provinceAdapter.getItem(i).equals(user.getLocation())) {
                        index = i;
                        break;
                    }
                }

                // Set the Spinner selection if the value is found
                if (index != -1) {
                    spnLocation.setSelection(index);
                }

                index = -1;
                for (int i = 0; i < genderAdapter.getCount(); i++) {
                    if (genderAdapter.getItem(i).equals(user.getGender())) {
                        index = i;
                        break;
                    }
                }

                // Set the Spinner selection if the value is found
                if (index != -1) {
                    spnGender.setSelection(index);
                }

                if(user.getImageUrl().equals("default")){
                    ivProfile.setImageResource(R.drawable.default_no_profile_icon);
                } else{
                    if(!PelangganEditProfileActivity.this.isDestroyed()){
                        Glide.with(PelangganEditProfileActivity.this).load(user.getImageUrl()).into(ivProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startProfilePage() {
        startActivity(new Intent(PelangganEditProfileActivity.this, PelangganProfileActivity.class));
        finish();
    }
}
