package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

public class ProfilePekerjaActivity extends AppCompatActivity {

    ImageView ivSettings, backButton;
    TextView tvPageTitle;
//    AppCompatButton btnEditPersonalInfo, btnAddJobsToPortofolio, btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_pekerja);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.btn_back_toolbar);
        ivSettings = findViewById(R.id.btn_profile_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);

        ivSettings.setImageResource(R.drawable.settings_icon);
        tvPageTitle.setText("Settings");


        ivSettings.setOnClickListener(v -> showSettings());

//        Handle back button on click event
        backButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(ProfilePekerjaActivity.this, PekerjaMainActivity.class));
                finish();
            }
        });


    }

    private void showSettings() {
        BottomSheetDialog bottomSheetDialogSettings = new BottomSheetDialog(ProfilePekerjaActivity.this);
//        View viewBottomSheet = LayoutInflater.from(ProfilePekerjaActivity.this).inflate(R.layout.bottom_sheet_dialog_settings, null);
//        bottomSheetDialogSettings.setContentView(viewBottomSheet);
//        bottomSheetDialogSettings.show();
        bottomSheetDialogSettings.setContentView(R.layout.bottom_sheet_dialog_settings);
        bottomSheetDialogSettings.show();
        bottomSheetDialogSettings.getWindow().setDimAmount(0.7f);

        AppCompatButton btnEditPersonalInfo = findViewById(R.id.btn_settings_editPersonalInformation);
        AppCompatButton btnAddJobsToPortofolio = findViewById(R.id.btn_settings_addJobsToPortofolio);
        AppCompatButton btnLogOut = findViewById(R.id.btn_settings_logOut);

//        btnEditPersonalInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        btnAddJobsToPortofolio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        btnLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }
}