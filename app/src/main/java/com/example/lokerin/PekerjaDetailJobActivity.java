package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PekerjaDetailJobActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_detail_job);

        Intent intent = getIntent();
        String jobTitle = intent.getStringExtra("jobTitle");
        String jobLocation = intent.getStringExtra("jobLocation");
        String jobStatus = intent.getStringExtra("jobStatus");
        //String jobDescription = intent.getStringExtra("jobDescription");
        //String jobSalary = intent.getStringExtra("salary");
        //String jobEndDate = intent.getStringExtra("jobEndDate");

        // Text View
        TextView titleView = findViewById(R.id.label1);
        titleView.setText(jobTitle);
        TextView locationView = findViewById(R.id.label2);
        locationView.setText(jobLocation);
        TextView jobDescriptionView = findViewById(R.id.tv_jobDescription_pekerjaDetailJobPage);
        // jobDescriptionView.setText(jobDescription);
        TextView jobSalaryView = findViewById(R.id.txt_salary);
        // jobSalaryView.setText(jobSalary);
        TextView pageTitle = findViewById(R.id.tv_page_toolbar);
        TextView endDateView = findViewById(R.id.label3);
        //

        // Button
        Button chatButton = findViewById(R.id.bt_chat_pekerjaDetailJobPage);
        Button actionButton = findViewById(R.id.bt_action_pekerjaDetailJobPage);

        if ("Active".equals(jobStatus) /* && belum apply */) {
            actionButton.setText("Apply Job");
            actionButton.setOnClickListener(v -> showApplyJobConfirmationDialog());
            pageTitle.setText("Apply Job");
        } else if ("On Going".equals(jobStatus) /* && belum di accept */) {
            actionButton.setText("Cancel Job");
            actionButton.setOnClickListener(v -> showCancelJobConfirmationDialog());
            pageTitle.setText("My Job");
        } else if ("On Going1".equals(jobStatus)  /* && sudah di accept */) {
            actionButton.setEnabled(false);
            pageTitle.setText("My Job");
        } else if ("Ended".equals(jobStatus)) {
            chatButton.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
            pageTitle.setText("My Job");
        }

        // on click chat button -> go to chat with pelanggan
        //chatButton.setOnClickListener(v -> startChatIntent());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showApplyJobConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Apply Job?");

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
        title.setText("Cancel Job?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            //Cencel Job
            dialog.dismiss();
        });

        dialog.show();
    }
}