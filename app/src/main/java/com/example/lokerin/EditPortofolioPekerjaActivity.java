package com.example.lokerin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EditPortofolioPekerjaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_portofolio_pekerja);

        RecyclerView recyclerView = this.findViewById(R.id.rv_jobs_editPortofolioPelangganPage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ListJobAdapter adapter = new ListJobAdapter(getJobDataList()); // Buat yang checkbox blm di check
        recyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private List<JobData> getJobDataList() {
        List<JobData> dataList = new ArrayList<>();
        dataList.add(new JobData("Job Title 1", "Location 1", "Date 1", "Category 1"));
        dataList.add(new JobData("Job Title 2", "Location 2", "Date 2", "Category 2"));
        dataList.add(new JobData("Job Title 1", "Location 1", "Date 1", "Category 1"));
        dataList.add(new JobData("Job Title 2", "Location 2", "Date 2", "Category 2"));
        dataList.add(new JobData("Job Title 1", "Location 1", "Date 1", "Category 1"));
        dataList.add(new JobData("Job Title 2", "Location 2", "Date 2", "Category 2"));
        dataList.add(new JobData("Job Title 1", "Location 1", "Date 1", "Category 1"));
        dataList.add(new JobData("Job Title 2", "Location 2", "Date 2", "Category 2"));
        dataList.add(new JobData("Job Title 1", "Location 1", "Date 1", "Category 1"));
        dataList.add(new JobData("Job Title 2", "Location 2", "Date 2", "Category 2"));
        dataList.add(new JobData("Job Title 1", "Location 1", "Date 1", "Category 1"));
        dataList.add(new JobData("Job Title 2", "Location 2", "Date 2", "Category 2"));
        return dataList;
    }
}