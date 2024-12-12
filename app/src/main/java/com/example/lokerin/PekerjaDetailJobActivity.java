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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PekerjaDetailJobActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageView btnBack;
    private TextView tvPageTitle, tvTitle, tvCategory, tvProvince, tvRegency, tvDate, tvSalary, tvDescription;
    private String jobId, jobStatus;
    private Button btnAction, btnChat;

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
        databaseReference = firebaseDatabase.getReference().child("jobs").child(jobId);
        jobStatus = databaseReference.child("jobStatus").toString();

        tvTitle = findViewById(R.id.tv_title_pekerja);
        tvCategory = findViewById(R.id.tv_category_pekerja);
        tvProvince = findViewById(R.id.tv_province_pekerja);
        tvRegency = findViewById(R.id.tv_regency_pekerja);
        tvDate = findViewById(R.id.tv_date_pekerja);
        tvSalary = findViewById(R.id.tv_salary_pekerja);
        tvDescription = findViewById(R.id.tv_description_pekerja);

        btnChat = findViewById(R.id.btn_chat_pekerjaDetailJobPage);
        btnAction = findViewById(R.id.bt_action_pekerjaDetailJobPage);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Detail Pekerjaan");

        if ("Active".equals(jobStatus) /* && belum apply */) {
            btnAction.setText("Daftar Pekerjaan");
            btnAction.setOnClickListener(v -> showApplyJobConfirmationDialog());
        } else if ("On Going".equals(jobStatus) /* && belum di accept */) {
            btnAction.setText("Batalkan Pekerjaan");
            btnAction.setOnClickListener(v -> showCancelJobConfirmationDialog());
        } else if ("On Going2".equals(jobStatus)  /* && sudah di accept */) {
            btnAction.setEnabled(false);
        } else if ("Ended".equals(jobStatus)) {
            btnChat.setVisibility(View.GONE);
            btnAction.setVisibility(View.GONE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PekerjaDetailJobActivity.this, ChatActivity.class);
                intent.putExtra("userid", "Ming ming");
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

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            //Apply to Job
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showCancelJobConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Batalkan Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            //Cencel Job
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

        String description = task.getResult().child("jobDescription").getValue(String.class);
        tvDescription.setText(description != null && !description.isEmpty() ? description : "N/A");
    }

    private void backPage() {
        finish();
    }
}