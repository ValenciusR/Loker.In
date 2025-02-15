package com.lokerin.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PelangganViewProfilePekerjaActivity extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference, jobReference, reviewsReferece;
    private DatabaseReference databaseReference;

    private String userIdFromPelanggan, jobIdFromPelanggan;
    private FlexboxLayout flKeterampilan;
    private ImageView ivProfilePicture, btnBack, ivProfileNavbar;
    private TextView tvPageTitle, tvName, tvLocation, tvAboutMe, tvPhone, tvEmail, tvEmptyPortofolio;
    private RecyclerView rvPortofolio, rvReview;
    private ArrayList<Portofolio> portofolios;
    private ArrayList<Review> reviews;
    private ArrayList<String> applicantsList, workersList;

    private LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    private ListPortofolioAdapter portofolioAdapter;
    private ListReviewAdapter listReviewAdapter;
    private Button btnChat, btnAction;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pelanggan_view_profile_pekerja);
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
        tvLocation = findViewById(R.id.tv_location_profilePekerjaPage);
        tvAboutMe = findViewById(R.id.tv_aboutMe_profilePekerjaPage);
        tvPhone = findViewById(R.id.tv_phone_profilePekerjaPage);
        tvEmail = findViewById(R.id.tv_email_profilePekerjaPage);
        rvPortofolio = findViewById(R.id.rv_portofolioList_profilePekerjaPage);
        tvEmptyPortofolio = findViewById(R.id.tv_emptyPortofolio_profilePekerjaPage);
        rvReview = findViewById(R.id.rv_reviewList_profilePekerjaPage);
        flKeterampilan = findViewById(R.id.fl_keterampilan_profilePekerjaPage);
        btnChat = findViewById(R.id.btn_chat_pelangganViewProfilePekerja);
        btnAction = findViewById(R.id.btn_action_pelangganViewProfilePekerja);

        Intent intent = getIntent();
        userIdFromPelanggan = intent.getStringExtra("USER_ID");
        jobIdFromPelanggan = intent.getStringExtra("JOB_ID");

        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userReference = firebaseDatabase.getReference().child("users").child(userIdFromPelanggan);
        jobReference = firebaseDatabase.getReference().child("jobs").child(jobIdFromPelanggan);
        reviewsReferece = firebaseDatabase.getReference().child("reviews");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PelangganViewProfilePekerjaActivity.this, PelangganDetailJobActivity.class);
                intent.putExtra("jobId", jobIdFromPelanggan);
                startActivity(intent);
                finish();
            }
        });
        tvPageTitle.setText("Profil Pelamar");
        ivProfileNavbar.setVisibility(View.GONE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = firebaseDatabase.getReference().child("users").child(userIdFromPelanggan);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                tvName.setText(user.getName());
                tvPageTitle.setText("Profil "+user.getName());
                tvAboutMe.setText(user.getAboutMe());
                tvLocation.setText(user.getLocation());
                tvPhone.setText(user.getPhoneNumber());
                tvEmail.setText(user.getEmail());
                if(user.getImageUrl().equals("default")){
                    ivProfilePicture.setImageResource(R.drawable.default_no_profile_icon);
                } else{
                    Glide.with(PelangganViewProfilePekerjaActivity.this).load(user.getImageUrl()).into(ivProfilePicture);
                }
                setSkills(user);
                setPortofolio(user);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        ivProfileNavbar.setImageResource(R.drawable.settings_icon);

        portofolios = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        portofolioAdapter = new ListPortofolioAdapter(portofolios);
        rvPortofolio.setLayoutManager(linearLayoutManager);
        rvPortofolio.setAdapter(portofolioAdapter);

        reviews = new ArrayList<Review>();
        listReviewAdapter = new ListReviewAdapter(reviews);
        rvReview.setLayoutManager(new LinearLayoutManager(this));
        rvReview.setAdapter(listReviewAdapter);
        reviewsReferece.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviews.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Review review = jobSnapshot.getValue(Review.class);
                    if (review != null) {
                        if (userIdFromPelanggan.equalsIgnoreCase(review.getPekerjaId())) {
                            reviews.add(review);
                        }
                    }
                }
                listReviewAdapter.updateList(reviews);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch review: " + error.getMessage());
            }
        });

//        List<String> skills = Arrays.asList("Plumbing", "Berkebun", "Service AC", "Maintenance Listrik", "Pengrajin Kayu", "Pandai Besi");
//        for (String skill : skills) {
//            TextView tvSkill = new TextView(this);
//
//            tvSkill.setText(skill);
//
//            tvSkill.setTextColor(getResources().getColor(R.color.blue));
//            tvSkill.setBackgroundResource(R.drawable.shape_rounded_blue_border); // Custom drawable
//            tvSkill.setPadding(50, 25, 50, 25);
//            tvSkill.setTextSize(16);
//            tvSkill.setLayoutParams(new FlexboxLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//            ));
//
//            FlexboxLayout.LayoutParams layoutParams = new FlexboxLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//            );
//            layoutParams.setMargins(0, 0, 25, 25);
//            tvSkill.setLayoutParams(layoutParams);
//
//            flKeterampilan.addView(tvSkill);
//        }

        btnChat.setOnClickListener(v -> {
            Intent intent2 = new Intent(PelangganViewProfilePekerjaActivity.this, ChatActivity.class);
            intent2.putExtra("userid", userIdFromPelanggan);
            startActivity(intent2);
        });

        jobReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String jobStatus = dataSnapshot.child("jobStatus").getValue(String.class);
                    DataSnapshot workersSnapshot = dataSnapshot.child("jobWorkers");

                    if ("ON GOING".equalsIgnoreCase(jobStatus) || "ENDED".equalsIgnoreCase(jobStatus)) {
                        btnAction.setText("PEKERJA TELAH TERDAFTAR");
                        btnAction.setEnabled(false);
                    } else if ("OPEN".equalsIgnoreCase(jobStatus)) {
                        boolean isCurrentUserWorker = false;
                        for (DataSnapshot workerSnapshot : workersSnapshot.getChildren()) {
                            String workerId = workerSnapshot.getValue(String.class);
                            if (workerId != null && workerId.equals(userIdFromPelanggan)) {
                                isCurrentUserWorker = true;
                                break;
                            }
                        }

                        if (isCurrentUserWorker) {
                            btnAction.setText("BATALKAN PEKERJA");
                            btnAction.setEnabled(true);
                            btnAction.setOnClickListener(v -> {
                                showCancelWorkerConfirmationDialog();
                            });
                        } else {
                            btnAction.setText("PILIH PEKERJA");
                            btnAction.setEnabled(true);
                            btnAction.setOnClickListener(v -> {
                                showApproveApplicantConfirmationDialog();
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch job details: " + error.getMessage());
            }
        });
    }

    private void showApproveApplicantConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup_accept_applicant);
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
                                        Intent intent = new Intent(PelangganViewProfilePekerjaActivity.this, PelangganDetailJobActivity.class);
                                        intent.putExtra("jobId", jobIdFromPelanggan);
                                        startActivity(intent);
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
        });
    }

    private void showCancelWorkerConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup_accept_applicant);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Batalkan Pekerja?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        dialog.show();

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            jobReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot snapshot = task.getResult();

                    workersList = new ArrayList<>();
                    if (snapshot.child("jobWorkers").exists()) {
                        workersList = (ArrayList<String>) snapshot.child("jobWorkers").getValue();
                    }
                    if (workersList == null) {
                        workersList = new ArrayList<>();
                    }

                    String userIdToBeRemoved = userIdFromPelanggan;
                    if (userIdToBeRemoved != null && workersList.contains(userIdToBeRemoved)) {
                        workersList.remove(userIdToBeRemoved);
                        jobReference.child("jobWorkers").setValue(workersList).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(this, "Berhasil membatalkan pekerja ini", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Gagal membatalkan pekerja ini", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(PelangganViewProfilePekerjaActivity.this, PelangganDetailJobActivity.class);
                            intent.putExtra("jobId", jobIdFromPelanggan);
                            startActivity(intent);
                        });
                    } else {
                        Toast.makeText(this, "Pekerja ini tidak terdaftar pada pekerjaan ini", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Gagal mengambil data pekerjaan", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.dismiss();
        });
    }


    private void setPortofolio(User user) {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getPortofolioJob() != null){
                    ArrayList<PortofolioJob> portofolios =  user.getPortofolioJob();
                    portofolioAdapter = new ListPortofolioAdapter(portofolios);
                    linearLayoutManager = new LinearLayoutManager(PelangganViewProfilePekerjaActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    rvPortofolio.setLayoutManager(linearLayoutManager);
                    rvPortofolio.setAdapter(portofolioAdapter);
                    tvEmptyPortofolio.setVisibility(View.GONE);
                }else{
                    ArrayList<PortofolioJob> portofolios =  new ArrayList<>();
                    portofolioAdapter = new ListPortofolioAdapter(portofolios);
                    linearLayoutManager = new LinearLayoutManager(PelangganViewProfilePekerjaActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    rvPortofolio.setLayoutManager(linearLayoutManager);
                    rvPortofolio.setAdapter(portofolioAdapter);
                    tvEmptyPortofolio.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}