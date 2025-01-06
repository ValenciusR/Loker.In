package com.example.lokerin;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.concurrent.atomic.AtomicBoolean;

public class PekerjaDetailJobActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsReference;
    private DatabaseReference userReference;

    private ImageView btnBack, ivProfileNavbar;;
    private TextView tvPageTitle, tvTitle, tvStatus, tvCategory, tvProvince, tvDate, tvSalary, tvDescription;
    private String jobId, jobStatus, jobMakerId;
    private Button btnAction, btnChat;
    private ArrayList<String> applicantsList, workersList;

    private FrameLayout loadingView;
    private RelativeLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_detail_job);

        Intent intent = getIntent();
        jobId = intent.getStringExtra("jobId");
        if (jobId == null) {
            Toast.makeText(this, "ID pekerjaan tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        jobsReference = firebaseDatabase.getReference().child("jobs").child(jobId);
        mAuth = FirebaseAuth.getInstance();

        jobsReference.child("jobStatus").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                jobStatus = task.getResult().getValue(String.class);
                if (jobStatus != null) {
                    if ("OPEN".equalsIgnoreCase(jobStatus)) {
                        jobsReference.child("jobApplicants").get().addOnCompleteListener(applicantsTask -> {
                            if (applicantsTask.isSuccessful()) {
                                boolean isApplicant;
                                AtomicBoolean isWorker = new AtomicBoolean(false);

                                if (applicantsTask.getResult().exists()) {
                                    ArrayList<String> jobApplicantsList = (ArrayList<String>) applicantsTask.getResult().getValue();
                                    if (jobApplicantsList != null && jobApplicantsList.contains(mAuth.getUid())) {
                                        isApplicant = true;
                                    } else {
                                        isApplicant = false;
                                    }
                                } else {
                                    isApplicant = false;
                                }

                                jobsReference.child("jobWorkers").get().addOnCompleteListener(workersTask -> {
                                    if (workersTask.isSuccessful()) {
                                        if (workersTask.getResult().exists()) {
                                            ArrayList<String> jobWorkersList = (ArrayList<String>) workersTask.getResult().getValue();
                                            if (jobWorkersList != null && jobWorkersList.contains(mAuth.getUid())) {
                                                isWorker.set(true);
                                            }
                                        }

                                        if (isWorker.get()) {
                                            btnAction.setText("TERDAFTAR");
                                            btnAction.setEnabled(false);
                                            btnAction.setBackgroundResource(R.drawable.shape_grey_rounded_border);
                                        } else if (isApplicant) {
                                            btnAction.setText("Batalkan Pekerjaan");
                                            btnAction.setOnClickListener(v -> showCancelJobConfirmationDialog());
                                        } else {
                                            btnAction.setText("Daftar Pekerjaan");
                                            btnAction.setOnClickListener(v -> showApplyJobConfirmationDialog());
                                        }
                                    } else {
                                        Toast.makeText(this, "Gagal memeriksa daftar pekerja", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(this, "Gagal memeriksa daftar pelamar", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if ("ON GOING".equalsIgnoreCase(jobStatus)) {
                        btnAction.setText("Pekerjaan Sedang Berjalan");
                        btnAction.setEnabled(false);
                        btnAction.setBackgroundResource(R.drawable.shape_grey_rounded_border);
                    } else if ("ENDED".equalsIgnoreCase(jobStatus)) {
                        btnChat.setVisibility(View.GONE);
                        btnAction.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(this, "Status pekerjaan sudah kadaluwarsa", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Gagal mendapatkan status pekerjaan", Toast.LENGTH_SHORT).show();
            }
        });

        jobsReference.child("jobMakerId").get().addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                jobMakerId = task2.getResult().getValue(String.class);
            }
        });

        tvTitle = findViewById(R.id.tv_title);
        tvStatus = findViewById(R.id.tv_status);
        tvProvince = findViewById(R.id.tv_province);
        tvCategory = findViewById(R.id.tv_category);
        tvDate = findViewById(R.id.tv_date);
        tvSalary = findViewById(R.id.tv_salary);
        tvDescription = findViewById(R.id.tv_description_pekerja);

        btnChat = findViewById(R.id.btn_chat_pekerjaDetailJobPage);
        btnAction = findViewById(R.id.btn_action_pekerjaDetailJobPage);

        loadingView = findViewById(R.id.loading_overlay);
        mainView = findViewById(R.id.main);

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PekerjaDetailJobActivity.this, ChatActivity.class);
                intent.putExtra("userid", jobMakerId);
                startActivity(intent);
            }
        });

        getJobData();

    }

    private void showApplyJobConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Daftar Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        dialog.show();

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();

            mainView.setClickable(false);
            loadingView.setVisibility(View.VISIBLE);

            jobsReference.get().addOnCompleteListener(task -> {
                loadingView.setVisibility(View.GONE);
                mainView.setClickable(true);
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();

                    applicantsList = new ArrayList<>();
                    if (snapshot.child("jobApplicants").exists()) {
                        applicantsList = (ArrayList<String>) snapshot.child("jobApplicants").getValue();
                    }
                    if (applicantsList == null) {
                        applicantsList = new ArrayList<>();
                    }

                    workersList = new ArrayList<>();
                    if (snapshot.child("jobWorkers").exists()) {
                        workersList = (ArrayList<String>) snapshot.child("jobWorkers").getValue();
                    }
                    if (workersList == null) {
                        workersList = new ArrayList<>();
                    }

                    String currentUserId = mAuth.getUid();
                    if (currentUserId != null && !applicantsList.contains(currentUserId) && !workersList.contains(currentUserId)) {
                        applicantsList.add(currentUserId);
                        jobsReference.child("jobApplicants").setValue(applicantsList)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(this, "Berhasil mendaftar pada pekerjaan ini", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Gagal mendaftar pada pekerjaan ini", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Anda sudah mendaftar pada pekerjaan ini!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Gagal mengambil data pekerjaan", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showCancelJobConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Batalkan Pendaftaran?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        dialog.show();

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            dialog.dismiss();

            mainView.setClickable(false);
            loadingView.setVisibility(View.VISIBLE);

            jobsReference.get().addOnCompleteListener(task -> {
                loadingView.setVisibility(View.GONE);
                mainView.setClickable(true);
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();

                    ArrayList<String> applicantsList = new ArrayList<>();
                    if (snapshot.child("jobApplicants").exists()) {
                        applicantsList = (ArrayList<String>) snapshot.child("jobApplicants").getValue();
                    }
                    if (applicantsList == null) {
                        applicantsList = new ArrayList<>();
                    }

                    String currentUserId = mAuth.getUid();
                    if (currentUserId != null && applicantsList.contains(currentUserId)) {
                        applicantsList.remove(currentUserId);
                        jobsReference.child("jobApplicants").setValue(applicantsList)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Toast.makeText(this, "Pendaftaran pada pekerjaan ini berhasil dibatalkan", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(this, "Gagal membatalkan pendaftaran pada pekerjaan ini", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Anda belum terdaftar pada pekerjaan ini", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Gagal mengambil data pekerjaan", Toast.LENGTH_SHORT).show();
                }
            });
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
                    Toast.makeText(PekerjaDetailJobActivity.this, "Data pekerjaan tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PekerjaDetailJobActivity.this, "Error mengambil data pekerjaan.", Toast.LENGTH_SHORT).show();
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

        String date = task.getResult().child("jobDateUpload").getValue(String.class);
        tvDate.setText(date != null && !date.isEmpty() ? date : "N/A");

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

        String description = task.getResult().child("jobDescription").getValue(String.class);
        tvDescription.setText(description != null ? description : "");
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

    private void backPage() {
        finish();
    }
}