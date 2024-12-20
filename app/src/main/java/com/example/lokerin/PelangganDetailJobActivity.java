package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private TextView tvPageTitle, tvTitle, tvCategory, tvProvince, tvStatus, tvDate, tvSalary, tvApplicants, tvEmptyApplicants, tvEmptyWorkers;
    private Button btnAction, btnDelete, btnAction2;
    private RecyclerView rvApplicants, rvWorkers;
    private LinearLayout btnGroup;

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
                    if ("OPEN".equalsIgnoreCase(jobStatus)) {
                        btnAction.setText("Mulai");
                        btnAction.setOnClickListener(v -> showConfirmBookConfirmationDialog());
                        btnDelete.setText("Hapus");
                        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
                    } else if ("ON GOING".equalsIgnoreCase(jobStatus)) {
                        btnGroup.setVisibility(View.GONE);
                        tvApplicants.setVisibility(View.GONE);
                        rvApplicants.setVisibility(View.GONE);
                        tvEmptyApplicants.setVisibility(View.GONE);
                        btnAction2.setVisibility(View.VISIBLE);
                        btnAction2.setText("Akhiri Pekerjaan");
                        btnAction2.setOnClickListener(v -> showFinishJobConfirmationDialog());
                    } else if ("ENDED".equalsIgnoreCase(jobStatus)) {
                        btnGroup.setVisibility(View.GONE);
                        btnAction2.setVisibility(View.GONE);
                        tvApplicants.setVisibility(View.GONE);
                        rvApplicants.setVisibility(View.GONE);
                        tvEmptyApplicants.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(this, "Status pekerjaan sudah kadaluwarsa", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Gagal mendapatkan status pekerjaan", Toast.LENGTH_SHORT).show();
            }
        });

        tvTitle = findViewById(R.id.tv_title);
        tvStatus = findViewById(R.id.tv_status);
        tvProvince = findViewById(R.id.tv_province);
        tvCategory = findViewById(R.id.tv_category);
        tvDate = findViewById(R.id.tv_date);
        tvSalary = findViewById(R.id.tv_salary);
        tvApplicants = findViewById(R.id.tv_applicants);
        tvEmptyApplicants = findViewById(R.id.tv_noData_applicants);
        tvEmptyWorkers = findViewById(R.id.tv_noData_workers);

        btnAction = findViewById(R.id.btn_action);
        btnDelete = findViewById(R.id.btn_delete);
        btnAction2 = findViewById(R.id.btn_action2);
        btnGroup = findViewById(R.id.btn_group);

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
                    if(applicants != null && !applicants.isEmpty()) {
                        listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
                        rvApplicants.setAdapter(listApplicantAdapter);
                        tvEmptyApplicants.setVisibility(View.GONE);
                    } else {
                        if (jobStatus.equalsIgnoreCase("OPEN")){
                            tvEmptyApplicants.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    applicants.clear();
                    if (jobStatus.equalsIgnoreCase("OPEN")){
                        tvEmptyApplicants.setVisibility(View.VISIBLE);
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
                    if(workers != null && !workers.isEmpty()) {
                        listWorkerAdapter = new ListApplicantAdapter(workers, jobId, jobStatus);
                        rvWorkers.setAdapter(listWorkerAdapter);
                        tvEmptyWorkers.setVisibility(View.GONE);
                    } else {
                        tvEmptyWorkers.setVisibility(View.VISIBLE);
                    }
                } else {
                    workers.clear();
                    tvEmptyWorkers.setVisibility(View.VISIBLE);
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
        dialog.setContentView(R.layout.confirmation_popup_confirmjob);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);
        String function = "confirm";

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Konfirmasi Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            if (jobId != null && function.equalsIgnoreCase("confirm")) {
                jobsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("jobWorkers").exists()) {
                            DatabaseReference jobApplicantsRef = jobsReference.child("jobApplicants");
                            jobApplicantsRef.removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        jobsReference.child("jobStatus").setValue("ON GOING")
                                                .addOnSuccessListener(statusUpdate -> {
                                                    Toast.makeText(PelangganDetailJobActivity.this, "Pekerjaan telah dimulai.", Toast.LENGTH_SHORT).show();

                                                    // REFRESH PAGE
                                                    jobStatus = "ON GOING";
                                                    btnAction.setText("Akhiri Pekerjaan");
                                                    btnAction.setOnClickListener(v -> showFinishJobConfirmationDialog());
                                                    btnDelete.setVisibility(View.GONE);
                                                    tvStatus.setText("ON GOING");
                                                    tvStatus.setTextColor(getResources().getColor(R.color.blue));
                                                    tvApplicants.setVisibility(View.GONE);
                                                    tvEmptyApplicants.setVisibility(View.GONE);
                                                })
                                                .addOnFailureListener(statusError -> {
                                                    Toast.makeText(PelangganDetailJobActivity.this, "Gagal memulai pekerjaan.", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(PelangganDetailJobActivity.this, "Gagal menghapus data pelamar.", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(PelangganDetailJobActivity.this, "Belum ada pekerja diterima, tidak dapat memulai pekerjaan!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
                Toast.makeText(PelangganDetailJobActivity.this, "ID pekerjaan tidak valid!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup_deletejob);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);
        String function = "delete";

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Hapus Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            if (jobId != null && function.equalsIgnoreCase("delete")) {
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
        dialog.setContentView(R.layout.confirmation_popup_confirmjob);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);
        String function = "finish";

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Selesaikan Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            if (jobId != null && function.equalsIgnoreCase("finish")) {
                jobsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            jobsReference.child("jobStatus").setValue("ENDED")
                                    .addOnSuccessListener(statusUpdate -> {
                                        Toast.makeText(PelangganDetailJobActivity.this, "Pekerjaan telah diakhiri.", Toast.LENGTH_SHORT).show();

                                        // REFRESH PAGE
                                        jobStatus = "ENDED";
                                        btnGroup.setVisibility(View.GONE);
                                        btnAction2.setVisibility(View.GONE);
                                        tvStatus.setText("ENDED");
                                        tvStatus.setTextColor(getResources().getColor(R.color.red));
                                        tvApplicants.setVisibility(View.GONE);
                                        tvEmptyApplicants.setVisibility(View.GONE);

                                        // REFRESH RECYCLER VIEW
                                        jobsReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                workers = (ArrayList<String>) snapshot.child("jobWorkers").getValue();
                                                listWorkerAdapter = new ListApplicantAdapter(workers, jobId, jobStatus);
                                                rvWorkers.setAdapter(listWorkerAdapter);
                                                tvEmptyWorkers.setVisibility(View.GONE);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    })
                                    .addOnFailureListener(statusError -> {
                                        Toast.makeText(PelangganDetailJobActivity.this, "Gagal mengakhiri pekerjaan.", Toast.LENGTH_SHORT).show();
                                    });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Toast.makeText(this, "WOII BERHASIL DI PENCET CO", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PelangganDetailJobActivity.this, "ID pekerjaan tidak valid!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
//            Toast.makeText(this, "BUTTON FINISH JOB DI CLICK", Toast.LENGTH_SHORT).show();
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

        String status = task.getResult().child("jobStatus").getValue(String.class);
        tvStatus.setText(status != null && !status.isEmpty() ? status : "N/A");
        setStatusColor(status);

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

    private void setStatusColor(String status) {
        if (status == null || status.isEmpty()) return;

        switch (status.toUpperCase()) {
            case "OPEN":
                tvStatus.setTextColor(getResources().getColor(R.color.green));
                break;
            case "ON GOING":
                tvStatus.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "ENDED":
                tvStatus.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
                tvStatus.setTextColor(getResources().getColor(android.R.color.black));
                break;
        }
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
