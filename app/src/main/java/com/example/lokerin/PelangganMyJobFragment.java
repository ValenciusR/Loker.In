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
        ListJobAdapter adapter = new ListJobAdapter(getJobDataList());
        recyclerView.setAdapter(adapter);

        return view;
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
