package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PelangganDetailJobActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageView btnBack,ivProfileNavbar;
    private String jobId, jobStatus;
    private TextView tvPageTitle, tvTitle, tvCategory, tvProvince, tvRegency, tvDate, tvSalary;
    private Button btnAction, btnDelete;
    private RecyclerView rvApplicants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_detail_job);

        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobId");

        if (jobId == null) {
            Toast.makeText(this, "ID pekerjaan tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("jobs").child(jobId);
        jobStatus = databaseReference.child("jobStatus").toString();

        tvTitle = findViewById(R.id.tv_title);
        tvCategory = findViewById(R.id.tv_category);
        tvProvince = findViewById(R.id.tv_province);
        tvRegency = findViewById(R.id.tv_regency);
        tvDate = findViewById(R.id.tv_date);
        tvSalary = findViewById(R.id.tv_salary);

        btnAction = findViewById(R.id.btn_action);
        btnDelete = findViewById(R.id.btn_delete);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Detail Pekerjaan");

        ArrayList<User> applicantsList = new ArrayList<User>();
        rvApplicants = findViewById(R.id.recyclerView);
        rvApplicants.setLayoutManager(new LinearLayoutManager(this));
        ListUserAdapter adapter = new ListUserAdapter(applicantsList, jobStatus);
        rvApplicants.setAdapter(adapter);

        if ("Active".equals(jobStatus)) {
            btnAction.setText("Mulai Pekerjaan");
            btnAction.setOnClickListener(v -> showConfirmBookConfirmationDialog());
            btnDelete.setVisibility(View.GONE);
        } else if ("On Going".equals(jobStatus)) {
            btnAction.setText("Akhiri Pekerjaan");
            btnAction.setOnClickListener(v -> showFinishJobConfirmationDialog());
            btnDelete.setVisibility(View.GONE);
        } else if ("Ended".equals(jobStatus)) {
            btnAction.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
        } else if ("Applyable".equals(jobStatus)) {
            btnAction.setText("Daftar");
            btnAction.setOnClickListener(v -> showFinishJobConfirmationDialog());
            btnDelete.setVisibility(View.GONE);
            rvApplicants.setVisibility(View.GONE);
        } else{
            btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
        }

        getJobData();

    }

    private void showDeleteConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Hapus Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            //Delete Job
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showConfirmBookConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Konfirmasi Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            //Change Status of the Job
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showFinishJobConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Selesaikan Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            //Change Status of the Job
            dialog.dismiss();
        });

        dialog.show();
    }

    private void getJobData() {
        if (jobId == null) {
            return;
        }

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    updateJobShown(task);
                } else {
                    Toast.makeText(PelangganDetailJobActivity.this, "Data pekerjaan tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PelangganDetailJobActivity.this, "Error mengambil data pekerjaan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateJobShown(Task<DataSnapshot> task) {
        String title = task.getResult().child("jobTitle").getValue(String.class);
        tvTitle.setText(title != null && !title.isEmpty() ? title : "N/A");

        String category = task.getResult().child("jobCategory").getValue(String.class);
        tvCategory.setText(category != null && !category.isEmpty() ? category : "N/A");

        String province = task.getResult().child("jobProvince").getValue(String.class);
        tvProvince.setText(province != null && !province.isEmpty() ? province : "N/A");

        String regency = task.getResult().child("jobRegency").getValue(String.class);
        tvRegency.setText(regency != null && !regency.isEmpty() ? regency : "N/A");

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
