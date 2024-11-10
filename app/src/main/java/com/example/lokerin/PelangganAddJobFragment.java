package com.example.lokerin;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.IOException;
import java.io.InputStream;

public class PelangganAddJobFragment extends Fragment {

    private Button btnCategory, btnDaily, btnWeekly, btnMonthly, btnSubmit;
    private Spinner spinnerProvince, spinnerRegency;
    private EditText etJobTitle, etDescription, etSalary, etAddress;
    private String frequentSalary = "";
    private boolean isCategorySelected = false; ;
    private ArrayAdapter<CharSequence> regencyAdapter;

    private static final int IMAGE_PICK_CODE = 1000;
    private ImageView uploadedImageView;
    private ImageView uploadIcon;
    private TextView uploadText;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pelanggan_addjob, container, false);

        etJobTitle = view.findViewById(R.id.et_job_addJob);
        etDescription = view.findViewById(R.id.et_description_addJob);
        etSalary = view.findViewById(R.id.et_salary_addJob);
        btnCategory = view.findViewById(R.id.btn_category_addJob);
        btnDaily = view.findViewById(R.id.btn_dailySalary_addJob);
        btnWeekly = view.findViewById(R.id.btn_weeklySalary_addJob);
        btnMonthly = view.findViewById(R.id.btn_monthlySalary_addJob);

        RelativeLayout uploadContainer = view.findViewById(R.id.upload_image_container);
        uploadedImageView = view.findViewById(R.id.iv_uploaded_image);
        uploadIcon = view.findViewById(R.id.iv_upload_image_icon);
        uploadText = view.findViewById(R.id.tv_upload_image_text);

        spinnerProvince = view.findViewById(R.id.spinner_province_addJob);
        spinnerRegency = view.findViewById(R.id.spinner_regency_addJob);
        etAddress = view.findViewById(R.id.et_address_addJob);

        btnSubmit = view.findViewById(R.id.btn_addJob);

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
            isCategorySelected = true;
            btnCategory.setBackgroundResource(R.drawable.shape_darkblue_rounded);
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

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleImageSelection
        );

        uploadContainer.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 1 && position < 39) {
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

        btnSubmit.setOnClickListener(v -> {
            boolean isValid = true;

            if (etJobTitle.getText().toString().trim().length() < 8) {
                etJobTitle.setBackgroundResource(R.drawable.shape_rounded_red_border);
                Toast.makeText(getContext(), "Job title must be at least 8 characters", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                etJobTitle.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            if (isValid && !isCategorySelected) {
                btnCategory.setBackgroundResource(R.drawable.shape_red_rounded);
                Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                btnCategory.setBackgroundResource(R.drawable.shape_darkblue_rounded);
            }

            if (isValid && etDescription.getText().toString().trim().length() < 20) {
                etDescription.setBackgroundResource(R.drawable.shape_rounded_red_border);
                Toast.makeText(getContext(), "Description must be at least 20 characters", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                etDescription.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            if (isValid) {
                try {
                    double salaryValue = Double.parseDouble(etSalary.getText().toString().trim());
                    if (salaryValue <= 0) {
                        etSalary.setBackgroundResource(R.drawable.shape_rounded_red_border);
                        Toast.makeText(getContext(), "Salary must be a positive number", Toast.LENGTH_SHORT).show();
                        isValid = false;
                    } else {
                        etSalary.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                    }
                } catch (NumberFormatException e) {
                    etSalary.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    Toast.makeText(getContext(), "Salary must be a valid number", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
            }

            if (isValid && frequentSalary.isEmpty()) {
                btnDaily.setBackgroundResource(R.drawable.shape_button_salary_error);
                btnWeekly.setBackgroundResource(R.drawable.shape_button_salary_error);
                btnMonthly.setBackgroundResource(R.drawable.shape_button_salary_error);
                Toast.makeText(getContext(), "Please select a salary frequency", Toast.LENGTH_SHORT).show();
                isValid = false;
            }

            if (isValid && spinnerProvince.getSelectedItemPosition() == 0) {
                spinnerProvince.setBackgroundResource(R.drawable.shape_rounded_red_border);
                Toast.makeText(getContext(), "Please select a province", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                spinnerProvince.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            if (isValid && spinnerRegency.isEnabled() && spinnerRegency.getSelectedItemPosition() == 0) {
                spinnerRegency.setBackgroundResource(R.drawable.shape_rounded_red_border);
                Toast.makeText(getContext(), "Please select a regency", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                spinnerRegency.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            if (isValid && etAddress.getText().toString().trim().length() < 20) {
                etAddress.setBackgroundResource(R.drawable.shape_rounded_red_border);
                Toast.makeText(getContext(), "Address must be at least 20 characters", Toast.LENGTH_SHORT).show();
                isValid = false;
            } else {
                etAddress.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            }

            if (isValid) {
                Toast.makeText(getContext(), "Job uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setSelectedButton(Button selectedButton) {
        btnDaily.setBackgroundResource(R.drawable.selector_item_salary_bg);
        btnWeekly.setBackgroundResource(R.drawable.selector_item_salary_bg);
        btnMonthly.setBackgroundResource(R.drawable.selector_item_salary_bg);

        btnDaily.setSelected(false);
        btnWeekly.setSelected(false);
        btnMonthly.setSelected(false);
        selectedButton.setSelected(true);

        if (selectedButton == btnDaily) {
            frequentSalary = "Daily";
        } else if (selectedButton == btnWeekly) {
            frequentSalary = "Weekly";
        } else if (selectedButton == btnMonthly) {
            frequentSalary = "Monthly";
        }
    }

    private void handleImageSelection(Uri uri) {
        if (uri != null) {
            try {
                uploadedImageView.setVisibility(View.VISIBLE);
                uploadIcon.setVisibility(View.GONE);
                uploadText.setVisibility(View.GONE);
                uploadedImageView.setImageURI(uri);

                InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                uploadedImageView.setImageBitmap(bitmap);
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRegencySpinner(int provincePosition) {
        int regencyArrayId;

        switch (provincePosition) {
            case 0:
                regencyArrayId = R.array.regency_aceh;
                break;
            case 1:
                regencyArrayId = R.array.regency_bali;
                break;
            case 2:
                regencyArrayId = R.array.regency_banten;
                break;
            case 3:
                regencyArrayId = R.array.regency_bengkulu;
                break;
            case 4:
                regencyArrayId = R.array.regency_gorontalo;
                break;
            case 5:
                regencyArrayId = R.array.regency_jambi;
                break;
            case 6:
                regencyArrayId = R.array.regency_jawa_barat;
                break;
            case 7:
                regencyArrayId = R.array.regency_jawa_tengah;
                break;
            case 8:
                regencyArrayId = R.array.regency_jawa_timur;
                break;
            case 9:
                regencyArrayId = R.array.regency_jakarta;
                break;
            case 10:
                regencyArrayId = R.array.regency_kalimantan_barat;
                break;
            case 11:
                regencyArrayId = R.array.regency_kalimantan_selatan;
                break;
            case 12:
                regencyArrayId = R.array.regency_kalimantan_tengah;
                break;
            case 13:
                regencyArrayId = R.array.regency_kalimantan_timur;
                break;
            case 14:
                regencyArrayId = R.array.regency_kalimantan_utara;
                break;
            case 15:
                regencyArrayId = R.array.regency_kepulauan_bangka_belitung;
                break;
            case 16:
                regencyArrayId = R.array.regency_kepulauan_riau;
                break;
            case 17:
                regencyArrayId = R.array.regency_lampung;
                break;
            case 18:
                regencyArrayId = R.array.regency_maluku;
                break;
            case 19:
                regencyArrayId = R.array.regency_maluku_utara;
                break;
            case 20:
                regencyArrayId = R.array.regency_nusa_tenggara_barat;
                break;
            case 21:
                regencyArrayId = R.array.regency_nusa_tenggara_timur;
                break;
            case 22:
                regencyArrayId = R.array.regency_papua;
                break;
            case 23:
                regencyArrayId = R.array.regency_papua_barat;
                break;
            case 24:
                regencyArrayId = R.array.regency_papua_barat_daya;
                break;
            case 25:
                regencyArrayId = R.array.regency_papua_pegunungan;
                break;
            case 26:
                regencyArrayId = R.array.regency_papua_selatan;
                break;
            case 27:
                regencyArrayId = R.array.regency_papua_tengah;
                break;
            case 28:
                regencyArrayId = R.array.regency_riau;
                break;
            case 29:
                regencyArrayId = R.array.regency_sulawesi_barat;
                break;
            case 30:
                regencyArrayId = R.array.regency_sulawesi_selatan;
                break;
            case 31:
                regencyArrayId = R.array.regency_sulawesi_tengah;
                break;
            case 32:
                regencyArrayId = R.array.regency_sulawesi_tenggara;
                break;
            case 33:
                regencyArrayId = R.array.regency_sulawesi_utara;
                break;
            case 34:
                regencyArrayId = R.array.regency_sumatra_barat;
                break;
            case 35:
                regencyArrayId = R.array.regency_sumatra_selatan;
                break;
            case 36:
                regencyArrayId = R.array.regency_sumatra_utara;
                break;
            case 37:
                regencyArrayId = R.array.regency_yogyakarta;
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
