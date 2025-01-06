package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PekerjaRecommendJobActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsRef;

    private ImageView btnBack, ivProfileNavbar, btnFilter, btnFilterClose;
    private LinearLayout layoutFilter,layoutFilterStatus;
    private Integer filterNumber;
    private TextView tvPageTitle,btnFilterAZ, btnFilterZA,btnFilterTanggal;
    private RecyclerView rvJobs;
    private EditText etSearchBar;
    private String currentUserId;

    private List<Job> jobList = new ArrayList<>();
    private ListJobAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_recommend_job);

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        jobsRef = firebaseDatabase.getReference("jobs");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            currentUserId = "noId";
        }

        btnBack = findViewById(R.id.btn_back_toolbar);

        filterNumber = 0;
        btnFilter = findViewById(R.id.btn_dropdown_filter);
        btnFilterClose = findViewById(R.id.btn_dropdown_filterClose);
        btnFilterAZ = findViewById(R.id.btn_filter_az);
        btnFilterZA = findViewById(R.id.btn_filter_za);
        btnFilterTanggal = findViewById(R.id.btn_filter_tanggal);
        layoutFilter = findViewById(R.id.linear_layout_filter);
        layoutFilterStatus = findViewById(R.id.layout_filter_status);
        layoutFilterStatus.setVisibility(View.GONE);
        btnFilterAZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterFunction(1);
            }
        });
        btnFilterZA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterFunction(2);
            }
        });
        btnFilterTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterFunction(3);
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutFilter.setVisibility(View.VISIBLE);
                btnFilter.setVisibility(View.GONE);
                btnFilterClose.setVisibility(View.VISIBLE);
            }
        });
        btnFilterClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutFilter.setVisibility(View.GONE);
                btnFilter.setVisibility(View.VISIBLE);
                btnFilterClose.setVisibility(View.GONE);
            }
        });

        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Semua Pekerjaan");
        ivProfileNavbar.setVisibility(View.GONE);

        rvJobs = findViewById(R.id.recyclerView);
        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListJobAdapter(this, jobList);
        rvJobs.setAdapter(adapter);

        etSearchBar = findViewById(R.id.search_bar);
        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterJobs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchJobsFromFirebase();

    }

    private void filterJobs(String query) {
        List<Job> filteredList = new ArrayList<>();
        for (Job job : jobList) {
            if (job.getJobTitle() != null && job.getJobTitle().toLowerCase().contains(query.toLowerCase())) {
                if ("OPEN".equalsIgnoreCase(job.getJobStatus())) {
                    filteredList.add(job);
                }
            }
        }
        adapter.updateList(filteredList);
    }

    private void sortJobs() {
        List<Job> filteredList = new ArrayList<>();
        for (Job job : jobList) {
            if (job.getJobTitle() != null) {
                if ("OPEN".equalsIgnoreCase(job.getJobStatus())) {
                    filteredList.add(job);
                }
            }
        }
        if(filterNumber == 1){
            Collections.sort(filteredList, new Comparator<Job>() {
                @Override
                public int compare(Job job1, Job job2) {
                    return job1.getJobTitle().compareToIgnoreCase(job2.getJobTitle());
                }
            });
        } else if (filterNumber == 2) {
            Collections.sort(filteredList, new Comparator<Job>() {
                @Override
                public int compare(Job job1, Job job2) {
                    return job2.getJobTitle().compareToIgnoreCase(job1.getJobTitle());
                }
            });
        } else if (filterNumber == 3){
            Collections.sort(filteredList, new Comparator<Job>() {
                @Override
                public int compare(Job job1, Job job2) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                    try {
                        Date date1 = sdf.parse(job1.getJobDateUpload());
                        Date date2 = sdf.parse(job2.getJobDateUpload());
                        return date2.compareTo(date1); // Newest date first
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0; // Treat as equal if parsing fails
                    }
                }
            });
        }

        adapter.updateList(filteredList);
    }

    private void fetchJobsFromFirebase() {
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Job job = jobSnapshot.getValue(Job.class);
                    if (job != null) {
                        if ("OPEN".equalsIgnoreCase(job.getJobStatus())) {
                            jobList.add(job);
                        }
                    }
                }
                adapter.updateList(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch jobs: " + error.getMessage());
            }
        });
    }

    private void backPage() {
        finish();
    }

    private void filterFunction(int filter){
        if(filter != filterNumber){
            filterNumber = filter;
            switch(filter){
                case 1:
                    btnFilterAZ.setBackgroundResource(R.drawable.shape_rounded);
                    btnFilterAZ.setTextColor(getResources().getColor(R.color.blue));
                    btnFilterZA.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
                    btnFilterZA.setTextColor(getResources().getColor(R.color.white_80));
                    btnFilterTanggal.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
                    btnFilterTanggal.setTextColor(getResources().getColor(R.color.white_80));
                    break;
                case 2:
                    btnFilterZA.setBackgroundResource(R.drawable.shape_rounded);
                    btnFilterZA.setTextColor(getResources().getColor(R.color.blue));
                    btnFilterAZ.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
                    btnFilterAZ.setTextColor(getResources().getColor(R.color.white_80));
                    btnFilterTanggal.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
                    btnFilterTanggal.setTextColor(getResources().getColor(R.color.white_80));
                    break;
                case 3:
                    btnFilterTanggal.setBackgroundResource(R.drawable.shape_rounded);
                    btnFilterTanggal.setTextColor(getResources().getColor(R.color.blue));
                    btnFilterAZ.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
                    btnFilterAZ.setTextColor(getResources().getColor(R.color.white_80));
                    btnFilterZA.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
                    btnFilterZA.setTextColor(getResources().getColor(R.color.white_80));
                    break;
            }
        }else{
            btnFilterAZ.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
            btnFilterAZ.setTextColor(getResources().getColor(R.color.white_80));
            btnFilterZA.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
            btnFilterZA.setTextColor(getResources().getColor(R.color.white_80));
            btnFilterTanggal.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
            btnFilterTanggal.setTextColor(getResources().getColor(R.color.white_80));
            filterNumber = 0;
        }
        sortJobs();

    }
}