package com.example.lokerin;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PelangganAddJobFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsReference;

    private Button btnCategory, btnDaily, btnWeekly, btnMonthly, btnSubmit;
    private Spinner spinnerProvince, spinnerRegency;
    private EditText etJobTitle, etDescription, etSalary, etAddress;
    private String frequentSalary = "", selectedCategory, currentUserId;
    private boolean isCategorySelected = false; ;
    private ArrayAdapter<CharSequence> regencyAdapter;
    private ArrayList<String> applicantsList;

    private static final int IMAGE_PICK_CODE = 1000;
    private RelativeLayout uploadContainer;
    private ImageView uploadedImageView;
    private ImageView uploadIcon;
    private TextView uploadText;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pelanggan_addjob, container, false);

        FirebaseApp.initializeApp(requireContext());
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app");
        jobsReference = firebaseDatabase.getReference().child("jobs");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            currentUserId = null;
        }

        etJobTitle = view.findViewById(R.id.et_job_addJob);
        etDescription = view.findViewById(R.id.et_description_addJob);
        etSalary = view.findViewById(R.id.et_salary_addJob);
        btnCategory = view.findViewById(R.id.btn_category_addJob);
        btnDaily = view.findViewById(R.id.btn_dailySalary_addJob);
        btnWeekly = view.findViewById(R.id.btn_weeklySalary_addJob);
        btnMonthly = view.findViewById(R.id.btn_monthlySalary_addJob);

        uploadContainer = view.findViewById(R.id.upload_image_container);
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
            bundle.putString("jobDescription", etDescription.getText().toString());
            bundle.putString("jobSalary", etSalary.getText().toString());
            bundle.putString("jobSalaryFrequent", frequentSalary);
            bundle.putString("jobCategory", btnCategory.getText().toString());
            bundle.putString("jobProvince", spinnerProvince.getSelectedItem().toString());
            bundle.putString("jobRegency", spinnerRegency.isEnabled() ? spinnerRegency.getSelectedItem().toString() : "");
            bundle.putString("jobAddress", etAddress.getText().toString());
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
            selectedCategory = result.getString("selectedCategory", "");
            if (!selectedCategory.isEmpty()) {
                Toast.makeText(getContext(), "Kategori dipilih: " + selectedCategory, Toast.LENGTH_SHORT).show();
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

        btnSubmit.setOnClickListener(v -> validateForm());

        return view;
    }

    private void validateForm() {
        boolean isValid = true;

        if (etJobTitle.getText().toString().trim().length() < 8) {
            etJobTitle.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(getContext(), "Judul pekerjaan minimal berisi 8 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etJobTitle.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid && !isCategorySelected) {
            btnCategory.setBackgroundResource(R.drawable.shape_red_rounded);
            Toast.makeText(getContext(), "Pilih salah satu kategori!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            btnCategory.setBackgroundResource(R.drawable.shape_darkblue_rounded);
        }

        if (isValid && etDescription.getText().toString().trim().length() < 20) {
            etDescription.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(getContext(), "Deskripsi minimal berisi 20 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etDescription.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid) {
            try {
                double salaryValue = Double.parseDouble(etSalary.getText().toString().trim());
                if (salaryValue <= 0) {
                    etSalary.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    Toast.makeText(getContext(), "Gaji harus bilangan positif!", Toast.LENGTH_SHORT).show();
                    isValid = false;
                } else {
                    etSalary.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                }
            } catch (NumberFormatException e) {
                etSalary.setBackgroundResource(R.drawable.shape_rounded_red_border);
                Toast.makeText(getContext(), "Gaji harus berupa angka valid!", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        if (isValid && frequentSalary.isEmpty()) {
            btnDaily.setBackgroundResource(R.drawable.shape_button_salary_error);
            btnWeekly.setBackgroundResource(R.drawable.shape_button_salary_error);
            btnMonthly.setBackgroundResource(R.drawable.shape_button_salary_error);
            Toast.makeText(getContext(), "Pilih salah satu frekuensi gaji!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid && spinnerProvince.getSelectedItemPosition() == 0) {
            spinnerProvince.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(getContext(), "Pilih salah satu provinsi!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            spinnerProvince.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid && spinnerRegency.isEnabled() && spinnerRegency.getSelectedItemPosition() == 0) {
            spinnerRegency.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(getContext(), "Pilih salah satu kabupaten!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            spinnerRegency.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid && etAddress.getText().toString().trim().length() < 20) {
            etAddress.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(getContext(), "Alamat minimal berisi 20 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etAddress.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid) {
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setTitle("Mengunggah...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            HashMap<String, Object> jobData = new HashMap<>();
            jobData.put("jobMakerId", currentUserId);
            jobData.put("jobTitle", etJobTitle.getText().toString().trim());
            jobData.put("jobCategory", selectedCategory);
            jobData.put("jobDescription", etDescription.getText().toString().trim());
            jobData.put("jobSalary", Double.parseDouble(etSalary.getText().toString().trim()));
            jobData.put("jobSalaryFrequent", frequentSalary);
            jobData.put("jobProvince", spinnerProvince.getSelectedItem().toString());
            jobData.put("jobRegency", spinnerRegency.isEnabled() ? spinnerRegency.getSelectedItem().toString() : "");
            jobData.put("jobAddress", etAddress.getText().toString().trim());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());
            jobData.put("jobDateUpload", currentDate);
            jobData.put("jobStatus", "OPEN");

            ArrayList<String> applicantsList = new ArrayList<>();
            jobData.put("jobApplicants", applicantsList);

            DatabaseReference jobRef = jobsReference.push();
            String generatedId = jobRef.getKey();
            jobData.put("jobId", generatedId);

            jobRef.setValue(jobData).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Pekerjaan berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    resetForm();
                } else {
                    Toast.makeText(requireContext(), "Gagal menambahkan pekerjaan. Coba lagi!", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
            case 1:
                regencyArrayId = R.array.regency_aceh;
                break;
            case 2:
                regencyArrayId = R.array.regency_bali;
                break;
            case 3:
                regencyArrayId = R.array.regency_banten;
                break;
            case 4:
                regencyArrayId = R.array.regency_bengkulu;
                break;
            case 5:
                regencyArrayId = R.array.regency_gorontalo;
                break;
            case 6:
                regencyArrayId = R.array.regency_jambi;
                break;
            case 7:
                regencyArrayId = R.array.regency_jawa_barat;
                break;
            case 8:
                regencyArrayId = R.array.regency_jawa_tengah;
                break;
            case 9:
                regencyArrayId = R.array.regency_jawa_timur;
                break;
            case 10:
                regencyArrayId = R.array.regency_jakarta;
                break;
            case 11:
                regencyArrayId = R.array.regency_kalimantan_barat;
                break;
            case 12:
                regencyArrayId = R.array.regency_kalimantan_selatan;
                break;
            case 13:
                regencyArrayId = R.array.regency_kalimantan_tengah;
                break;
            case 14:
                regencyArrayId = R.array.regency_kalimantan_timur;
                break;
            case 15:
                regencyArrayId = R.array.regency_kalimantan_utara;
                break;
            case 16:
                regencyArrayId = R.array.regency_kepulauan_bangka_belitung;
                break;
            case 17:
                regencyArrayId = R.array.regency_kepulauan_riau;
                break;
            case 18:
                regencyArrayId = R.array.regency_lampung;
                break;
            case 19:
                regencyArrayId = R.array.regency_maluku;
                break;
            case 20:
                regencyArrayId = R.array.regency_maluku_utara;
                break;
            case 21:
                regencyArrayId = R.array.regency_nusa_tenggara_barat;
                break;
            case 22:
                regencyArrayId = R.array.regency_nusa_tenggara_timur;
                break;
            case 23:
                regencyArrayId = R.array.regency_papua;
                break;
            case 24:
                regencyArrayId = R.array.regency_papua_barat;
                break;
            case 25:
                regencyArrayId = R.array.regency_papua_barat_daya;
                break;
            case 26:
                regencyArrayId = R.array.regency_papua_pegunungan;
                break;
            case 27:
                regencyArrayId = R.array.regency_papua_selatan;
                break;
            case 28:
                regencyArrayId = R.array.regency_papua_tengah;
                break;
            case 29:
                regencyArrayId = R.array.regency_riau;
                break;
            case 30:
                regencyArrayId = R.array.regency_sulawesi_barat;
                break;
            case 31:
                regencyArrayId = R.array.regency_sulawesi_selatan;
                break;
            case 32:
                regencyArrayId = R.array.regency_sulawesi_tengah;
                break;
            case 33:
                regencyArrayId = R.array.regency_sulawesi_tenggara;
                break;
            case 34:
                regencyArrayId = R.array.regency_sulawesi_utara;
                break;
            case 35:
                regencyArrayId = R.array.regency_sumatra_barat;
                break;
            case 36:
                regencyArrayId = R.array.regency_sumatra_selatan;
                break;
            case 37:
                regencyArrayId = R.array.regency_sumatra_utara;
                break;
            case 38:
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

    private void resetForm() {
        etJobTitle.setText("");
        etDescription.setText("");
        selectedCategory = "";
        isCategorySelected = false;
        btnCategory.setText("Pilih Kategori");
        etSalary.setText("");
        etAddress.setText("");
        spinnerProvince.setSelection(0);
        spinnerRegency.setEnabled(false);
        btnDaily.setSelected(false);
        btnWeekly.setSelected(false);
        btnMonthly.setSelected(false);
        frequentSalary = "";

    }
}
