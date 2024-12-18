package com.example.lokerin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ArrayAdapter<CharSequence> regencyAdapter, provinceAdapter;

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

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfilePicture = findViewById(R.id.btn_profile_toolbar);

        Intent intent = getIntent();
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

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageSelection);

        rlUploadImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        provinceAdapter = ArrayAdapter.createFromResource(PekerjaModifyWorkExperienceActivity.this,
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnLocation.setAdapter(provinceAdapter);

        acbSave.setOnClickListener(v -> {
            boolean isValid = true;
//            validations..
        });
    }

    private void handleImageSelection(Uri uri) {
        if (uri != null) {
            try {
                ivUploadedImage.setVisibility(View.VISIBLE);
                ivUploadImage.setVisibility(View.GONE);
                tvUploadImage.setVisibility(View.GONE);
                ivUploadedImage.setImageURI(uri);

                InputStream inputStream = PekerjaModifyWorkExperienceActivity.this.getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ivUploadedImage.setImageBitmap(bitmap);
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(PekerjaModifyWorkExperienceActivity.this, "Gagal untuk mengunggah gambar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PekerjaModifyWorkExperienceActivity.this, "Tidak ada gambar yang terpilih", Toast.LENGTH_SHORT).show();
        }
    }
}