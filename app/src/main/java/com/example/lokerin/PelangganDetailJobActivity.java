package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PelangganDetailJobActivity extends AppCompatActivity {

    private List<JobData> jobDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_detail_job);

        Intent intent = getIntent();
        String jobTitle = intent.getStringExtra("jobTitle");
        String jobLocation = intent.getStringExtra("jobLocation");
        String jobStatus = intent.getStringExtra("jobStatus");
        User[] jobApplicants = (User[]) intent.getSerializableExtra("jobApplicants");
        List<User> applicantsList = Arrays.asList(jobApplicants);

        //TEXT VIEW
        TextView titleView = findViewById(R.id.label1);
        titleView.setText(jobTitle);
        TextView locationView = findViewById(R.id.label2);
        locationView.setText(jobLocation);

        //BUTTON
        Button actionButton = findViewById(R.id.btn_action);
        Button deleteButton = findViewById(R.id.btn_delete);

        if ("Active".equals(jobStatus)) {
            deleteButton.setVisibility(View.GONE);
            actionButton.setText("Confirm Book");
            actionButton.setOnClickListener(v -> showConfirmBookConfirmationDialog());
        } else if ("On Going".equals(jobStatus)) {
            deleteButton.setVisibility(View.GONE);
            actionButton.setText("Finish Job");
            actionButton.setOnClickListener(v -> showFinishJobConfirmationDialog());
        } else if ("Ended".equals(jobStatus)) {
            deleteButton.setVisibility(View.GONE);
            actionButton.setVisibility(View.GONE);
        }

        //POP UP FUNCTION
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListUserAdapter adapter = new ListUserAdapter(applicantsList, jobStatus);
        recyclerView.setAdapter(adapter);
    }

    private void showDeleteConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Delete Job?");

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
        title.setText("Confirm Book?");

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
        title.setText("Finish Job?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            //Change Status of the Job
            dialog.dismiss();
        });

        dialog.show();
    }

}
