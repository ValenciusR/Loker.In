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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PelangganDetailJobActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsReference;

    private ImageView btnBack,ivProfileNavbar;
    private String jobId, jobStatus;
    private TextView tvPageTitle, tvTitle, tvCategory, tvProvince, tvRegency, tvDate, tvSalary;
    private Button btnAction, btnDelete;
    private RecyclerView rvApplicants, rvWorkers;

    private ArrayList<String> applicants, workers;
    private ListApplicantAdapter listApplicantAdapter, listWorkerAdapter;

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
        jobsReference = firebaseDatabase.getReference().child("jobs").child(jobId);
        jobsReference.child("jobStatus").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                jobStatus = task.getResult().getValue(String.class);
                if (jobStatus != null) {
                    Toast.makeText(this, "Status pekerjaan : " + jobStatus, Toast.LENGTH_SHORT).show();

                    if ("OPEN".equalsIgnoreCase(jobStatus)) {
                        btnAction.setText("Mulai");
                        btnAction.setOnClickListener(v -> showConfirmBookConfirmationDialog());
                        btnDelete.setText("Hapus");
                        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
                    } else if ("ON GOING".equalsIgnoreCase(jobStatus)) {
                        btnAction.setText("Akhiri Pekerjaan");
                        btnAction.setOnClickListener(v -> showFinishJobConfirmationDialog());
                        btnDelete.setVisibility(View.GONE);
                    } else if ("ENDED".equalsIgnoreCase(jobStatus)) {
                        btnAction.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                    } else if ("Applyable".equalsIgnoreCase(jobStatus)) {
                        btnAction.setText("Daftar");
                        btnAction.setOnClickListener(v -> showFinishJobConfirmationDialog());
                        btnDelete.setVisibility(View.GONE);
                        rvApplicants.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(this, "Status pekerjaan sudah kadaluwarsa", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Gagal mendapatkan status pekerjaan", Toast.LENGTH_SHORT).show();
            }
        });

        tvTitle = findViewById(R.id.tv_title);
        tvCategory = findViewById(R.id.tv_category);
        tvProvince = findViewById(R.id.tv_province);
        tvRegency = findViewById(R.id.tv_regency);
        tvDate = findViewById(R.id.tv_date);
        tvSalary = findViewById(R.id.tv_salary);

        btnAction = findViewById(R.id.btn_action);
        btnDelete = findViewById(R.id.btn_delete);

        rvApplicants = findViewById(R.id.rv_applicants_detailJob);
        rvWorkers = findViewById(R.id.rv_workers_detailJob);

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
        ivProfileNavbar.setVisibility(View.GONE);

        applicants = new ArrayList<>();
        listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
        rvApplicants.setLayoutManager(new LinearLayoutManager(this));
        rvApplicants.setAdapter(listApplicantAdapter);
        jobsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("jobApplicants").exists()) {
                    applicants = (ArrayList<String>) snapshot.child("jobApplicants").getValue();
                    if(!applicants.isEmpty()) {
                        listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
                        rvApplicants.setAdapter(listApplicantAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        workers = new ArrayList<>();
        listWorkerAdapter = new ListApplicantAdapter(workers, jobId, jobStatus);
        rvWorkers.setLayoutManager(new LinearLayoutManager(this));
        rvWorkers.setAdapter(listWorkerAdapter);
        jobsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("jobWorkers").exists()) {
                    workers = (ArrayList<String>) snapshot.child("jobWorkers").getValue();
                    if(!workers.isEmpty()) {
                        listWorkerAdapter = new ListApplicantAdapter(workers, jobId, jobStatus);
                        rvWorkers.setAdapter(listWorkerAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        getJobData();

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
            if (jobId != null) {
                jobsReference.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(PelangganDetailJobActivity.this, "Data pekerjaan berhasil dihapus.", Toast.LENGTH_SHORT).show();
                            navigateToAdminMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(PelangganDetailJobActivity.this, "Gagal menghapus data pekerjaan.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(PelangganDetailJobActivity.this, "ID pekerjaan tidak valid!", Toast.LENGTH_SHORT).show();
            }
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

        jobsReference.get().addOnCompleteListener(task -> {
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

    private void navigateToAdminMainActivity() {
        Intent intent = new Intent(PelangganDetailJobActivity.this, PelangganMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void backPage() {
        finish();
    }

}
