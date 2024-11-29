package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PekerjaDetailJobActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvPageTitle, tvTitle, tvLocation, tvJobDescription, tvJobSalary, tvEndDate;
    private String jobTitle, jobLocation, jobStatus, jobDescription, jobSalary, jobEndDate;
    private Button btnAction, btnChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_detail_job);

        Intent intent = getIntent();
        jobTitle = intent.getStringExtra("jobTitle");
        jobLocation = intent.getStringExtra("jobLocation");
        jobStatus = intent.getStringExtra("jobStatus");
        jobDescription = intent.getStringExtra("jobDescription");
        jobSalary = intent.getStringExtra("salary");
        jobEndDate = intent.getStringExtra("jobEndDate");

        // Text View
        tvTitle = findViewById(R.id.label1);
        tvLocation = findViewById(R.id.label2);
        tvJobDescription = findViewById(R.id.tv_jobDescription_pekerjaDetailJobPage);
        tvJobSalary = findViewById(R.id.txt_salary);
        tvEndDate = findViewById(R.id.label3);

        tvTitle.setText(jobTitle);
        tvLocation.setText(jobLocation);
        tvJobDescription.setText(jobDescription);
        tvJobSalary.setText(jobSalary);
        tvEndDate.setText(jobEndDate);

        // Button
        btnChat = findViewById(R.id.bt_chat_pekerjaDetailJobPage);
        btnAction = findViewById(R.id.bt_action_pekerjaDetailJobPage);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Detil Pekerjaan");
        tvPageTitle.setText(jobStatus);

        if ("Active".equals(jobStatus) /* && belum apply */) {
            btnAction.setText("Daftar Pekerjaan");
            btnAction.setOnClickListener(v -> showApplyJobConfirmationDialog());
            tvPageTitle.setText("Daftar Pekerjaan");
        } else if ("On Going".equals(jobStatus) /* && belum di accept */) {
            btnAction.setText("Batalkan Pekerjaan");
            btnAction.setOnClickListener(v -> showCancelJobConfirmationDialog());
            tvPageTitle.setText("Pekerjaan Saya");
        } else if ("On Going2".equals(jobStatus)  /* && sudah di accept */) {
            btnAction.setEnabled(false);
            tvPageTitle.setText("Pekerjaan Saya");
        } else if ("Ended".equals(jobStatus)) {
            btnChat.setVisibility(View.GONE);
            btnAction.setVisibility(View.GONE);
            tvPageTitle.setText("Pekerjaan Saya");
        }

        // on click chat button -> go to chat with pelanggan
        //btnChat.setOnClickListener(v -> startChatIntent());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });

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

    private void backPage() {
        startActivity(new Intent(PekerjaDetailJobActivity.this, PekerjaMainActivity.class));
        finish();
    }
}