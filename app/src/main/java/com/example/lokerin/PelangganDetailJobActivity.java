package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class PelangganDetailJobActivity extends AppCompatActivity {

    private ImageView btnBack,ivProfileNavbar;
    private List<JobData> jobDataList;
    private String jobTitle, jobLocation, jobStatus;
    private User[] jobApplicants;
    private List<User> applicantsList;
    private TextView tvPageTitle, tvTitle, tvLocation;
    private Button btnAction, btnDelete;
    private RecyclerView rvApplicants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_detail_job);

        Intent intent = getIntent();
        jobTitle = intent.getStringExtra("jobTitle");
        jobLocation = intent.getStringExtra("jobLocation");
        jobStatus = intent.getStringExtra("jobStatus");
        jobApplicants = (User[]) intent.getSerializableExtra("jobApplicants");
        applicantsList = Arrays.asList(jobApplicants);
        tvTitle = findViewById(R.id.label1);
        tvLocation = findViewById(R.id.label2);

        btnAction = findViewById(R.id.btn_action);
        btnDelete = findViewById(R.id.btn_delete);

        tvTitle.setText(jobTitle);
        tvLocation.setText(jobLocation);

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
        } else if ("Applyable".equals(jobStatus)) { //buat pekerja seharusnya
            btnAction.setText("Daftar");
            btnAction.setOnClickListener(v -> showFinishJobConfirmationDialog());
            btnDelete.setVisibility(View.GONE);
            rvApplicants.setVisibility(View.GONE);
        } else{
            btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
        }
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

    private void backPage() {
        finish();
    }

}
