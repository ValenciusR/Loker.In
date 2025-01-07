package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;

public class PelangganDetailJobActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsReference;

    private ImageView btnBack,ivProfileNavbar, ivJobImage;
    private String jobId, jobStatus;
    private TextView tvPageTitle, tvTitle, tvCategory, tvProvince,
            tvStatus, tvDate, tvSalary, tvApplicants, tvEmptyApplicants, tvEmptyWorkers, tvDayLeft, tvAlamat;

    private TextView btnAction, btnDelete, btnAction2, btnEdit;
    private RecyclerView rvApplicants, rvWorkers;
    private LinearLayout btnGroup;

    private ArrayList<String> applicants, workers;
    private ListApplicantAdapter listApplicantAdapter, listWorkerAdapter;

    private FrameLayout loadingView;
    private RelativeLayout mainView;

    private static final int REQUEST_CODE_VALIDATION = 1001;

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

        jobsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean hasApplicants = false;
                boolean hasWorkers = false;
                String jobStatusTemp = "";

                if (snapshot.child("jobApplicants").exists()) {
                    applicants = (ArrayList<String>) snapshot.child("jobApplicants").getValue();
                    if (applicants != null && !applicants.isEmpty()) {
                        hasApplicants = true;
                    }
                }

                if (snapshot.child("jobWorkers").exists()) {
                    workers = (ArrayList<String>) snapshot.child("jobWorkers").getValue();
                    if (workers != null && !workers.isEmpty()) {
                        hasWorkers = true;
                    }
                }

                if (snapshot.child("jobStatus").exists()) {
                    jobStatusTemp = snapshot.child("jobStatus").getValue(String.class);
                }

                if ("OPEN".equalsIgnoreCase(jobStatusTemp) && !hasApplicants && !hasWorkers) {
                    btnEdit.setVisibility(View.VISIBLE);
                    btnEdit.setOnClickListener(v -> navigateToEditJobActivity(jobId));
                } else {
                    btnEdit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        jobsReference.child("jobStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    jobStatus = snapshot.getValue(String.class);
                    if (jobStatus != null) {
                        handleJobStatus(jobStatus);
                    } else {
                        Toast.makeText(PelangganDetailJobActivity.this, "Status pekerjaan sudah kadaluwarsa", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PelangganDetailJobActivity.this, "Status pekerjaan tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PelangganDetailJobActivity.this, "Gagal mendapatkan status pekerjaan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        tvTitle = findViewById(R.id.tv_title);
        tvStatus = findViewById(R.id.tv_status);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvProvince = findViewById(R.id.tv_province);
        tvCategory = findViewById(R.id.tv_category);
        tvDate = findViewById(R.id.tv_date);
        tvSalary = findViewById(R.id.tv_salary);
        tvApplicants = findViewById(R.id.tv_applicants);
        tvEmptyApplicants = findViewById(R.id.tv_noData_applicants);
        tvEmptyWorkers = findViewById(R.id.tv_noData_workers);
        tvDayLeft = findViewById(R.id.tv_day_left);
        ivJobImage = findViewById(R.id.iv_jobImage_pelangganDetailJob);

        btnAction = findViewById(R.id.btn_action);
        btnDelete = findViewById(R.id.btn_delete);
        btnEdit = findViewById(R.id.btn_edit);
        btnAction2 = findViewById(R.id.btn_action2);
        btnGroup = findViewById(R.id.btn_group);

        rvApplicants = findViewById(R.id.rv_applicants_detailJob);
        rvWorkers = findViewById(R.id.rv_workers_detailJob);

        loadingView = findViewById(R.id.loading_overlay);
        mainView = findViewById(R.id.main);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMainActivity();
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
                            listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
                            rvApplicants.setAdapter(listApplicantAdapter);
                            tvEmptyApplicants.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    applicants.clear();
                    if (jobStatus.equalsIgnoreCase("OPEN")){
                        listApplicantAdapter = new ListApplicantAdapter(applicants, jobId, jobStatus);
                        rvApplicants.setAdapter(listApplicantAdapter);
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

        getJobData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_VALIDATION) {
            if (resultCode == RESULT_OK) {
                finish();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        navigateToMainActivity();
        super.onBackPressed();
    }

    private void handleJobStatus(String jobStatus) {
        switch (jobStatus.toUpperCase()) {
            case "OPEN":
                btnAction.setText("Mulai");
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setOnClickListener(v -> showConfirmBookConfirmationDialog());

                btnDelete.setText("Hapus");
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

                btnGroup.setVisibility(View.VISIBLE);
                btnAction2.setVisibility(View.GONE);

                tvApplicants.setVisibility(View.VISIBLE);
                break;

            case "ON GOING":
                btnGroup.setVisibility(View.GONE);
                tvApplicants.setVisibility(View.GONE);
                rvApplicants.setVisibility(View.GONE);
                tvEmptyApplicants.setVisibility(View.GONE);

                btnAction2.setVisibility(View.VISIBLE);
                btnAction2.setText("Akhiri Pekerjaan");
                btnAction2.setOnClickListener(v -> showFinishJobConfirmationDialog());
                break;

            case "ENDED":
                btnGroup.setVisibility(View.GONE);
                btnAction2.setVisibility(View.GONE);
                tvApplicants.setVisibility(View.GONE);
                rvApplicants.setVisibility(View.GONE);
                tvEmptyApplicants.setVisibility(View.GONE);
                break;

            default:
                Toast.makeText(this, "Status pekerjaan tidak valid", Toast.LENGTH_SHORT).show();
                break;
        }
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

        dialog.show();

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();

            mainView.setClickable(false);
            loadingView.setVisibility(View.VISIBLE);

            if (jobId != null && function.equalsIgnoreCase("confirm")) {
                loadingView.setVisibility(View.GONE);
                mainView.setClickable(true);
                jobsReference.addListenerForSingleValueEvent (new ValueEventListener() {
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
                                                    tvStatus.setBackground(getDrawable(R.drawable.shape_rounded_yellow_pill));
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
        });
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

        dialog.show();

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();

            mainView.setClickable(false);
            loadingView.setVisibility(View.VISIBLE);

            if (jobId != null && function.equalsIgnoreCase("delete")) {
                loadingView.setVisibility(View.GONE);
                mainView.setClickable(true);
                jobsReference.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(PelangganDetailJobActivity.this, "Data pekerjaan berhasil dihapus.", Toast.LENGTH_SHORT).show();
                            navigateToMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(PelangganDetailJobActivity.this, "Gagal menghapus data pekerjaan.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                loadingView.setVisibility(View.GONE);
                mainView.setClickable(true);
                Toast.makeText(PelangganDetailJobActivity.this, "ID pekerjaan tidak valid!", Toast.LENGTH_SHORT).show();
            }
        });
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

        dialog.show();

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();

            mainView.setClickable(false);
            loadingView.setVisibility(View.VISIBLE);

            if (jobId != null && function.equalsIgnoreCase("finish")) {
                loadingView.setVisibility(View.GONE);
                mainView.setClickable(true);
                jobsReference.addListenerForSingleValueEvent (new ValueEventListener() {
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
                                        tvStatus.setBackground(getDrawable(R.drawable.shape_rounded_red_pill));
                                        tvApplicants.setVisibility(View.GONE);
                                        tvEmptyApplicants.setVisibility(View.GONE);

                                        // REFRESH RECYCLER VIEW
                                        jobsReference.addListenerForSingleValueEvent (new ValueEventListener() {
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
            } else {
                loadingView.setVisibility(View.GONE);
                mainView.setClickable(true);
                Toast.makeText(PelangganDetailJobActivity.this, "ID pekerjaan tidak valid!", Toast.LENGTH_SHORT).show();
            }
        });
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
        //tvStatus.setText(status != null && !status.isEmpty() ? status : "N/A");
        setStatusColor(status);

        String province = task.getResult().child("jobProvince").getValue(String.class);
        String regency = task.getResult().child("jobRegency").getValue(String.class);
        if (province != null && !province.isEmpty() && regency != null && !regency.isEmpty()){
            tvProvince.setText(regency + ", " + province);
        } else {
            tvProvince.setText("N/A");
        }

        String alamat = task.getResult().child("jobAddress").getValue(String.class);
        tvAlamat.setText(alamat);

        String category = task.getResult().child("jobCategory").getValue(String.class);
        tvCategory.setText(category != null && !category.isEmpty() ? category : "N/A");

        String dateOpen = task.getResult().child("jobDateUpload").getValue(String.class);
        String dateClose = task.getResult().child("jobDateClose").getValue(String.class);
        if (dateOpen != null && !dateOpen.isEmpty() && dateClose != null && !dateClose.isEmpty()){
            tvDate.setText(dateClose);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

            try {
                // Parse the date strings into Date objects
                Date openDate = dateFormat.parse(dateOpen);
                Date closeDate = dateFormat.parse(dateClose);

                // Calculate the difference in milliseconds
                long diffInMillis = closeDate.getTime() - openDate.getTime();

                // Convert milliseconds to days
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);

                if(diffInDays < 0){
                    tvDayLeft.setText("waktu habis");
                }else{
                    tvDayLeft.setText(diffInDays+" hari lagi");
                }

            } catch (ParseException e) {
                e.printStackTrace();

            }
        } else {
            tvDate.setText("N/A");
        }

        String jobImgUrl = task.getResult().child("jobImgUri").getValue(String.class);
        if(jobImgUrl.equals("default")){
            ivJobImage.setImageResource(R.drawable.no_image);
        } else{
            if(!PelangganDetailJobActivity.this.isDestroyed()){
                Glide.with(PelangganDetailJobActivity.this).load(jobImgUrl).into(ivJobImage);
            }
        }

        Integer salary = task.getResult().child("jobSalary").getValue(Integer.class);
        NumberFormat priceFormat = NumberFormat.getCurrencyInstance();
        priceFormat.setMaximumFractionDigits(0);
        priceFormat.setCurrency(Currency.getInstance("IDR"));
        String salaryFrequent = task.getResult().child("jobSalaryFrequent").getValue(String.class);
        if(salaryFrequent.equals("Daily")) {
            salaryFrequent = "Hari";
        } else if (salaryFrequent.equals("Weekly")) {
            salaryFrequent = "Minggu";
        } else if (salaryFrequent.equals("Monthly")) {
            salaryFrequent = "Bulan";
        }
        tvSalary.setText(salaryFrequent != null && !salaryFrequent.isEmpty() ? "Rp" + String.valueOf(priceFormat.format(salary)).replace("IDR","") + ".00 / " + salaryFrequent : "N/A");
    }

    private void setStatusColor(String status) {
        if (status == null || status.isEmpty()) return;

        switch (status.toUpperCase()) {
            case "OPEN":
                tvStatus.setText("TERBUKA");
                tvStatus.setBackground(getDrawable(R.drawable.shape_rounded_green_pill));
                break;
            case "ON GOING":
                tvStatus.setText("SEDANG BERJALAN");
                tvStatus.setBackground(getDrawable(R.drawable.shape_rounded_yellow_pill));
                break;
            case "ENDED":
                tvStatus.setText("SELESAI");
                tvStatus.setBackground(getDrawable(R.drawable.shape_rounded_red_pill));
                break;
            default:
                tvStatus.setTextColor(getResources().getColor(android.R.color.black));
                break;
        }
    }

    private void navigateToEditJobActivity(String jobId) {
        Intent intent = new Intent(PelangganDetailJobActivity.this, PelangganEditJobActivity.class);
        intent.putExtra("JOB_ID", jobId);
        startActivityForResult(intent, REQUEST_CODE_VALIDATION);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(PelangganDetailJobActivity.this, PelangganMainActivity.class);
        startActivity(intent);
        finish();
    }

}
