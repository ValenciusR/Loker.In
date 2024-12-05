package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class PekerjaProfileActivity extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ImageView ivProfilePicture, btnBack, ivProfileNavbar;
    private TextView tvPageTitle, tvName, tvJob, tvLocation, tvJobDescription, tvPhone, tvEmail;
    private RecyclerView rvPortofolio, rvReview;
    private ArrayList<Portofolio> portofolios;
    private ArrayList<Review> reviews;
    private LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    private ListPortofolioAdapter portofolioAdapter;
    private ListReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btn_back_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        ivProfilePicture = findViewById(R.id.iv_profilePicture_profilePekerjaPage);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        tvName = findViewById(R.id.tv_name_profilePekerjaPage);
        tvJob = findViewById(R.id.tv_job_profilePekerjaPage);
        tvLocation = findViewById(R.id.tv_location_profilePekerjaPage);
        tvJobDescription = findViewById(R.id.tv_jobDescription_profilePekerjaPage);
        tvPhone = findViewById(R.id.tv_phone_profilePekerjaPage);
        tvEmail = findViewById(R.id.tv_email_profilePekerjaPage);
        rvPortofolio = findViewById(R.id.rv_portofolioList_profilePekerjaPage);
        rvReview = findViewById(R.id.rv_reviewList_profilePekerjaPage);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Profil");
        ivProfileNavbar.setImageResource(R.drawable.settings_icon);

//        Set Portofolio Recycler View
        Portofolio templatePortofolio = new Portofolio("Plumbing", new Date(), "Lorem ipsum dolor sit amet. Ut recusandae fugit quo eaque impedit eum ipsum illo sit animi galisum ut officia voluptate qui quia ducimus?");
        portofolios = new ArrayList<>();
        portofolios.add(templatePortofolio);
        portofolios.add(templatePortofolio);
        portofolios.add(templatePortofolio);
        portofolios.add(templatePortofolio);

        linearLayoutManager = new LinearLayoutManager(PekerjaProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
        portofolioAdapter = new ListPortofolioAdapter(portofolios);
        rvPortofolio.setLayoutManager(linearLayoutManager);
        rvPortofolio.setAdapter(portofolioAdapter);

//        Set Review Recycler View
        Review templateReview = new Review("John", "Pekerja berperilaku baik, hasil pekerjaan bagus dan berkualitas", 5f);
        reviews = new ArrayList<>();
        reviews.add(templateReview);
        reviews.add(templateReview);
        reviews.add(templateReview);
        reviews.add(templateReview);

        linearLayoutManager2 = new LinearLayoutManager(PekerjaProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        reviewAdapter = new ListReviewAdapter(reviews);
        rvReview.setLayoutManager(linearLayoutManager2);
        rvReview.setAdapter(reviewAdapter);

        ivProfileNavbar.setOnClickListener(v -> showSettings());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
    }

    private void showSettings() {
        BottomSheetDialog bottomSheetDialogSettings = new BottomSheetDialog(PekerjaProfileActivity.this);

        View viewBottomSheet = LayoutInflater.from(PekerjaProfileActivity.this).inflate(R.layout.bottom_sheet_dialog_settings, null);
        bottomSheetDialogSettings.setContentView(viewBottomSheet);
        bottomSheetDialogSettings.show();
        bottomSheetDialogSettings.getWindow().setDimAmount(0.7f);

        AppCompatButton btnEditPersonalInfo = viewBottomSheet.findViewById(R.id.btn_settings_editPersonalInformation);
        AppCompatButton btnAddJobsToPortofolio = viewBottomSheet.findViewById(R.id.btn_settings_addJobsToPortofolio);
        AppCompatButton btnLogOut = viewBottomSheet.findViewById(R.id.btn_settings_logOut);

        btnEditPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaProfileActivity.this, EditProfilePekerjaActivity.class));
                finish();
            }
        });

        btnAddJobsToPortofolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaProfileActivity.this, PekerjaEditPortofolioActivity.class));
                finish();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(PekerjaProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void backPage() {
        startActivity(new Intent(PekerjaProfileActivity.this, PekerjaMainActivity.class));
        finish();
    }
}