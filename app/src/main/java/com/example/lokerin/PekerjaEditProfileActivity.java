package com.example.lokerin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;

public class PekerjaEditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageView btnBack, ivProfileNavbar, ivProfilePicture;
    private EditText etName, etPhone, etAboutMe, etAge;
    private AutoCompleteTextView etGender;
    private Spinner spnLocation;
    private ArrayAdapter<CharSequence> provinceAdapter;
    private AppCompatButton btnSaveChanges;
    private Boolean isValid;
    private TextView tvPageTitle, tvNameError, tvPhoneError, tvLocationError, tvAboutMeError, tvGenderError, tvAgeError;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private String[] item = {"Laki-Laki", "Perempuan"};
    private ArrayAdapter<String> adapterItems;


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

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        ivProfilePicture = findViewById(R.id.iv_profile2_editProfilePekerjaPage);
        etName = findViewById(R.id.et_name_editProfilePekerjaPage);
        etPhone = findViewById(R.id.et_phone_editProfilePekerjaPage);
        etAge = findViewById(R.id.et_age_editProfilePage);
        etGender = findViewById(R.id.et_gender_editProfilePage);
        spnLocation = findViewById(R.id.spinner_location_editProfilePage);
        etAboutMe = findViewById(R.id.et_aboutMe_editProfilePekerjaPage);
        btnSaveChanges = findViewById(R.id.btn_saveChanges_editProfilePekerjaPage);

        tvNameError = findViewById(R.id.tv_nameError_editProfilePekerjaPage);
        tvPhoneError = findViewById(R.id.tv_phoneError_editProfilePekerjaPage);
        tvLocationError = findViewById(R.id.tv_locationError_editProfilePekerjaPage);
        tvAboutMeError = findViewById(R.id.tv_aboutMeError_editProfilePekerjaPage);
        tvAgeError = findViewById(R.id.tv_ageError_editProfilePage);
        tvGenderError = findViewById(R.id.tv_ageError_editProfilePage);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProfilePage();
            }
        });
        tvPageTitle.setText("Edit Profil");
        ivProfileNavbar.setVisibility(View.GONE);

        provinceAdapter = ArrayAdapter.createFromResource(PekerjaEditProfileActivity.this,
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnLocation.setAdapter(provinceAdapter);

        adapterItems = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item);
        etGender.setAdapter(adapterItems);
        etGender.setOnClickListener(v -> {
            etGender.showDropDown();
        });
        etGender.setOnFocusChangeListener((v, hasFocus) -> etGender.showDropDown());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                etName.setText(user.getName());
                etAboutMe.setText(user.getAboutMe());
                etAge.setText(Integer.toString(user.getAge()));
                etGender.setText(user.getGender());

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

                etPhone.setText(user.getPhoneNumber());
                if(user.getImageUrl().equals("default")){
                    ivProfilePicture.setImageResource(R.drawable.default_no_profile_icon);
                } else{
                    if(!PekerjaEditProfileActivity.this.isDestroyed()){
                        Glide.with(PekerjaEditProfileActivity.this).load(user.getImageUrl()).into(ivProfilePicture);
                    }

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

                if (etName.getText().toString().trim().length() < 1) {
                    etName.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvNameError.setText("Nama harus diisi!");
                    isValid = false;
                } else {

                    for (char c : etName.getText().toString().toCharArray()) {
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
                if (spnLocation.getSelectedItem().toString().equals("Choose Province")) {
                    spnLocation.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvLocationError.setText("Lokasi harus dipilih!");
                    isValid = false;
                } else {
                    spnLocation.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvLocationError.setText("");
                }

//                Check if about me is empty
                if (etAboutMe.getText().toString().trim().length() < 20) {
                    etAboutMe.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvAboutMeError.setText("Minimal berisi 20 huruf!");
                    isValid = false;
                } else {
                    etAboutMe.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvAboutMeError.setText("");
                }

                //check gender
                if (etGender.getText().toString().isEmpty()) {
                    etGender.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvGenderError.setText("Jenis kelamin harus dipilih!");
                    isValid = false;
                }
                else if (!etGender.getText().toString().equals("Laki-Laki") && !etGender.getText().toString().equals("Perempuan")) {
                    etGender.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvGenderError.setText("Pilih antara Laki-Laki atau Perempuan!");
                    isValid = false;
                }
                else {
                    etGender.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    tvGenderError.setText("");
                }

                //check age
                if (etAge.getText().toString().isEmpty()) {
                    etAge.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    tvAgeError.setText("Umur harus diisi!");
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
        updates.put("location", spnLocation.getSelectedItem().toString());
        updates.put("aboutMe", etAboutMe.getText().toString());
        updates.put("age", Integer.parseInt(etAge.getText().toString()));
        updates.put("gender", etGender.getText().toString());

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


            if(!PekerjaEditProfileActivity.this.isDestroyed()){
                Glide.with(PekerjaEditProfileActivity.this).load(imageUri).into(ivProfilePicture);
            }

        }
    }

    private void startProfilePage() {
        startActivity(new Intent(PekerjaEditProfileActivity.this, PekerjaProfileActivity.class));
        finish();
    }
}