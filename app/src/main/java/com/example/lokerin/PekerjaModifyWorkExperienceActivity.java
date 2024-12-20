package com.example.lokerin;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.IOException;
import java.io.InputStream;

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

        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaModifyWorkExperienceActivity.this, PekerjaModifyWorkExperienceCategoryActivity.class));
                finish();
            }
        });

        provinceAdapter = ArrayAdapter.createFromResource(PekerjaModifyWorkExperienceActivity.this,
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnLocation.setAdapter(provinceAdapter);

        acbSave.setOnClickListener(v -> {
            boolean isValid = true;
//            validations..
        });

        rlUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
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