package com.example.lokerin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class PelangganRatingReviewActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reviewReference, jobReference, pekerjaReference;

    private String jobId, pekerjaId, pelangganId, pelangganName, pelangganImageUrl;
    private ImageView btnBack, ivProfileNavbar;
    private TextView tvPageTitle, tvTitle, tvStatus, tvProvince, tvCategory, tvDate, tvSalary;
    private RatingBar rbRating;
    private CheckBox cbRecommend;
    private EditText etReviewRate;
    private AppCompatButton btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_rating_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        jobId = intent.getStringExtra("JOB_ID");
        pekerjaId = intent.getStringExtra("PEKERJA_ID");
        pelangganId = intent.getStringExtra("PELANGGAN_ID");
        pelangganName = intent.getStringExtra("PELANGGAN_NAME");
        pelangganImageUrl = intent.getStringExtra("PELANGGAN_IMAGE_URL");

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app");
        jobReference = firebaseDatabase.getReference().child("jobs").child(jobId);
        pekerjaReference = firebaseDatabase.getReference().child("users").child((pekerjaId));
        reviewReference = firebaseDatabase.getReference().child("reviews");

        tvTitle = findViewById(R.id.tv_title);
        tvStatus = findViewById(R.id.tv_status);
        tvProvince = findViewById(R.id.tv_province);
        tvCategory = findViewById(R.id.tv_category);
        tvDate = findViewById(R.id.tv_date);
        tvSalary = findViewById(R.id.tv_salary);

        rbRating = findViewById(R.id.rating_bar);
        cbRecommend = findViewById(R.id.checkbox_recommend);
        etReviewRate = findViewById(R.id.et_review_rate);
        btnPublish = findViewById(R.id.btn_login);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Ulasan");
        ivProfileNavbar.setVisibility(View.GONE);

        btnPublish.setOnClickListener(v -> validateInput());

        getJobData();

    }

    private void validateInput() {
        boolean isValid = true;

        if (rbRating.getRating() == 0) {
            Toast.makeText(this, "Pilih skala rating (1 - 5)!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        String review = etReviewRate.getText().toString();
        if (isValid && review.length() < 20) {
            etReviewRate.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(this, "Ulasan minimal berisi 20 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etReviewRate.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mengunggah...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            HashMap<String, Object> reviewData = new HashMap<>();
            reviewData.put("jobId", jobId);
            reviewData.put("pelangganId", pelangganId);
            reviewData.put("pelangganName", pelangganName);
            reviewData.put("pelangganImageUrl", pelangganImageUrl);
            reviewData.put("pekerjaId", pekerjaId);
            reviewData.put("rating", rbRating.getRating());
            reviewData.put("recommend", cbRecommend.isChecked());
            reviewData.put("review", review);

            DatabaseReference reviewRef = reviewReference.push();
            String generatedId = reviewRef.getKey();
            reviewData.put("rateReviewId", generatedId);

            reviewRef.setValue(reviewData).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    pekerjaReference.get().addOnCompleteListener(pekerjaTask -> {
                        if (pekerjaTask.isSuccessful() && pekerjaTask.getResult().exists()) {
                            DataSnapshot snapshot = pekerjaTask.getResult();

                            ArrayList<String> reviewsList = new ArrayList<>();
                            if (snapshot.child("reviews").exists()) {
                                reviewsList = (ArrayList<String>) snapshot.child("reviews").getValue();
                            }
                            if (reviewsList == null) {
                                reviewsList = new ArrayList<>();
                            }

                            String reviewIdToInsert = generatedId;
                            if (!reviewsList.contains(reviewIdToInsert)) {
                                reviewsList.add(reviewIdToInsert);

                                pekerjaReference.child("reviews").setValue(reviewsList).addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(this, "Ulasan berhasil ditambahkan.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "Gagal memperbarui daftar ulasan pekerja.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(this, "Gagal mengambil data pekerja.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Gagal menambahkan ulasan. Coba lagi!", Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(PelangganRatingReviewActivity.this, PelangganMainActivity.class);
            startActivity(intent);
        }
    }

    private void getJobData() {
        if (jobId == null) {
            return;
        }

        jobReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    updateJobShown(task);
                } else {
                    Toast.makeText(PelangganRatingReviewActivity.this, "Data pekerjaan tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PelangganRatingReviewActivity.this, "Error mengambil data pekerjaan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateJobShown(Task<DataSnapshot> task) {
        String title = task.getResult().child("jobTitle").getValue(String.class);
        tvTitle.setText(title != null && !title.isEmpty() ? title : "N/A");

        String status = task.getResult().child("jobStatus").getValue(String.class);
        tvStatus.setText(status != null && !status.isEmpty() ? status : "N/A");
        tvStatus.setTextColor(getResources().getColor(R.color.red));

        String province = task.getResult().child("jobProvince").getValue(String.class);
        tvProvince.setText(province != null && !province.isEmpty() ? province : "N/A");

        String category = task.getResult().child("jobCategory").getValue(String.class);
        tvCategory.setText(category != null && !category.isEmpty() ? category : "N/A");

        String date = task.getResult().child("jobDateUpload").getValue(String.class);
        tvDate.setText(date != null && !date.isEmpty() ? date : "N/A");

        Integer salary = task.getResult().child("jobSalary").getValue(Integer.class);
        String salaryFrequent = task.getResult().child("jobSalaryFrequent").getValue(String.class);
        tvSalary.setText(salaryFrequent != null && !salaryFrequent.isEmpty() ? "Rp " + String.valueOf(salary) + ",00 / " + salaryFrequent : "N/A");
    }

    private void backPage() {
        finish();
    }
}
