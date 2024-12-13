package com.example.lokerin;

import static java.security.AccessController.getContext;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class EditProfilePekerjaActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageView btnBack, ivProfileNavbar, ivProfilePicture;
    private EditText etName, etPhone, etLocation, etJob, etJobDescription;
    private AppCompatButton btnSaveChanges;
    private Boolean isValid;
    private TextView tvPageTitle, tvNameError, tvPhoneError, tvLocationError, tvJobError, tvJobDescriptionError;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        ivProfilePicture = findViewById(R.id.iv_profile2_editProfilePekerjaPage);
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
                if(user.getImageUrl().equals("default")){
                    ivProfilePicture.setImageResource(R.drawable.default_no_profile_icon);
                } else{
                    Glide.with(EditProfilePekerjaActivity.this).load(user.getImageUrl()).into(ivProfilePicture);
                }
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

                if (isValid) {
                    if (imageUri != null) {
                        uploadImageAndSaveProfile();
                    } else {
                        saveProfileData(null); // No image to upload
                    }
                }
            }
        });

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", etName.getText().toString());
        updates.put("phoneNumber", etPhone.getText().toString());
        updates.put("location", etLocation.getText().toString());
        updates.put("job", etJob.getText().toString());
        updates.put("jobDesc", etJobDescription.getText().toString());

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

            Glide.with(this).load(imageUri).into(ivProfilePicture);
        }
    }

    private void startProfilePage() {
        startActivity(new Intent(EditProfilePekerjaActivity.this, PekerjaProfileActivity.class));
        finish();
    }
}