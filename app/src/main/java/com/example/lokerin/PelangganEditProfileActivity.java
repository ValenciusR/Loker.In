package com.example.lokerin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
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

    private ImageView btnBack;
    private ShapeableImageView ivProfileNavbar;
    private TextView tvPageTitle, tvNameError, tvPhoneError, tvLocationError, tvEmailError;
    private EditText etName, etPhone, etLocation, etEmail;
    private AppCompatButton btnSaveChanges;
    private boolean isValid;
    private DatabaseReference userReference;

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

        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mAuth = FirebaseAuth.getInstance();
        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
        getUserData();

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfilePage();
            }
        });
        tvPageTitle.setText("Edit Profil");

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

            String location = etLocation.getText().toString().trim();
            if (location.isEmpty()) {
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
                if (imageUri != null) {
                    uploadImageAndSaveProfile();
                } else {
                    saveProfileData(null); // No image to upload
                }
            }
        });

        ivProfileNavbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
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
        updates.put("location", etLocation.getText().toString());
        updates.put("email", etEmail.getText().toString());

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

            Glide.with(PelangganEditProfileActivity.this).load(imageUri).into(ivProfileNavbar);
        }
    }

    private void getUserData() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                etName.setText(user.getName());
                etPhone.setText(user.getPhoneNumber());
                etLocation.setText(user.getLocation());
                etEmail.setText(user.getEmail());

                if(user.getImageUrl().equals("default")){
                    ivProfileNavbar.setImageResource(R.drawable.default_no_profile_icon);
                } else{
                    if(!PelangganEditProfileActivity.this.isDestroyed()){
                        Glide.with(PelangganEditProfileActivity.this).load(user.getImageUrl()).into(ivProfileNavbar);
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
