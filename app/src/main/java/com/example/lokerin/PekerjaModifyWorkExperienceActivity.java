package com.example.lokerin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
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

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PekerjaModifyWorkExperienceActivity extends AppCompatActivity {

    private EditText etJob, etDescription;
    private DatePicker dpDate;
    private Spinner spnLocation;
    private Button btnCategory;
    private RelativeLayout rlUploadImage;
    private ImageView ivUploadImage, ivUploadedImage, btnBack, ivProfilePicture;
    private TextView tvUploadImage, tvPageTitle;
    private AppCompatButton acbSave;
    private ArrayAdapter<CharSequence> regencyAdapter, provinceAdapter;
    private CardView[] cards;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;
    FirebaseUser fuser;
    private User user;
    ArrayList<PortofolioJob> portofolioJobsArray;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_modify_work_experience);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cards = new CardView[]{
                findViewById(R.id.cv_category1_workExperienceCategoryPage),
                findViewById(R.id.cv_category2_workExperienceCategoryPage),
                findViewById(R.id.cv_category3_workExperienceCategoryPage),
                findViewById(R.id.cv_category4_workExperienceCategoryPage),
                findViewById(R.id.cv_category5_workExperienceCategoryPage),
                findViewById(R.id.cv_category6_workExperienceCategoryPage),
                findViewById(R.id.cv_category7_workExperienceCategoryPage),
                findViewById(R.id.cv_category8_workExperienceCategoryPage)
        };

        String[] categoryNames = {
                "Barber",
                "Builder",
                "Driver",
                "Gardener",
                "Maid",
                "Peddler",
                "Porter",
                "Secretary",
        };

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfilePicture = findViewById(R.id.btn_profile_toolbar);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");
        if(category != null){
            for(int i = 0; i < categoryNames.length; i++){
                if(category.equals(categoryNames[i])){
                    cards[i].setVisibility(View.VISIBLE);
                }else{
                    cards[i].setVisibility(View.GONE);
                }
            }
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntentActivity;
                if(intent.getStringExtra("activityOrigin").toString().equals("CreateProfile")) {
                    startIntentActivity = new Intent(PekerjaModifyWorkExperienceActivity.this, CreateProfile_WorkExperience.class);
                }
                else {
                    startIntentActivity = new Intent(PekerjaModifyWorkExperienceActivity.this, PekerjaAddWorkExperienceActivity.class);
                }
                startActivity(startIntentActivity);
                finish();
            }
        });
        tvPageTitle.setText("Tambah Pekerjaan Sebelumnya");
        ivProfilePicture.setImageResource(R.drawable.settings_icon);

        etJob = findViewById(R.id.et_job_modifyWorkExperiencePage);
        dpDate = findViewById(R.id.dp_date_modifyWorkExperiencePage);
        etDescription = findViewById(R.id.et_description_modifyWorkExperiencePage);
        spnLocation = findViewById(R.id.spinner_location_modifyWorkExperiencePage);
        btnCategory = findViewById(R.id.btn_category_modifyWorkExperiencePage);
        rlUploadImage = findViewById(R.id.rl_uploadImage_modifyWorkExperiencePage);
        ivUploadImage = findViewById(R.id.iv_uploadImage_modifyWorkExperiencePage);
        ivUploadedImage = findViewById(R.id.iv_uploadedImage_modifyWorkExperiencePage);
        tvUploadImage = findViewById(R.id.tv_uploadImage_modifyWorkExperiencePage);
        acbSave = findViewById(R.id.acb_save_modifyWorkExperiencePage);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaModifyWorkExperienceActivity.this, PekerjaModifyWorkExperienceCategoryActivity.class));
                finish();
            }
        });

        portofolioJobsArray = new ArrayList<>();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if(user.getPortofolioJob() != null){
                    portofolioJobsArray = user.getPortofolioJob();
                }else{
                    portofolioJobsArray = new ArrayList<>();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        provinceAdapter = ArrayAdapter.createFromResource(PekerjaModifyWorkExperienceActivity.this,
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnLocation.setAdapter(provinceAdapter);


        acbSave.setOnClickListener(v -> {
            boolean isValid = true;
            if(etJob.getText().toString().trim().length() < 1){
                isValid = false;
                Toast.makeText(this, "Judul Pekerjaan Harus diisi", Toast.LENGTH_SHORT).show();
            }

            if(spnLocation.getSelectedItem().toString().equals("Choose Province")){
                isValid = false;
                Toast.makeText(this, "Lokasi Harus dipilih", Toast.LENGTH_SHORT).show();
            }

            int   day  = dpDate.getDayOfMonth();
            int   month= dpDate.getMonth();
            int   year = dpDate.getYear();
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);

            Calendar today = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formattedDate = sdf.format(selectedDate.getTime());;

            if (!selectedDate.after(today)) {
                isValid = false;
                Toast.makeText(this, "Tanggal yang dipilih harus lebih besar dari hari ini", Toast.LENGTH_SHORT).show();
            }

            if(category == null){
                isValid = false;
                Toast.makeText(this, "Category Harus dipilih", Toast.LENGTH_SHORT).show();
            }

            if(etDescription.getText().toString().trim().length() < 1){
                isValid = false;
                Toast.makeText(this, "Deskripsi Pekerjaan Harus diisi", Toast.LENGTH_SHORT).show();
            }

            if (isValid) {
                if (imageUri != null) {
                    uploadImageAndSaveProfile();
                } else {
                    saveProfileData(null); // No image to upload
                }
            }

        });

        rlUploadImage.setOnClickListener(new View.OnClickListener() {
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
        userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        int   day  = dpDate.getDayOfMonth();
        int   month= dpDate.getMonth();
        int   year = dpDate.getYear();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        PortofolioJob dataPortofolioJob;

        if(imageUrl != null){
            dataPortofolioJob = new PortofolioJob(etJob.getText().toString(),spnLocation.getSelectedItem().toString(),category, selectedDate.getTime(),imageUrl , false, true );
            portofolioJobsArray.add(dataPortofolioJob);
        }else{
            dataPortofolioJob = new PortofolioJob(etJob.getText().toString(),spnLocation.getSelectedItem().toString(),category, selectedDate.getTime(),"default" , false, true );
            portofolioJobsArray.add(dataPortofolioJob);
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("portofolioJob", portofolioJobsArray);

        userReference.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Portofolio berhasil ditambah!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save Portofolio!", Toast.LENGTH_SHORT).show();
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

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();


            if(!PekerjaModifyWorkExperienceActivity.this.isDestroyed()){
                Glide.with(PekerjaModifyWorkExperienceActivity.this).load(imageUri).into(ivUploadedImage);
                ivUploadedImage.setVisibility(View.VISIBLE);
            }

        }
    }
}