package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PekerjaRecommendJobActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvPageTitle;
    private RecyclerView rvJobs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_recommend_job);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Rekomendasi Pekerjaan");

        rvJobs = findViewById(R.id.recyclerView);
        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        ListJobAdapter adapter = new ListJobAdapter(this, getJobDataList());
        rvJobs.setAdapter(adapter);
    }

    private List<JobData> getJobDataList() {
        List<JobData> dataList = new ArrayList<>();
        List<User> applicants1 = new ArrayList<>();
        applicants1.add(new User("bangjago@gmail.com", "BangJago"));
        applicants1.add(new User("jagoan@gmail.com", "Jagoan"));

        dataList.add(new JobData("Job Title 1", "Location 1", "Date 1", "Category 1", "Open", applicants1));
        dataList.add(new JobData("Job Title 2", "Location 2", "Date 2", "Category 2", "Active", new ArrayList<>()));
        dataList.add(new JobData("Job Title 3", "Location 2", "Date 2", "Category 2", "On Going", applicants1));
        dataList.add(new JobData("Job Title 4", "Location 2", "Date 2", "Category 2", "Ended", new ArrayList<>()));
        dataList.add(new JobData("Applicant Open", "Location 2", "Date 2", "Category 2", "Open", applicants1));
        dataList.add(new JobData("Applicant Active", "Location 2", "Date 2", "Category 2", "Active", applicants1));;
        dataList.add(new JobData("Applicant On Going", "Location 2", "Date 2", "Category 2", "On Going", applicants1));
        dataList.add(new JobData("Applicant Ended", "Location 2", "Date 2", "Category 2", "Ended", applicants1));

        List<JobData> filteredList = new ArrayList<>();
        for (JobData job : dataList) {
            if (!job.getJobStatus().equalsIgnoreCase("On Going") && !job.getJobStatus().equalsIgnoreCase("Ended")) {
                JobData tempJob = job;
                tempJob.setJobStatus("Applyable");
                filteredList.add(tempJob);
            }
        }

        return filteredList;
    }

    private void backPage() {
        startActivity(new Intent(PekerjaRecommendJobActivity.this, PekerjaMainActivity.class));
        finish();
    }
}