package com.example.lokerin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.lokerin.Job;
import com.example.lokerin.PekerjaEditProfileActivity;
import com.example.lokerin.PekerjaMainActivity;
import com.example.lokerin.PekerjaProfileActivity;
import com.example.lokerin.R;
import com.example.lokerin.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PelangganEditJobActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobReference;

    private ImageView btnBack, ivProfileNavbar;
    private Button btnCategory, btnDaily, btnWeekly, btnMonthly, btnSubmit;
    private Spinner spinnerProvince, spinnerRegency;
    private EditText etJobTitle, etDescription, etSalary, etAddress;
    private String frequentSalary = "", selectedCategory, currentUserId, jobId;
    private boolean isCategorySelected = false;
    private TextView tvPageTitle;
    private ArrayAdapter<CharSequence> regencyAdapter;

    private Integer provinceIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pelanggan_edit_job);

        Intent intent = getIntent();
        jobId = intent.getStringExtra("JOB_ID");

        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app");
        jobReference = firebaseDatabase.getReference().child("jobs").child(jobId);

        etJobTitle = findViewById(R.id.et_job_addJob);
        etDescription = findViewById(R.id.et_description_addJob);
        etSalary = findViewById(R.id.et_salary_addJob);
        btnCategory = findViewById(R.id.btn_category_addJob);
        btnDaily = findViewById(R.id.btn_dailySalary_addJob);
        btnWeekly = findViewById(R.id.btn_weeklySalary_addJob);
        btnMonthly = findViewById(R.id.btn_monthlySalary_addJob);
        spinnerProvince = findViewById(R.id.spinner_province_addJob);
        spinnerRegency = findViewById(R.id.spinner_regency_addJob);
        etAddress = findViewById(R.id.et_address_addJob);

        if (intent.hasExtra("jobTitle") && intent.hasExtra("jobCategory")) {
            loadJobDetailsFromIntent(intent);
        } else {
            fetchJobDetails();
        }

        btnSubmit = findViewById(R.id.btn_editJob);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Edit Pekerjaan");
        ivProfileNavbar.setVisibility(View.GONE);

        btnCategory.setOnClickListener(v -> {
            Intent intentCategory = new Intent(PelangganEditJobActivity.this, PelangganEditJobCategoryActivity.class);
            intentCategory.putExtra("jobId", jobId);
            intentCategory.putExtra("jobTitle", etJobTitle.getText().toString());
            intentCategory.putExtra("jobCategory", btnCategory.getText().toString());
            intentCategory.putExtra("jobDescription", etDescription.getText().toString());
            intentCategory.putExtra("jobSalary", etSalary.getText().toString());
            intentCategory.putExtra("jobSalaryFrequent", frequentSalary);
            intentCategory.putExtra("jobProvince", spinnerProvince.getSelectedItem().toString());
            intentCategory.putExtra("jobRegency", spinnerRegency.isEnabled() ? spinnerRegency.getSelectedItem().toString() : "");
            intentCategory.putExtra("jobAddress", etAddress.getText().toString());
            startActivity(intentCategory);
            finish();
        });

        btnDaily.setOnClickListener(v -> setSelectedButton(btnDaily));
        btnWeekly.setOnClickListener(v -> setSelectedButton(btnWeekly));
        btnMonthly.setOnClickListener(v -> setSelectedButton(btnMonthly));

        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(this,
                R.array.province, R.layout.spinner_item);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerProvince.setAdapter(provinceAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position >= 1 && position < 39) {
                    spinnerRegency.setEnabled(true);
                    updateRegencySpinner(position, "");
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

        btnSubmit.setOnClickListener(v -> updateJobDetails());
    }

    private void fetchJobDetails() {
        if (jobId != null) {
            jobReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Job job = dataSnapshot.getValue(Job.class);
                    if (job != null) {
                        etJobTitle.setText(job.getJobTitle());
                        btnCategory.setText(job.getJobCategory() != null ? job.getJobCategory() : "Pilih Kategori");
                        selectedCategory = job.getJobCategory();
                        if (!selectedCategory.isEmpty()){
                            isCategorySelected = true;
                        }
                        etDescription.setText(job.getJobDescription());
                        etSalary.setText(String.valueOf(job.getJobSalary()));

                        String salaryFrequency = job.getJobSalaryFrequent();
                        resetSalaryFrequencyButtons();
                        if ("Daily".equals(salaryFrequency)) {
                            btnDaily.setSelected(true);
                            frequentSalary = "Daily";
                        } else if ("Weekly".equals(salaryFrequency)) {
                            btnWeekly.setSelected(true);
                            frequentSalary = "Weekly";
                        } else if ("Monthly".equals(salaryFrequency)) {
                            btnMonthly.setSelected(true);
                            frequentSalary = "Monthly";
                        }

                        setSpinnerSelection(spinnerProvince, job.getJobProvince());
                        int provinceIndex = spinnerProvince.getSelectedItemPosition();

                        if (provinceIndex > 0) {
                            spinnerRegency.setEnabled(true);
                            updateRegencySpinner(provinceIndex, job.getJobRegency());
                        } else {
                            spinnerRegency.setEnabled(false);
                        }

                        etAddress.setText(job.getJobAddress());
                    } else {
                        Toast.makeText(PelangganEditJobActivity.this, "Data pekerjaan tidak ditemukan!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PelangganEditJobActivity.this, "Gagal mengambil data pekerjaan.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Pekerjaan tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadJobDetailsFromIntent(Intent intent) {
        etJobTitle.setText(intent.getStringExtra("jobTitle"));
        selectedCategory = intent.getStringExtra("jobCategory");
        btnCategory.setText(selectedCategory != null && !selectedCategory.isEmpty() ? selectedCategory : "Pilih Kategori");
        isCategorySelected = selectedCategory != null && !selectedCategory.isEmpty();
        etDescription.setText(intent.getStringExtra("jobDescription"));
        etSalary.setText(intent.getStringExtra("jobSalary"));

        frequentSalary = intent.getStringExtra("jobSalaryFrequent");
        resetSalaryFrequencyButtons();
        if ("Daily".equals(frequentSalary)) {
            btnDaily.setSelected(true);
        } else if ("Weekly".equals(frequentSalary)) {
            btnWeekly.setSelected(true);
        } else if ("Monthly".equals(frequentSalary)) {
            btnMonthly.setSelected(true);
        }

        String provinceTemp = intent.getStringExtra("jobProvince");
        if (provinceTemp != null && !provinceTemp.isEmpty()) {
            spinnerProvince.post(() -> {
                setSpinnerSelection(spinnerProvince, provinceTemp);
                provinceIndex = spinnerProvince.getSelectedItemPosition();

                if (provinceIndex > 0) {
                    spinnerRegency.setEnabled(true);
                    updateRegencySpinner(provinceIndex, intent.getStringExtra("jobRegency"));
                } else {
                    spinnerRegency.setEnabled(false);
                }
            });
        }

        etAddress.setText(intent.getStringExtra("jobAddress"));
    }

    private void updateJobDetails() {
        boolean isValid = true;

        if (etJobTitle.getText().toString().trim().length() < 8) {
            etJobTitle.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(this, "Judul pekerjaan minimal berisi 8 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etJobTitle.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid && !isCategorySelected || selectedCategory.isEmpty()) {
            btnCategory.setBackgroundResource(R.drawable.shape_red_rounded);
            Toast.makeText(this, "Pilih salah satu kategori!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            btnCategory.setBackgroundResource(R.drawable.shape_darkblue_rounded);
        }

        if (isValid && etDescription.getText().toString().trim().length() < 20) {
            etDescription.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(this, "Deskripsi minimal berisi 20 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etDescription.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid) {
            try {
                double salaryValue = Double.parseDouble(etSalary.getText().toString().trim());
                if (salaryValue <= 0) {
                    etSalary.setBackgroundResource(R.drawable.shape_rounded_red_border);
                    Toast.makeText(this, "Upah harus bilangan positif!", Toast.LENGTH_SHORT).show();
                    isValid = false;
                } else {
                    etSalary.setBackgroundResource(R.drawable.shape_rounded_blue_border);
                }
            } catch (NumberFormatException e) {
                etSalary.setBackgroundResource(R.drawable.shape_rounded_red_border);
                Toast.makeText(this, "Upah harus berupa angka valid!", Toast.LENGTH_SHORT).show();
                isValid = false;
            }
        }

        if (isValid && frequentSalary.isEmpty()) {
            btnDaily.setBackgroundResource(R.drawable.shape_button_salary_error);
            btnWeekly.setBackgroundResource(R.drawable.shape_button_salary_error);
            btnMonthly.setBackgroundResource(R.drawable.shape_button_salary_error);
            Toast.makeText(this, "Pilih salah satu frekuensi upah!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid && spinnerProvince.getSelectedItemPosition() == 0) {
            spinnerProvince.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(this, "Pilih salah satu provinsi!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            spinnerProvince.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid && spinnerRegency.isEnabled() && spinnerRegency.getSelectedItemPosition() == 0) {
            spinnerRegency.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(this, "Pilih salah satu kota / kabupaten!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            spinnerRegency.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid && etAddress.getText().toString().trim().length() < 20) {
            etAddress.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(this, "Alamat minimal berisi 20 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etAddress.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (isValid) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Mengubah...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Map<String, Object> updates = new HashMap<>();
            updates.put("jobTitle", etJobTitle.getText().toString());
            updates.put("jobCategory", selectedCategory);
            updates.put("jobDescription", etDescription.getText().toString());
            updates.put("jobSalary", Double.parseDouble(etSalary.getText().toString()));
            updates.put("jobSalaryFrequent", frequentSalary);
            updates.put("jobProvince", spinnerProvince.getSelectedItem().toString());
            updates.put("jobRegency", spinnerRegency.getSelectedItem().toString());
            updates.put("jobAddress", etAddress.getText().toString());

            jobReference.updateChildren(updates).addOnCompleteListener(task -> {
                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    Toast.makeText(this, "Pekerjaan berhasil diubah!", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);

                    Intent intentUpdatePage = new Intent(PelangganEditJobActivity.this, PelangganDetailJobActivity.class);
                    intentUpdatePage.putExtra("jobId", jobId);
                    startActivity(intentUpdatePage);
                } else {
                    Toast.makeText(this, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show();
                }

                finish();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, "Terjadi kesalahan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void resetSalaryFrequencyButtons() {
        btnDaily.setBackgroundResource(R.drawable.selector_item_salary_bg);
        btnWeekly.setBackgroundResource(R.drawable.selector_item_salary_bg);
        btnMonthly.setBackgroundResource(R.drawable.selector_item_salary_bg);
    }

    private void setSelectedButton(Button selectedButton) {
        resetSalaryFrequencyButtons();

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

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null && !value.isEmpty()) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(value);
                if (position >= 0) {
                    spinner.setSelection(position);
                }
            }
        }
    }


    private void updateRegencySpinner(int provincePosition, String regency) {
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

        regencyAdapter = ArrayAdapter.createFromResource(this,
                regencyArrayId, R.layout.spinner_item);
        regencyAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerRegency.setAdapter(regencyAdapter);

        spinnerRegency.post(() -> setSpinnerSelection(spinnerRegency, regency));
    }

    private void backPage() {
        Intent resultIntent = new Intent();
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }
}
