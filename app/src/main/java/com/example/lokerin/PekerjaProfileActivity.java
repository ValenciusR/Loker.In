package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PekerjaProfileActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference, jobReference;
    FirebaseUser fuser;
    private User user;

    private FlexboxLayout flKeterampilan;
    private ImageView ivProfilePicture, btnBack, ivProfileNavbar;
    private TextView tvPageTitle, tvName, tvJob, tvLocation, tvJobDescription, tvPhone, tvEmail;
    private String fromUserType, userIdFromPelanggan, jobIdFromPelanggan, onPage;
    private Button btnChat, btnAccept;
    private RecyclerView rvPortofolio, rvReview;
    private ArrayList<Portofolio> portofolios;
    private ArrayList<Review> reviews;
    private ArrayList<String> applicantsList, workersList;

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
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        ivProfilePicture = findViewById(R.id.iv_profilePicture_profilePekerjaPage);
        tvName = findViewById(R.id.tv_name_profilePekerjaPage);
        tvJob = findViewById(R.id.tv_job_profilePekerjaPage);
        tvLocation = findViewById(R.id.tv_location_profilePekerjaPage);
        tvJobDescription = findViewById(R.id.tv_jobDescription_profilePekerjaPage);
        tvPhone = findViewById(R.id.tv_phone_profilePekerjaPage);
        tvEmail = findViewById(R.id.tv_email_profilePekerjaPage);
        rvPortofolio = findViewById(R.id.rv_portofolioList_profilePekerjaPage);
        rvReview = findViewById(R.id.rv_reviewList_profilePekerjaPage);
        flKeterampilan = findViewById(R.id.fl_keterampilan_profilePekerjaPage);
        btnChat = findViewById(R.id.btn_chat_profilePekerjaPage);
        btnAccept = findViewById(R.id.btn_accept_profilePekerjaPage);

        Intent intent = getIntent();
        fromUserType = intent.getStringExtra("fromUserType");
        userIdFromPelanggan = intent.getStringExtra("USER_ID");
        jobIdFromPelanggan = intent.getStringExtra("JOB_ID");
        onPage = "PEKERJA";

        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        if (fromUserType == null) {
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            userReference = firebaseDatabase.getReference().child("users").child(fuser.getUid());
            btnChat.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
        } else {
            userReference = firebaseDatabase.getReference().child("users").child(userIdFromPelanggan);
            ivProfileNavbar.setVisibility(View.GONE);
            jobReference = firebaseDatabase.getReference().child("jobs").child(jobIdFromPelanggan);
            btnChat.setOnClickListener(v -> {
                Toast.makeText(this, "INI PINDAH KE CHAT (INI DARI PELANGGAN (get auth) KE DETAIL PEKERJA YANG SEDANG DI CEK)", Toast.LENGTH_SHORT).show();
            });
            btnAccept.setOnClickListener(v -> {showApproveApplicantConfirmationDialog();});
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Profil");
        ivProfileNavbar.setImageResource(R.drawable.settings_icon);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                tvName.setText(user.getName());
                tvJob.setText(user.getJob());
                tvLocation.setText(user.getLocation());
                tvJobDescription.setText(user.getJobDesc());
                tvPhone.setText(user.getPhoneNumber());
                tvEmail.setText(user.getEmail());

                setSkills(user);

                if(user.getImageUrl().equals("default")){
                    ivProfilePicture.setImageResource(R.drawable.default_no_profile_icon);
                } else{
                    if(!PekerjaProfileActivity.this.isDestroyed()){
                        Glide.with(PekerjaProfileActivity.this).load(user.getImageUrl()).into(ivProfilePicture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    private void setSkills(User user){
        List<String> skills = user.getSkill();

        if (skills == null || skills.isEmpty()) {
            return;
        }

        for (String skill : skills) {
            TextView tvSkill = new TextView(this);
            tvSkill.setText(skill);

            tvSkill.setTextColor(getResources().getColor(R.color.blue));
            tvSkill.setBackgroundResource(R.drawable.shape_rounded_blue_border); // Custom drawable
            tvSkill.setPadding(50, 25, 50, 25);
            tvSkill.setTextSize(16);
            tvSkill.setLayoutParams(new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 25, 25);
            tvSkill.setLayoutParams(layoutParams);

            flKeterampilan.addView(tvSkill);
        }
    }

    private void showSettings() {
        BottomSheetDialog bottomSheetDialogSettings = new BottomSheetDialog(PekerjaProfileActivity.this);

        View viewBottomSheet = LayoutInflater.from(PekerjaProfileActivity.this).inflate(R.layout.bottom_sheet_dialog_settings, null);
        bottomSheetDialogSettings.setContentView(viewBottomSheet);
        bottomSheetDialogSettings.show();
        bottomSheetDialogSettings.getWindow().setDimAmount(0.7f);

        AppCompatButton btnEditPersonalInfo = viewBottomSheet.findViewById(R.id.btn_settings_editPersonalInformation);
        AppCompatButton btnEditKeterampilan = viewBottomSheet.findViewById(R.id.btn_settings_editKeterampilan);
        AppCompatButton btnTambahPekerjaanSebelumnya = viewBottomSheet.findViewById(R.id.btn_settings_tambahPekerjaanSebelumnya);
        AppCompatButton btnAddJobsToPortofolio = viewBottomSheet.findViewById(R.id.btn_settings_addJobsToPortofolio);
        AppCompatButton btnLogOut = viewBottomSheet.findViewById(R.id.btn_settings_logOut);

        btnEditPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaProfileActivity.this, EditProfilePekerjaActivity.class));
            }
        });

        btnEditKeterampilan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaProfileActivity.this, PekerjaAddKeterampilanActivity.class));
            }
        });

        btnTambahPekerjaanSebelumnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaProfileActivity.this, PekerjaAddWorkExperienceActivity.class));
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

    private void showApproveApplicantConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Terima Pekerja?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        dialog.show();

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            jobReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();

                    applicantsList = new ArrayList<>();
                    if (snapshot.child("jobApplicants").exists()) {
                        applicantsList = (ArrayList<String>) snapshot.child("jobApplicants").getValue();
                    }
                    if (applicantsList == null) {
                        applicantsList = new ArrayList<>();
                    }

                    workersList = new ArrayList<>();
                    if (snapshot.child("jobWorkers").exists()) {
                        workersList = (ArrayList<String>) snapshot.child("jobWorkers").getValue();
                    }
                    if (workersList == null) {
                        workersList = new ArrayList<>();
                    }

                    String userIdToBeAccept = userIdFromPelanggan;
                    if (userIdToBeAccept != null) {
                        if (applicantsList.contains(userIdToBeAccept) && !workersList.contains(userIdToBeAccept)) {
                            applicantsList.remove(userIdToBeAccept);
                            jobReference.child("jobApplicants").setValue(applicantsList).addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    workersList.add(userIdToBeAccept);
                                    jobReference.child("jobWorkers").setValue(workersList).addOnCompleteListener(updateTask2 -> {
                                        if (updateTask2.isSuccessful()) {
                                            Toast.makeText(this, "Berhasil menerima pekerja ini", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Gagal mendaftarkan pekerja ini", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(this, "Gagal mendaftarkan pekerja ini", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            if (!applicantsList.contains(userIdToBeAccept) && !workersList.contains(userIdToBeAccept)) {
                                Toast.makeText(this, "Pekerja belum mendaftar pada pekerjaan ini", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Pekerja sudah diterima pada pekerjaan ini", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Gagal mengambil data pekerjaan", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
            Intent intent = new Intent(PekerjaProfileActivity.this, PelangganMainActivity.class);
            startActivity(intent);
        });
    }

    private void backPage() {
        startActivity(new Intent(PekerjaProfileActivity.this, PekerjaMainActivity.class));
        finish();
    }
}