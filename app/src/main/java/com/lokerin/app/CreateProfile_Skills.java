package com.lokerin.app;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CreateProfile_Skills extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private ImageView btnBack;
    private AppCompatButton btnNext;
    private TextView tvSkip;

    private EditText etSearch;
    private RecyclerView rvKeterampilanList;
    private AppCompatButton acbTambahKeterampilan;

    private ArrayList<String> skills, dbSkills;
    private LinearLayoutManager linearLayoutManager;
    private ListSkillAdapter listSkillAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile_skills);

        btnBack = findViewById(R.id.iv_back_skillsPage);
        btnNext = findViewById(R.id.acb_next_skillsPage);
        tvSkip = findViewById(R.id.tv_skip_skillsPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(this, CreateProfile_AboutMe.class));
            finish();
        });

        tvSkip.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, CreateProfile_WorkExperience.class);
            startActivity(loginIntent);
        });

        etSearch = findViewById(R.id.et_searchBar_addKeterampilanPage);
        rvKeterampilanList = findViewById(R.id.rv_keterampilanList_addKeterampilanPage);
        acbTambahKeterampilan = findViewById(R.id.acb_tambahKeterampilan_addKeterampilanPage);

        skills = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listSkillAdapter = new ListSkillAdapter(this, skills);
        rvKeterampilanList.setLayoutManager(linearLayoutManager);
        rvKeterampilanList.setAdapter(listSkillAdapter);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("skill").exists()) {
                    skills = (ArrayList<String>) snapshot.child("skill").getValue();
                    if(!skills.isEmpty()) {
                        listSkillAdapter = new ListSkillAdapter(CreateProfile_Skills.this, skills);
                        rvKeterampilanList.setAdapter(listSkillAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSkills(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

//        Get all Skills from DB
        acbTambahKeterampilan.setOnClickListener(v -> {
            showAddSkillDialog();
        });

        btnNext.setOnClickListener(view -> {
            Boolean isValid = true;
            if(this.skills.isEmpty()) {
                isValid = false;
                Toast.makeText(this, "Minimal tambahkan satu keterampilan!", Toast.LENGTH_SHORT).show();
            }

            if(isValid) {
                Intent loginIntent = new Intent(this, CreateProfile_WorkExperience.class);
                startActivity(loginIntent);
            }
        });
    }

    private void showAddSkillDialog() {
        Dialog dialog = new Dialog(CreateProfile_Skills.this);
        dialog.setContentView(R.layout.confirmation_add_skill_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        dbSkills = new ArrayList<>();
        dbSkills.add("Bongkar Muat Barang");
        dbSkills.add("Cetak Sablon");
        dbSkills.add("Instalasi Listrik Bangunan");
        dbSkills.add("Juru Masak");
        dbSkills.add("Mekanik Kendaraan Bermotor");
        dbSkills.add("Mekanik Sepeda Motor");
        dbSkills.add("Operasi Alat Berat");
        dbSkills.add("Operator CCTV");
        dbSkills.add("Operator conveyor Tambang");
        dbSkills.add("Operator Crane Pelabuhan");
        dbSkills.add("Operator Forklift");
        dbSkills.add("Operator Genset");
        dbSkills.add("Operator Mesin Press");
        dbSkills.add("Operator Mesin Tekstil");
        dbSkills.add("Operator Pengeboran Minyak dan Gas");
        dbSkills.add("Pandai Besi");
        dbSkills.add("Pekerja Tambang Batu Bara");
        dbSkills.add("Pekerjaan Scaffolding");
        dbSkills.add("Pelayan Restoran/Kafe");
        dbSkills.add("Pemanenan Hasil Tani");
        dbSkills.add("Pemangkasan Tanaman");
        dbSkills.add("Pemasangan Aksesoris Kendaraaan");
        dbSkills.add("Pemasangan CCTV");
        dbSkills.add("Pemasangan Kaca dan Alumunium");
        dbSkills.add("Pemasangan Keramik");
        dbSkills.add("Pemasangan Pagar");
        dbSkills.add("Pemasangan Pipa Air");
        dbSkills.add("Pemasangan Rangka Atap");
        dbSkills.add("Pemasangan Sistem Irigasi");
        dbSkills.add("Pembersi Rumah");
        dbSkills.add("Pembersih Gedung");
        dbSkills.add("Pembersin Kaca Gedung Tinggi");
        dbSkills.add("Pembuat Perabot Mebel");
        dbSkills.add("Pembudidayaan Ikan");
        dbSkills.add("Pembungkusan dan Pengemasan");
        dbSkills.add("Pemeliharaan Alat Pertanian");
        dbSkills.add("Pemeliharaan Kendaraan Logistik");
        dbSkills.add("Pemeliharaan Kolam Ikan");
        dbSkills.add("Pemeliharaan Sapi dan Unggas");
        dbSkills.add("Penata Rambut");
        dbSkills.add("Penata Rias");
        dbSkills.add("Penganganan Hasil Panen");
        dbSkills.add("Penganyam Rotan");
        dbSkills.add("Pengasuh Anak");
        dbSkills.add("Pengawas Keselamatan Kerja");
        dbSkills.add("Pengecoran Beton");
        dbSkills.add("Pengelasan");
        dbSkills.add("Pengelasan Pipa Industri");
        dbSkills.add("Pengemudi Kendaraan Umum");
        dbSkills.add("Pengemudi Mobil Box");
        dbSkills.add("Pengemudi Ojek Online");
        dbSkills.add("Pengemudi Pribadi");
        dbSkills.add("Pengemudi Truk");
        dbSkills.add("Pengolahan Hasil Laut");
        dbSkills.add("Pengolahan Hasil Tani");
        dbSkills.add("Pengrajin Logam");
        dbSkills.add("Pengrajin Mebel");
        dbSkills.add("Penjaga Kebun atau Lahan");
        dbSkills.add("Penjaga Pintu Gerbang Industri");
        dbSkills.add("Penjaga Toko");
        dbSkills.add("Penyemprotan Pestisida");
        dbSkills.add("Penyusunan Barang Ekspor/Impor");
        dbSkills.add("Perawat Lansia");
        dbSkills.add("Petugas Keamanan");
        dbSkills.add("Petugas Kebersihan Jalan");
        dbSkills.add("Petugas Pemadam Kebakaran Pabrik");
        dbSkills.add("Petugas Sanitasi");
        dbSkills.add("Plester dan Acian Dinding");
        dbSkills.add("Reparasi Ban Kendaraan");
        dbSkills.add("Reparasi Pompa Air");
        dbSkills.add("Sedot WC");
        dbSkills.add("Servis Elektronik Kecil");
        dbSkills.add("Servis Jam Tangan");
        dbSkills.add("Servis Sepatu");
        dbSkills.add("Servis Sepeda Motor");
        dbSkills.add("Tekinisi Boiler");
        dbSkills.add("Teknik Perawatan Mesin");
        dbSkills.add("Teknisi AC Kendaraan");
        dbSkills.add("Teknisi Alat Berat Tambang");
        dbSkills.add("Teknisi Alat Transportasi Laut");
        dbSkills.add("Teknisi Alat Transportasi Udara");
        dbSkills.add("Teknisi Elektronik Rumah Tangga");
        dbSkills.add("Teknisi Hidrolik");
        dbSkills.add("Teknisi HVAC (AC dan Ventilasi)");
        dbSkills.add("Teknisi Kabel Listrik");
        dbSkills.add("Teknisi Kelistrikan Industri");
        dbSkills.add("Teknisi Kendaraan Berat");
        dbSkills.add("Teknisi Kendaraan Bermotor");
        dbSkills.add("Teknisi Komputer");
        dbSkills.add("Teknisi Listrik");
        dbSkills.add("Teknisi Mesin Traktor");
        dbSkills.add("Teknisi Panel Surya");
        dbSkills.add("Teknisi Pembangkit Listrik");
        dbSkills.add("Teknisi Rem dan Suspensi");
        dbSkills.add("Teknisi Reparasi Mesin Industri");
        dbSkills.add("Teknisi Sistem Kelistrikan Kendaraan");
        dbSkills.add("Tukang Batu");
        dbSkills.add("Tukang Besi");
        dbSkills.add("Tukang Cat");
        dbSkills.add("Tukang Cukur");
        dbSkills.add("Tukang Jahit");
        dbSkills.add("Tukang Kayu");
        dbSkills.add("Tukang Kebun");
        dbSkills.add("Tukang Pijat");
        dbSkills.add("Tukang Poles Perhiasan");
        dbSkills.add("Tukang Sepatu");
        dbSkills.add("Tukang Ukir");
        TextView title = dialog.findViewById(R.id.tv_title_addSkillPopUp);
        AutoCompleteTextView searchSkill = dialog.findViewById(R.id.actv_search_addSkillPopup);
//        Remove duplicate skills
        dbSkills.removeAll(skills);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dbSkills);
        searchSkill.setAdapter(arrayAdapter);
        searchSkill.setOnClickListener(v -> {
            searchSkill.showDropDown();
        });
        searchSkill.setOnFocusChangeListener((v, hasFocus) -> searchSkill.showDropDown());

        Button btnCancel = dialog.findViewById(R.id.acb_cancel_addSkillPopUp);
        Button btnConfirm = dialog.findViewById(R.id.acb_confirm_addSkillPopUp);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
//            Save Skill to DB & user
            String result = searchSkill.getText().toString();
            if(dbSkills.contains(result)) {
//                userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
                Map<String, Object> updates = new HashMap<>();
                this.skills.add(result);
                Collections.sort(this.skills);
                updates.put("skill", this.skills);
                this.userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        this.listSkillAdapter = new ListSkillAdapter(CreateProfile_Skills.this, this.skills);
                        this.rvKeterampilanList.setAdapter(listSkillAdapter);
                        dialog.dismiss();
                    } else {
                        // Handle the error here
                        Toast.makeText(this, "Gagal menyimpan data user", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }

    private void filterSkills(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String str : skills) {
            if (str != null && str.toLowerCase().contains(query.toLowerCase())) {
                //FILTER ONLY APPLIED JOB BY USER ID
                filteredList.add(str);
            }
        }
        listSkillAdapter.updateList(filteredList);
    }
}