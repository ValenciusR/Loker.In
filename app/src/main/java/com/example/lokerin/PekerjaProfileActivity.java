package com.example.lokerin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PekerjaProfileActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference, reviewsReferece;
    FirebaseUser fuser;
    private User user;

    private FlexboxLayout flKeterampilan;
    private ImageView ivProfilePicture, btnBack, ivProfileNavbar;
    private TextView tvPageTitle, tvName, tvLocation, tvPhone, tvEmail,tvAboutMe, tvEmptyPortofolio, tvEmptyReview;
    private RecyclerView rvPortofolio, rvReview;
    private ArrayList<Review> reviews;

    private LinearLayoutManager linearLayoutManager, linearLayoutManager2;
    private ListPortofolioAdapter portofolioAdapter;
    private ListReviewAdapter listReviewAdapter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

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
        tvLocation = findViewById(R.id.tv_location_profilePekerjaPage);
        tvAboutMe = findViewById(R.id.tv_aboutMe_profilePekerjaPage);
        tvPhone = findViewById(R.id.tv_phone_profilePekerjaPage);
        tvEmail = findViewById(R.id.tv_email_profilePekerjaPage);
        rvPortofolio = findViewById(R.id.rv_portofolioList_profilePekerjaPage);
        rvReview = findViewById(R.id.rv_reviewList_profilePekerjaPage);
        flKeterampilan = findViewById(R.id.fl_keterampilan_profilePekerjaPage);
        tvEmptyPortofolio = findViewById(R.id.tv_emptyPortofolio_profilePekerjaPage);
        tvEmptyReview = findViewById(R.id.tv_emptyReview_profilePekerjaPage);

        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = firebaseDatabase.getReference().child("users").child(fuser.getUid());
        reviewsReferece = firebaseDatabase.getReference().child("reviews");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Profil");
        ivProfileNavbar.setImageResource(R.drawable.settings_icon);
        ivProfileNavbar.setOnClickListener(v -> showSettings());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if(user != null){
                    tvName.setText(user.getName());
                    tvLocation.setText(user.getLocation());
                    tvAboutMe.setText(user.getAboutMe());
                    tvPhone.setText(user.getPhoneNumber());
                    tvEmail.setText(user.getEmail());

                    setSkills(user);

                    setPortofolio(user);

                    if(user.getImageUrl().equals("default")){
                        ivProfilePicture.setImageResource(R.drawable.default_no_profile_icon);
                    } else{
                        if(!PekerjaProfileActivity.this.isDestroyed()){
                            Glide.with(PekerjaProfileActivity.this).load(user.getImageUrl()).into(ivProfilePicture);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
                        if (fuser.getUid().equalsIgnoreCase(review.getPekerjaId())) {
                            reviews.add(review);
                        }
                    }
                }
                listReviewAdapter.updateList(reviews);
                if(reviews.isEmpty()){
                    tvEmptyReview.setVisibility(View.VISIBLE);
                }else{
                    tvEmptyReview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch review: " + error.getMessage());
            }
        });
    }

    private void setPortofolio(User user) {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    if(user.getPortofolioJob() != null){
                        ArrayList<PortofolioJob> portofolios =  user.getPortofolioJob();
                        portofolioAdapter = new ListPortofolioAdapter(portofolios);
                        linearLayoutManager = new LinearLayoutManager(PekerjaProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        rvPortofolio.setLayoutManager(linearLayoutManager);
                        rvPortofolio.setAdapter(portofolioAdapter);
                        tvEmptyPortofolio.setVisibility(View.GONE);
                    }else{
                        ArrayList<PortofolioJob> portofolios =  new ArrayList<>();
                        portofolioAdapter = new ListPortofolioAdapter(portofolios);
                        linearLayoutManager = new LinearLayoutManager(PekerjaProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        rvPortofolio.setLayoutManager(linearLayoutManager);
                        rvPortofolio.setAdapter(portofolioAdapter);
                        tvEmptyPortofolio.setVisibility(View.VISIBLE);
                    }
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

    private void showSettings() {
        BottomSheetDialog bottomSheetDialogSettings = new BottomSheetDialog(PekerjaProfileActivity.this);

        View viewBottomSheet = LayoutInflater.from(PekerjaProfileActivity.this).inflate(R.layout.bottom_sheet_dialog_settings, null);
        bottomSheetDialogSettings.setContentView(viewBottomSheet);
        bottomSheetDialogSettings.show();
        bottomSheetDialogSettings.getWindow().setDimAmount(0.7f);

        AppCompatButton btnEditPersonalInfo = viewBottomSheet.findViewById(R.id.btn_settings_editPersonalInformation);
        AppCompatButton btnEditKeterampilan = viewBottomSheet.findViewById(R.id.btn_settings_editKeterampilan);
        AppCompatButton btnTambahPekerjaanSebelumnya = viewBottomSheet.findViewById(R.id.btn_settings_tambahPekerjaanSebelumnya);
        AppCompatButton btnLogOut = viewBottomSheet.findViewById(R.id.btn_settings_logOut);
        AppCompatButton btnDeleteAccount = viewBottomSheet.findViewById(R.id.btn_settings_deleteAccount);

        btnEditPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaProfileActivity.this, PekerjaEditProfileActivity.class));
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


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                GoogleSignInClient googleSignIn = GoogleSignIn.getClient(PekerjaProfileActivity.this, options);

                googleSignIn.revokeAccess()
                        .addOnCompleteListener(task -> {
                            startActivity(new Intent(PekerjaProfileActivity.this, LoginActivity.class));
                            finish();
                            sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                        });
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("FirebaseAuth", "User account deleted successfully.");
                                    GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                                    GoogleSignInClient googleSignIn = GoogleSignIn.getClient(PekerjaProfileActivity.this, options);

                                    googleSignIn.revokeAccess()
                                            .addOnCompleteListener(task2 -> {
                                                sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
                                                editor = sharedPreferences.edit();
                                                editor.clear();
                                                editor.apply();

                                                userReference.removeValue();

                                                startActivity(new Intent(PekerjaProfileActivity.this, LoginActivity.class));
                                                finish();
                                            });
                                } else {
                                    Log.e("FirebaseAuth", "Error deleting user account.", task.getException());
                                }
                            });
                } else {
                    Log.d("FirebaseAuth", "No user is currently signed in.");
                }
            }
        });
    }

    private void backPage() {
        startActivity(new Intent(PekerjaProfileActivity.this, PekerjaMainActivity.class));
        finish();
    }
}