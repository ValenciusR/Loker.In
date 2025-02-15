package com.lokerin.app;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdminDetailJobActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageView btnBack,ivProfileNavbar;
    private String jobId, jobStatus, jobMakerId;
    private TextView tvPageTitle, tvTitle, tvStatus, tvProvince, tvCategory, tvDate, tvSalary, tvEmptyApplicants, tvEmptyWorkers, tvCreatedBy;
    private Button btnAction, btnDelete;
    private RecyclerView rvApplicants, rvWorkers;

    private ArrayList<String> applicants, workers;
    private ListApplicantAdapter listApplicantAdapter,listWorkerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail_job);

        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobId");

        if (jobId == null) {
            Toast.makeText(this, "ID pekerjaan tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("jobs").child(jobId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    jobStatus = snapshot.child("jobStatus").getValue(String.class);
                    jobMakerId = snapshot.child("jobMakerId").getValue(String.class);
                    DatabaseReference databaseReferenceJobMaker = firebaseDatabase.getReference().child("users").child(jobMakerId);
                    databaseReferenceJobMaker.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            tvCreatedBy.setText("Dibuat Oleh : " + user.getName());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        tvTitle = findViewById(R.id.tv_title);
        tvCategory = findViewById(R.id.tv_category);
        tvProvince = findViewById(R.id.tv_province);
        tvStatus = findViewById(R.id.tv_status);
        tvDate = findViewById(R.id.tv_date);
        tvSalary = findViewById(R.id.tv_salary);
        tvCreatedBy = findViewById(R.id.tv_createdBy_adminDetailJobPage);

        btnAction = findViewById(R.id.btn_action);
        btnDelete = findViewById(R.id.btn_delete);

        rvApplicants = findViewById(R.id.rv_applicants_detailJobAdmin);
        rvWorkers = findViewById(R.id.rv_workers_detailJobAdmin);
        tvEmptyApplicants = findViewById(R.id.tv_noData_applicants);
        tvEmptyWorkers = findViewById(R.id.tv_noData_workers);

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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("jobApplicants").exists()) {
                    applicants = (ArrayList<String>) snapshot.child("jobApplicants").getValue();
                    if(applicants != null && !applicants.isEmpty()) {
                        listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
                        rvApplicants.setAdapter(listApplicantAdapter);
                        tvEmptyApplicants.setVisibility(View.GONE);
                    } else {
                        listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
                        rvApplicants.setAdapter(listApplicantAdapter);
                        tvEmptyApplicants.setVisibility(View.VISIBLE);
                    }
                } else {
                    applicants.clear();
                    listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
                    rvApplicants.setAdapter(listApplicantAdapter);
                    tvEmptyApplicants.setVisibility(View.VISIBLE);
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("jobWorkers").exists()) {
                    workers = (ArrayList<String>) snapshot.child("jobWorkers").getValue();
                    if(workers != null && !workers.isEmpty()) {
                        listWorkerAdapter = new ListApplicantAdapter(workers, jobId, jobStatus);
                        rvWorkers.setAdapter(listWorkerAdapter);
                        tvEmptyWorkers.setVisibility(View.GONE);
                    } else {
                        listWorkerAdapter = new ListApplicantAdapter(workers, jobId, jobStatus);
                        rvWorkers.setAdapter(listWorkerAdapter);
                        tvEmptyWorkers.setVisibility(View.VISIBLE);
                    }
                } else {
                    workers.clear();
                    listWorkerAdapter = new ListApplicantAdapter(workers, jobId, jobStatus);
                    rvWorkers.setAdapter(listWorkerAdapter);
                    tvEmptyWorkers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnAction.setVisibility(View.GONE);
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        getJobData();
    }

    private void showDeleteConfirmationDialog() {
        Dialog dialog = new Dialog(AdminDetailJobActivity.this);
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
                databaseReference.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AdminDetailJobActivity.this, "Data pekerjaan berhasil dihapus.", Toast.LENGTH_SHORT).show();
                            navigateToAdminMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AdminDetailJobActivity.this, "Gagal menghapus data pekerjaan.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(AdminDetailJobActivity.this, "ID pekerjaan tidak valid!", Toast.LENGTH_SHORT).show();
            }
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
                    Toast.makeText(AdminDetailJobActivity.this, "Data pekerjaan tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminDetailJobActivity.this, "Error mengambil data pekerjaan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateJobShown(Task<DataSnapshot> task) {
        String title = task.getResult().child("jobTitle").getValue(String.class);
        tvTitle.setText(title != null && !title.isEmpty() ? title : "N/A");

        String status = task.getResult().child("jobStatus").getValue(String.class);
        //tvStatus.setText(status != null && !status.isEmpty() ? status : "N/A");
        setStatusColor(status);

        String province = task.getResult().child("jobProvince").getValue(String.class);
        String regency = task.getResult().child("jobRegency").getValue(String.class);
        if (province != null && !province.isEmpty() && regency != null && !regency.isEmpty()){
            tvProvince.setText(regency + ", " + province);
        } else {
            tvProvince.setText("N/A");
        }

        String category = task.getResult().child("jobCategory").getValue(String.class);
        tvCategory.setText(category != null && !category.isEmpty() ? category : "N/A");

        String dateOpen = task.getResult().child("jobDateUpload").getValue(String.class);
        String dateClose = task.getResult().child("jobDateClose").getValue(String.class);

        if (dateOpen != null && !dateOpen.isEmpty() && dateClose != null && !dateClose.isEmpty()) {
            // Set Locale to Indonesian
            Locale localeID = new Locale("id", "ID");

            // Format dates with Indonesian locale
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", localeID);

            try {
                // Parse the date strings into Date objects
                Date openDate = dateFormat.parse(dateOpen);
                Date closeDate = dateFormat.parse(dateClose);

                // Convert back to Indonesian format
                String formattedDateOpen = dateFormat.format(openDate);
                String formattedDateClose = dateFormat.format(closeDate);

                // Set formatted text
                tvDate.setText(formattedDateOpen + " - " + formattedDateClose);
            } catch (ParseException e) {
                e.printStackTrace();
                tvDate.setText("N/A");
            }
        } else {
            tvDate.setText("N/A");
        }

        Integer salary = task.getResult().child("jobSalary").getValue(Integer.class);
        String salaryFrequent = task.getResult().child("jobSalaryFrequent").getValue(String.class);
        tvSalary.setText(salaryFrequent != null && !salaryFrequent.isEmpty() ? "Rp " + String.valueOf(salary) + ",00 / " + salaryFrequent : "N/A");
    }

    private void setStatusColor(String status) {
        if (status == null || status.isEmpty()) return;

        switch (status.toUpperCase()) {
            case "OPEN":
                tvStatus.setText("TERBUKA");
                tvStatus.setTextColor(getResources().getColor(R.color.green));
                break;
            case "ON GOING":
                tvStatus.setText("SEDANG BERJALAN");
                tvStatus.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "ENDED":
                tvStatus.setText("SELESAI");
                tvStatus.setTextColor(getResources().getColor(R.color.red));
                break;
            default:
                tvStatus.setTextColor(getResources().getColor(android.R.color.black));
                break;
        }
    }

    private void navigateToAdminMainActivity() {
        Intent intent = new Intent(AdminDetailJobActivity.this, AdminMainActivity.class);
        intent.putExtra("targetFragment", "AdminJobsFragment");
        startActivity(intent);
        finish();
    }

    private void backPage() {
        finish();
    }

}
