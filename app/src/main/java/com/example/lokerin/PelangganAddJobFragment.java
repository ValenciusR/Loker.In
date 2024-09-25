package com.example.lokerin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class PelangganAddJobFragment extends Fragment {

    private Button btnDaily, btnWeekly, btnMonthly;
    private Spinner spinnerProvince, spinnerRegency;
    private ArrayAdapter<CharSequence> regencyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pelanggan_addjob, container, false);

        btnDaily = view.findViewById(R.id.btn_dailySalary_addJob);
        btnWeekly = view.findViewById(R.id.btn_weeklySalary_addJob);
        btnMonthly = view.findViewById(R.id.btn_monthlySalary_addJob);
        spinnerProvince = view.findViewById(R.id.spinner_province_addJob);
        spinnerRegency = view.findViewById(R.id.spinner_regency_addJob);

        btnDaily.setOnClickListener(v -> setSelectedButton(btnDaily));
        btnWeekly.setOnClickListener(v -> setSelectedButton(btnWeekly));
        btnMonthly.setOnClickListener(v -> setSelectedButton(btnMonthly));

        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 0 && position < 3) {
                    spinnerRegency.setEnabled(true);
                    updateRegencySpinner(position);
                } else {
                    spinnerRegency.setEnabled(false);
                    spinnerRegency.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                spinnerRegency.setEnabled(false);
            }
        });

        return view;
    }

    private void setSelectedButton(Button selectedButton) {
        btnDaily.setSelected(selectedButton == btnDaily);
        btnWeekly.setSelected(selectedButton == btnWeekly);
        btnMonthly.setSelected(selectedButton == btnMonthly);
    }

    private void updateRegencySpinner(int provincePosition) {
        int regencyArrayId;

        switch (provincePosition) {
            case 0: // West Java
                regencyArrayId = R.array.regency_west_java;
                break;
            case 1: // Central Java
                regencyArrayId = R.array.regency_central_java;
                break;
            case 2: // East Java
                regencyArrayId = R.array.regency_east_java;
                break;
            default:
                regencyArrayId = R.array.empty_array;
        }

        regencyAdapter = ArrayAdapter.createFromResource(getContext(),
                regencyArrayId, R.layout.spinner_item);
        regencyAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerRegency.setAdapter(regencyAdapter);
    }
}
