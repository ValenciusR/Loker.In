package com.lokerin.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PekerjaAddWorkExperienceActivity extends AppCompatActivity {

    private ImageView btnBack, ivProfilePicture;
    private TextView tvPageTitle;
    private EditText etSearchBar;
    private RecyclerView rvPekerjaanList;
    private AppCompatButton acpTambahPekerjaan;
    private ArrayList<PortofolioJob> portofolios;
    private LinearLayoutManager linearLayoutManager;
    private ListAddWorkExperienceAdapter listAddWorkExperienceAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_add_work_experience);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etSearchBar = findViewById(R.id.et_searchBar_tambahPekerjaanSebelumnyaPage);
        rvPekerjaanList = findViewById(R.id.rv_pekerjaanList_tambahPekerjaanSebelumnyaPage);
        acpTambahPekerjaan = findViewById(R.id.acb_tambahPekerjaan_tambahPekerjaanSebelumnyaPage);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfilePicture = findViewById(R.id.btn_profile_toolbar);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaAddWorkExperienceActivity.this, PekerjaProfileActivity.class));
                finish();
            }
        });
        tvPageTitle.setText("Tambah Portofolio");
        ivProfilePicture.setVisibility(View.GONE);

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

                linearLayoutManager = new LinearLayoutManager(PekerjaAddWorkExperienceActivity.this, LinearLayoutManager.VERTICAL, false);
                listAddWorkExperienceAdapter = new ListAddWorkExperienceAdapter(PekerjaAddWorkExperienceActivity.this, portofolios,"Profile");
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
                Intent intent = new Intent(PekerjaAddWorkExperienceActivity.this, PekerjaModifyWorkExperienceActivity.class);
                intent.putExtra("activityOrigin", "ViewProfile");
                startActivity(intent);
                finish();
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