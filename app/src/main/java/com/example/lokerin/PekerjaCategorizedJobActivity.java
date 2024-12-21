package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;

public class PekerjaCategorizedJobActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsRef;

    private ImageView btnBack, ivProfileNavbar;
    private String category, currentUserId;
    private TextView tvPageTitle;
    private RecyclerView rvJobs;
    private EditText etSearchBar;

    private List<Job> jobList = new ArrayList<>();
    private ListJobAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_categorized_job);

        Intent intent = getIntent();
        category = intent.getStringExtra("category");

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
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        if (category != null) {
            tvPageTitle.setText(category);
        }

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
                if (category.equalsIgnoreCase(job.getJobCategory()) && "OPEN".equalsIgnoreCase(job.getJobStatus())) {
                    filteredList.add(job);
                }
            }
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
                        if (category.equalsIgnoreCase(job.getJobCategory()) && "OPEN".equalsIgnoreCase(job.getJobStatus())) {
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
        startActivity(new Intent(PekerjaCategorizedJobActivity.this, PekerjaMainActivity.class));
        finish();
    }
}