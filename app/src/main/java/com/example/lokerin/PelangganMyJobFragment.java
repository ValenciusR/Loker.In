package com.example.lokerin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class PelangganMyJobFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelanggan_myjob, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ListJobAdapter adapter = new ListJobAdapter(getActivity(), getJobDataList());
        recyclerView.setAdapter(adapter);

        return view;
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

        return dataList;
    }
}
