package com.example.lokerin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PelangganAddJobFragment extends Fragment {

    private Button btnDaily, btnWeekly, btnMonthly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pelanggan_addjob, container, false);

        // Initialize buttons after inflating the layout
        btnDaily = view.findViewById(R.id.btn_dailySalary_addJob);
        btnWeekly = view.findViewById(R.id.btn_weeklySalary_addJob);
        btnMonthly = view.findViewById(R.id.btn_monthlySalary_addJob);

        // Set click listeners for each button
        btnDaily.setOnClickListener(v -> setSelectedButton(btnDaily));
        btnWeekly.setOnClickListener(v -> setSelectedButton(btnWeekly));
        btnMonthly.setOnClickListener(v -> setSelectedButton(btnMonthly));

        return view;
    }

    private void setSelectedButton(Button selectedButton) {
        btnDaily.setSelected(selectedButton == btnDaily);
        btnWeekly.setSelected(selectedButton == btnWeekly);
        btnMonthly.setSelected(selectedButton == btnMonthly);
    }
}