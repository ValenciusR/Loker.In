package com.example.lokerin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PelangganMyJobFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsRef;

    private RecyclerView rvJobs;
    private EditText etSearchBar;
    private String currentUserId;
    private ImageView btnDropdownOpen, btnDropdownClose;
    private LinearLayout linearLayoutFilter;
    private TextView btnSortAz, btnSortZa, btnSortDate;
    private TextView btnFilterOpen, btnFilterOnGoing, btnFilterEnded;
    private List<Job> jobList = new ArrayList<>();
    private ListJobAdapter adapter;

    private String activeSort = null;
    private String activeFilter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelanggan_myjob, container, false);

        btnDropdownOpen = view.findViewById(R.id.btn_dropdown_filter);
        btnDropdownClose = view.findViewById(R.id.btn_dropdown_filterClose);
        linearLayoutFilter = view.findViewById(R.id.linear_layout_filter);
        btnSortAz = view.findViewById(R.id.btn_filter_az);
        btnSortZa = view.findViewById(R.id.btn_filter_za);
        btnSortDate = view.findViewById(R.id.btn_filter_tanggal);
        btnFilterOpen = view.findViewById(R.id.btn_filter_status_open);
        btnFilterOnGoing = view.findViewById(R.id.btn_filter_status_ongoing);
        btnFilterEnded = view.findViewById(R.id.btn_filter_status_end);

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

        rvJobs = view.findViewById(R.id.recyclerView);
        rvJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListJobAdapter(getActivity(), jobList);
        rvJobs.setAdapter(adapter);

        etSearchBar = view.findViewById(R.id.search_bar);
        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAndSortJobs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        setButtonListeners();
        fetchJobsFromFirebase();

        return view;
    }

    private void setButtonListeners() {
        btnDropdownOpen.setOnClickListener(v -> {
            linearLayoutFilter.setVisibility(View.VISIBLE);
            btnDropdownOpen.setVisibility(View.GONE);
            btnDropdownClose.setVisibility(View.VISIBLE);
        });

        btnDropdownClose.setOnClickListener(v -> hideFilterOptions());

        btnSortAz.setOnClickListener(v -> handleSortSelection(btnSortAz, "az"));
        btnSortZa.setOnClickListener(v -> handleSortSelection(btnSortZa, "za"));
        btnSortDate.setOnClickListener(v -> handleSortSelection(btnSortDate, "date"));

        btnFilterOpen.setOnClickListener(v -> handleFilterSelection(btnFilterOpen, "OPEN"));
        btnFilterOnGoing.setOnClickListener(v -> handleFilterSelection(btnFilterOnGoing, "ON GOING"));
        btnFilterEnded.setOnClickListener(v -> handleFilterSelection(btnFilterEnded, "ENDED"));
    }

    private void handleSortSelection(TextView selectedButton, String sortType) {
        resetSortButtonStyles();

        selectedButton.setBackgroundResource(R.drawable.shape_rounded);
        selectedButton.setTextColor(getResources().getColor(R.color.blue, null));

        activeSort = sortType;

        filterAndSortJobs(etSearchBar.getText().toString());
        hideFilterOptions();
    }

    private void handleFilterSelection(TextView selectedButton, String filterType) {
        resetFilterButtonStyles();

        selectedButton.setBackgroundResource(R.drawable.shape_rounded);
        selectedButton.setTextColor(getResources().getColor(R.color.blue, null));

        activeFilter = filterType;

        filterAndSortJobs(etSearchBar.getText().toString());
        hideFilterOptions();
    }

    private void filterAndSortJobs(String query) {
        List<Job> filteredList = new ArrayList<>();

        for (Job job : jobList) {
            boolean matchesSearch = job.getJobTitle() != null && job.getJobTitle().toLowerCase().contains(query.toLowerCase());
            boolean matchesUser = !"noId".equalsIgnoreCase(currentUserId) && currentUserId.equalsIgnoreCase(job.getJobMakerId());
            boolean matchesFilter = activeFilter == null || activeFilter.equalsIgnoreCase(job.getJobStatus());

            if (matchesSearch && matchesUser && matchesFilter) {
                filteredList.add(job);
            }
        }

        if ("az".equalsIgnoreCase(activeSort)) {
            Collections.sort(filteredList, Comparator.comparing(job -> job.getJobTitle() != null ? job.getJobTitle().toLowerCase() : ""));
        } else if ("za".equalsIgnoreCase(activeSort)) {
            Collections.sort(filteredList, (job1, job2) -> {
                String title1 = job1.getJobTitle() != null ? job1.getJobTitle().toLowerCase() : "";
                String title2 = job2.getJobTitle() != null ? job2.getJobTitle().toLowerCase() : "";
                return title2.compareTo(title1);
            });
        } else if ("date".equalsIgnoreCase(activeSort)) {
            Collections.sort(filteredList, (job1, job2) -> {
                if (job1.getJobDateUpload() == null) return -1;
                if (job2.getJobDateUpload() == null) return 1;
                return job1.getJobDateUpload().compareTo(job2.getJobDateUpload());
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
                        jobList.add(job);
                    }
                }
                filterAndSortJobs(etSearchBar.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch jobs: " + error.getMessage());
            }
        });
    }

    //RESET STYLE CLICK BTN FILTER N SORT
    private void hideFilterOptions() {
        linearLayoutFilter.setVisibility(View.GONE);
        btnDropdownOpen.setVisibility(View.VISIBLE);
        btnDropdownClose.setVisibility(View.GONE);
    }

    private void resetSortButtonStyles() {
        btnSortAz.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
        btnSortAz.setTextColor(getResources().getColor(R.color.white_80, null));

        btnSortZa.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
        btnSortZa.setTextColor(getResources().getColor(R.color.white_80, null));

        btnSortDate.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
        btnSortDate.setTextColor(getResources().getColor(R.color.white_80, null));
    }

    private void resetFilterButtonStyles() {
        btnFilterOpen.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
        btnFilterOpen.setTextColor(getResources().getColor(R.color.white_80, null));

        btnFilterOnGoing.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
        btnFilterOnGoing.setTextColor(getResources().getColor(R.color.white_80, null));

        btnFilterEnded.setBackgroundResource(R.drawable.shape_rounded_white_border_clear);
        btnFilterEnded.setTextColor(getResources().getColor(R.color.white_80, null));
    }
}
