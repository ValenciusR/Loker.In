package com.example.lokerin;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PelangganAddJobFragment extends Fragment {

    private Button btnCategory, btnDaily, btnWeekly, btnMonthly, btnSubmit;
    private Spinner spinnerProvince, spinnerRegency;
    private EditText etJobTitle, etDescription, etSalary, etAddress;
    private String frequentSalary = "";
    private ArrayAdapter<CharSequence> regencyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pelanggan_addjob, container, false);

        btnCategory = view.findViewById(R.id.btn_category_addJob);
        btnDaily = view.findViewById(R.id.btn_dailySalary_addJob);
        btnWeekly = view.findViewById(R.id.btn_weeklySalary_addJob);
        btnMonthly = view.findViewById(R.id.btn_monthlySalary_addJob);
        btnSubmit = view.findViewById(R.id.btn_addJob);
        spinnerProvince = view.findViewById(R.id.spinner_province_addJob);
        spinnerRegency = view.findViewById(R.id.spinner_regency_addJob);

        etJobTitle = view.findViewById(R.id.et_job_addJob);
        etDescription = view.findViewById(R.id.et_description_addJob);
        etSalary = view.findViewById(R.id.et_salary_addJob);
        etAddress = view.findViewById(R.id.et_address_addJob);

        btnCategory.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("jobTitle", etJobTitle.getText().toString());
            bundle.putString("description", etDescription.getText().toString());
            bundle.putString("salary", etSalary.getText().toString());
            bundle.putString("salaryFrequent", frequentSalary);
            bundle.putString("selectedCategory", btnCategory.getText().toString());
            bundle.putString("selectedProvince", spinnerProvince.getSelectedItem().toString());
            bundle.putString("selectedRegency", spinnerRegency.isEnabled() ? spinnerRegency.getSelectedItem().toString() : "");
            bundle.putString("address", etAddress.getText().toString());

            PelangganAddJobCategoryFragment categoryFragment = new PelangganAddJobCategoryFragment();
            categoryFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, categoryFragment)
                    .addToBackStack(null)
                    .commit();
        });

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {
            String selectedCategory = result.getString("selectedCategory", "");
            if (!selectedCategory.isEmpty()) {
                Toast.makeText(getContext(), "Selected category: " + selectedCategory, Toast.LENGTH_SHORT).show();
                btnCategory.setText(selectedCategory);
            }
        });

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
        if (selectedButton == btnDaily){
            btnDaily.setSelected(selectedButton == btnDaily);
            frequentSalary = "Daily";
        } else if (selectedButton == btnWeekly){
            btnWeekly.setSelected(selectedButton == btnWeekly);
            frequentSalary = "Weekly";
        } else if (selectedButton == btnMonthly){
            btnMonthly.setSelected(selectedButton == btnMonthly);
            frequentSalary = "Monthly";
        }
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
