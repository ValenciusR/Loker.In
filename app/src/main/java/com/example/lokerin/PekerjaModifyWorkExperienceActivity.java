package com.example.lokerin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Date;
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
    private String[] categoryNames;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;
    FirebaseUser fuser;
    private User user;
    ArrayList<PortofolioJob> portofolioJobsArray;
    String category;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private static final int CATEGORY_REQUEST = 2;
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

        categoryNames = new String[]{
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
        PortofolioJob dataPortofolio = (PortofolioJob) intent.getSerializableExtra("portofolioData");


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

        provinceAdapter = ArrayAdapter.createFromResource(PekerjaModifyWorkExperienceActivity.this,
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnLocation.setAdapter(provinceAdapter);

        dpDate.setMaxDate(System.currentTimeMillis());

//        SharedPreferences sharedPreferences = getSharedPreferences("FormData", MODE_PRIVATE);
//
//// Retrieve the data
//        String title = sharedPreferences.getString("title", "");
//        String desc = sharedPreferences.getString("desc", "");
//        String location = sharedPreferences.getString("location", "");
//        Integer dateDay = sharedPreferences.getInt("dateDay", -1);
//        Integer dateMonth = sharedPreferences.getInt("dateMonth", -1);
//        Integer dateYear = sharedPreferences.getInt("dateYear", -1);
//        String imageUriString = sharedPreferences.getString("imageUri", "");
//
//// Set the data back to the UI components
//        if (title != null) {
//            etJob.setText(title);
//        }
//
//        if (desc != null) {
//            etDescription.setText(desc);
//        }
//
//        if (location != null) {
//            // Get the index of the target value
//            int index = -1;
//            for (int i = 0; i < provinceAdapter.getCount(); i++) {
//                if (provinceAdapter.getItem(i).equals(location)) {
//                    index = i;
//                    break;
//                }
//            }
//
//            // Set the Spinner selection if the value is found
//            if (index != -1) {
//                spnLocation.setSelection(index);
//            } else {
//                Toast.makeText(this, "Value not found in Spinner", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        if (dateDay != -1 && dateMonth != -1 && dateYear != -1) {
//            Log.d("TAG", "onCreate: " + dateDay);
//            Log.d("TAG", "onCreate: " + dateMonth);
//            Log.d("TAG", "onCreate: " + dateYear);
//
//            dpDate.updateDate(dateYear, dateMonth, dateDay);
//        }
//
//        if (imageUriString != null) {
//            imageUri = Uri.parse(imageUriString);
//            // Set the image to an ImageView or process it further
//            Glide.with(PekerjaModifyWorkExperienceActivity.this).load(imageUri).into(ivUploadedImage);
//            ivUploadedImage.setVisibility(View.VISIBLE);
//        }

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PekerjaModifyWorkExperienceActivity.this, PekerjaModifyWorkExperienceCategoryActivity.class);
                startActivityForResult(intent, CATEGORY_REQUEST);

//                int   day  = dpDate.getDayOfMonth();
//                int   month= dpDate.getMonth();
//                int   year = dpDate.getYear();
//                Calendar selectedDate = Calendar.getInstance();
//                selectedDate.set(year, month, day);
//
//                getContentResolver().takePersistableUriPermission(
//                        imageUri,
//                        Intent.FLAG_GRANT_READ_URI_PERMISSION
//                );
//
//                SharedPreferences sharedPreferences = getSharedPreferences("FormData", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("title", etJob.getText().toString());
//                editor.putString("desc", etDescription.getText().toString());
//                editor.putString("location", spnLocation.getSelectedItem().toString());
//                editor.putInt("dateDay", dpDate.getDayOfMonth());
//                editor.putInt("dateMonth", dpDate.getMonth());
//                editor.putInt("dateYear", dpDate.getYear());
//                if (imageUri != null) {
//                    editor.putString("imageUri", imageUri.toString());
//                } else {
//                    editor.remove("imageUri"); // Optional: remove any existing value
//                }
//                editor.apply();

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

//            if (!selectedDate.after(today)) {
//                isValid = false;
//                Toast.makeText(this, "Tanggal yang dipilih harus lebih besar dari hari ini", Toast.LENGTH_SHORT).show();
//            }

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

        tvPageTitle.setText("Tambah Portofolio");
        if(dataPortofolio != null){
            tvPageTitle.setText("Update Portofolio");
            etJob.setText(dataPortofolio.getTitle());
            String targetValue = dataPortofolio.getLocation(); // The string you want to select in the Spinner

            // Get the index of the target value
            int index = -1;
            for (int i = 0; i < provinceAdapter.getCount(); i++) {
                if (provinceAdapter.getItem(i).equals(targetValue)) {
                    index = i;
                    break;
                }
            }

            // Set the Spinner selection if the value is found
            if (index != -1) {
                spnLocation.setSelection(index);
            } else {
                Toast.makeText(this, "Value not found in Spinner", Toast.LENGTH_SHORT).show();
            }

            Date date = dataPortofolio.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            dpDate.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

            if(dataPortofolio.getCategory() != null){
                for(int i = 0; i < categoryNames.length; i++){
                    if(dataPortofolio.getCategory().equals(categoryNames[i])){
                        cards[i].setVisibility(View.VISIBLE);
                    }else{
                        cards[i].setVisibility(View.GONE);
                    }
                }

                category = dataPortofolio.getCategory();
            }

            etDescription.setText(dataPortofolio.getDesc());
            Glide.with(PekerjaModifyWorkExperienceActivity.this).load(dataPortofolio.getImageURI()).into(ivUploadedImage);
            ivUploadedImage.setVisibility(View.VISIBLE);
        }
    }

    private void uploadImageAndSaveProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.show();

        try {
            getContentResolver().takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );

        } catch (SecurityException e) {
            Log.e("TAG", "Failed to re-grant persistable permission: " + e.getMessage());
        }

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

        Intent intent = getIntent();
        Integer pos = intent.getIntExtra("pos", -1);
        PortofolioJob dataPortofolio = (PortofolioJob) intent.getSerializableExtra("portofolioData");
        Log.d("TAG", "saveProfileData: " + pos);
        if (pos != -1){
            userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid()).child("portofolioJob").child(String.valueOf(pos));

            int   day  = dpDate.getDayOfMonth();
            int   month= dpDate.getMonth();
            int   year = dpDate.getYear();
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);

            PortofolioJob dataPortofolioJob;

            if(dataPortofolio.getImageURI() != null){
                if(imageUrl != null){
                    dataPortofolioJob = new PortofolioJob(etJob.getText().toString(),spnLocation.getSelectedItem().toString(),etDescription.getText().toString(),category, selectedDate.getTime(),imageUrl);
                }else{
                    dataPortofolioJob = new PortofolioJob(etJob.getText().toString(),spnLocation.getSelectedItem().toString(),etDescription.getText().toString(),category, selectedDate.getTime(),dataPortofolio.getImageURI());
                }
            }else{
                dataPortofolioJob = new PortofolioJob(etJob.getText().toString(),spnLocation.getSelectedItem().toString(),etDescription.getText().toString(),category, selectedDate.getTime(),"default");
            }

            userReference.setValue(dataPortofolioJob).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Portofolio berhasil ditambah!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save Portofolio!", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

            int   day  = dpDate.getDayOfMonth();
            int   month= dpDate.getMonth();
            int   year = dpDate.getYear();
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);

            PortofolioJob dataPortofolioJob;

            if(imageUrl != null){
                dataPortofolioJob = new PortofolioJob(etJob.getText().toString(),spnLocation.getSelectedItem().toString(),etDescription.getText().toString(),category, selectedDate.getTime(),imageUrl);
                portofolioJobsArray.add(dataPortofolioJob);
            }else{
                dataPortofolioJob = new PortofolioJob(etJob.getText().toString(),spnLocation.getSelectedItem().toString(),etDescription.getText().toString(),category, selectedDate.getTime(),"default");
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

        if (resultCode == RESULT_OK && data != null && requestCode == CATEGORY_REQUEST) {
            String response = data.getStringExtra("category");
            if(response != null){
                for(int i = 0; i < categoryNames.length; i++){
                    if(response.equals(categoryNames[i])){
                        cards[i].setVisibility(View.VISIBLE);
                    }else{
                        cards[i].setVisibility(View.GONE);
                    }
                }
                category = response;
            }
        }

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();


            if(!PekerjaModifyWorkExperienceActivity.this.isDestroyed()){
                Glide.with(PekerjaModifyWorkExperienceActivity.this).load(imageUri).into(ivUploadedImage);
                ivUploadedImage.setVisibility(View.VISIBLE);
            }

        }
    }
}