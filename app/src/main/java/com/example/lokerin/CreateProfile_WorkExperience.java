package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateProfile_WorkExperience extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private ImageView btnBack;
    private AppCompatButton btnNext;
    private TextView tvSkip;

    private EditText etSearchBar;
    private RecyclerView rvPekerjaanList;
    private AppCompatButton acpTambahPekerjaan;
    private ArrayList<PortofolioJob> portofolios;
    private LinearLayoutManager linearLayoutManager;
    private ListAddWorkExperienceAdapter listAddWorkExperienceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile_work_experience);

        btnBack = findViewById(R.id.iv_back_profileWorkExperiencePage);
        btnNext = findViewById(R.id.acb_next_profileWorkExperiencePage);
        tvSkip = findViewById(R.id.tv_skip_profileWorkExperiencePage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(this, CreateProfile_Skills.class));
            finish();
        });

        btnNext.setOnClickListener(view -> {
            startActivity(new Intent(this, PekerjaMainActivity.class));
            finish();
        });

        tvSkip.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, PekerjaMainActivity.class);
            startActivity(loginIntent);
            finish();
        });

        etSearchBar = findViewById(R.id.et_searchBar_tambahPekerjaanSebelumnyaPage);
        rvPekerjaanList = findViewById(R.id.rv_pekerjaanList_tambahPekerjaanSebelumnyaPage);
        acpTambahPekerjaan = findViewById(R.id.acb_tambahPekerjaan_tambahPekerjaanSebelumnyaPage);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                portofolios = new ArrayList<>();
                if(user.getPortofolioJob() != null){
                    portofolios = user.getPortofolioJob();
                }

                linearLayoutManager = new LinearLayoutManager(CreateProfile_WorkExperience.this, LinearLayoutManager.VERTICAL, false);
                listAddWorkExperienceAdapter = new ListAddWorkExperienceAdapter(CreateProfile_WorkExperience.this, portofolios, "CreateProfile");
                rvPekerjaanList.setLayoutManager(linearLayoutManager);
                rvPekerjaanList.setAdapter(listAddWorkExperienceAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPortofolios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        acpTambahPekerjaan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateProfile_WorkExperience.this, PekerjaModifyWorkExperienceActivity.class);
                intent.putExtra("activityOrigin", "CreateProfile");
                startActivity(intent);
            }
        });
    }

    private void filterPortofolios(String query) {
        ArrayList<PortofolioJob> filteredList = new ArrayList<>();
        for (PortofolioJob porto : portofolios) {
            if (porto.getTitle() != null && porto.getTitle().toLowerCase().contains(query.toLowerCase())) {
                //FILTER ONLY APPLIED JOB BY USER ID
                filteredList.add(porto);
            }
        }
        listAddWorkExperienceAdapter.updateList(filteredList);
    }
}