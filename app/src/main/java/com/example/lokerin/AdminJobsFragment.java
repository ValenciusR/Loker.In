package com.example.lokerin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminJobsFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsRef;

    private RecyclerView rvJobs;
    private EditText etSearchBar;

    private List<JobData> jobDataList = new ArrayList<>();
    private ListJobAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_jobs, container, false);

        FirebaseApp.initializeApp(requireContext());
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        jobsRef = firebaseDatabase.getReference("jobs");

        rvJobs = view.findViewById(R.id.recyclerView);
        rvJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListJobAdapter(getContext(), jobDataList);
        rvJobs.setAdapter(adapter);

        etSearchBar = view.findViewById(R.id.search_bar);
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

        return view;
    }

    private void filterJobs(String query) {
        List<JobData> filteredList = new ArrayList<>();
        for (JobData job : jobDataList) {
            if (job.getJobTitle() != null && job.getJobTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(job);
            }
        }
        adapter.updateList(filteredList);
    }

    private void fetchJobsFromFirebase() {
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobDataList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    JobData job = jobSnapshot.getValue(JobData.class);
                    if (job != null) {
                        jobDataList.add(job);
                    }
                }
                adapter.updateList(jobDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch jobs: " + error.getMessage());
            }
        });
    }

}
