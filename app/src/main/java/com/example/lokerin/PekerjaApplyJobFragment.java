package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class PekerjaApplyJobFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsRef;

    private TextView tvViewAll;
    private RecyclerView rvCategorizedJobs, rvRecommendedJobs;
    private String currentUserId;
    private int counter;

    private List<Job> jobList = new ArrayList<>();
    private ListJobAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pekerja_applyjob, container, false);

        FirebaseApp.initializeApp(requireContext());
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        jobsRef = firebaseDatabase.getReference("jobs");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            currentUserId = "noId";
        }

        tvViewAll = view.findViewById(R.id.viewAllTextView);
        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewAllClick(view);
            }
        });

        rvCategorizedJobs = view.findViewById(R.id.recyclerViewCategory);
        rvCategorizedJobs.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        List<CategoryData> items = new ArrayList<>();
        items.add(new CategoryData(R.drawable.img_barber, "Barber"));
        items.add(new CategoryData(R.drawable.img_builder, "Builder"));
        items.add(new CategoryData(R.drawable.img_driver, "Driver"));
        items.add(new CategoryData(R.drawable.img_gardener, "Gardener"));
        items.add(new CategoryData(R.drawable.img_maid, "Maid"));
        items.add(new CategoryData(R.drawable.img_peddler, "Peddler"));
        items.add(new CategoryData(R.drawable.img_porter, "Porter"));
        items.add(new CategoryData(R.drawable.img_secretary, "Secretary"));

        ListCategoryAdapter adapter2 = new ListCategoryAdapter(getActivity(), items);
        rvCategorizedJobs.setAdapter(adapter2);

        rvRecommendedJobs = view.findViewById(R.id.recyclerView);
        rvRecommendedJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListJobAdapter(getActivity(), jobList);
        rvRecommendedJobs.setAdapter(adapter);

        fetchJobsFromFirebase();

        return view;
    }

    public void onViewAllClick(View view) {
        Intent intent = new Intent(getActivity(), PekerjaRecommendJobActivity.class);
        startActivity(intent);
    }

    private void fetchJobsFromFirebase() {
        counter = 0;
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Job job = jobSnapshot.getValue(Job.class);
                    if (job != null) {
                        //FILTER BY MAPPING RECOMMEND ACTIVITY + ONLY 3
                        jobList.add(job);
                        counter++;
                    }
                    if (counter == 5){
                        break;
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

}