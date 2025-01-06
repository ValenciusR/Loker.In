package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PekerjaApplyJobFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference jobsRef, usersRef;

    private TextView tvViewAll;
    private RecyclerView rvCategorizedJobs, rvRecommendedJobs;
    private String currentUserId;
    private int counter;

    private List<Job> jobList = new ArrayList<>();
    private ListJobAdapter adapter;

    private static final HashMap<String, String> skillCategoryMap = new HashMap<>();

    static {
        skillCategoryMap.put("Instalasi Listrik Bangunan", "KONSTRUKSI");
        skillCategoryMap.put("Operasi Alat Berat", "KONSTRUKSI");
        skillCategoryMap.put("Pekerjaan Scaffolding", "KONSTRUKSI");
        skillCategoryMap.put("Pemasangan Kaca dan Alumunium", "KONSTRUKSI");
        skillCategoryMap.put("Pemasangan Keramik", "KONSTRUKSI");
        skillCategoryMap.put("Pemasangan Pagar", "KONSTRUKSI");
        skillCategoryMap.put("Pemasangan Pipa Air", "KONSTRUKSI");
        skillCategoryMap.put("Pemasangan Rangka Atap", "KONSTRUKSI");
        skillCategoryMap.put("Pengecoran Beton", "KONSTRUKSI");
        skillCategoryMap.put("Pengelasan", "KONSTRUKSI");
        skillCategoryMap.put("Plester dan Acian Dinding", "KONSTRUKSI");
        skillCategoryMap.put("Teknisi HVAC (AC dan Ventilasi)", "KONSTRUKSI");
        skillCategoryMap.put("Tukang Batu", "KONSTRUKSI");
        skillCategoryMap.put("Tukang Besi", "KONSTRUKSI");
        skillCategoryMap.put("Tukang Cat", "KONSTRUKSI");
        skillCategoryMap.put("Tukang Kayu", "KONSTRUKSI");

        skillCategoryMap.put("Operator Forklift", "MANUFAKTUR");
        skillCategoryMap.put("Operator Mesin Press", "MANUFAKTUR");
        skillCategoryMap.put("Operator Mesin Tekstil", "MANUFAKTUR");
        skillCategoryMap.put("Pembungkusan dan Pengemasan", "MANUFAKTUR");
        skillCategoryMap.put("Pengelasan Pipa Industri", "MANUFAKTUR");
        skillCategoryMap.put("Tekinisi Boiler", "MANUFAKTUR");
        skillCategoryMap.put("Teknik Perawatan Mesin", "MANUFAKTUR");
        skillCategoryMap.put("Teknisi Hidrolik", "MANUFAKTUR");
        skillCategoryMap.put("Teknisi Reparasi Mesin Industri", "MANUFAKTUR");

        skillCategoryMap.put("Bongkar Muat Barang", "TRANSPORT");
        skillCategoryMap.put("Mekanik Sepeda Motor", "TRANSPORT");
        skillCategoryMap.put("Operator Crane Pelabuhan", "TRANSPORT");
        skillCategoryMap.put("Pemasangan Aksesoris Kendaraaan", "TRANSPORT");
        skillCategoryMap.put("Pemeliharaan Kendaraan Logistik", "TRANSPORT");
        skillCategoryMap.put("Pengemudi Kendaraan Umum", "TRANSPORT");
        skillCategoryMap.put("Pengemudi Mobil Box", "TRANSPORT");
        skillCategoryMap.put("Pengemudi Ojek Online", "TRANSPORT");
        skillCategoryMap.put("Pengemudi Pribadi", "TRANSPORT");
        skillCategoryMap.put("Pengemudi Truk", "TRANSPORT");
        skillCategoryMap.put("Penyusunan Barang Ekspor/Impor", "TRANSPORT");
        skillCategoryMap.put("Reparasi Ban Kendaraan", "TRANSPORT");
        skillCategoryMap.put("Teknisi AC Kendaraan", "TRANSPORT");
        skillCategoryMap.put("Teknisi Alat Transportasi Laut", "TRANSPORT");
        skillCategoryMap.put("Teknisi Alat Transportasi Udara", "TRANSPORT");
        skillCategoryMap.put("Teknisi Kendaraan Berat", "TRANSPORT");
        skillCategoryMap.put("Teknisi Kendaraan Bermotor", "TRANSPORT");
        skillCategoryMap.put("Teknisi Rem dan Suspensi", "TRANSPORT");
        skillCategoryMap.put("Teknisi Sistem Kelistrikan Kendaraan", "TRANSPORT");

        skillCategoryMap.put("Juru Masak", "LAYANAN");
        skillCategoryMap.put("Mekanik Kendaraan Bermotor", "LAYANAN");
        skillCategoryMap.put("Pelayan Restoran/Kafe", "LAYANAN");
        skillCategoryMap.put("Penata Rambut", "LAYANAN");
        skillCategoryMap.put("Penata Rias", "LAYANAN");
        skillCategoryMap.put("Pengasuh Anak", "LAYANAN");
        skillCategoryMap.put("Penjahit", "LAYANAN");
        skillCategoryMap.put("Perawat Lansia", "LAYANAN");
        skillCategoryMap.put("Reparasi Pompa Air", "LAYANAN");
        skillCategoryMap.put("Sedot WC", "LAYANAN");
        skillCategoryMap.put("Servis Sepeda Motor", "LAYANAN");
        skillCategoryMap.put("Teknisi Elektronik Kecil", "LAYANAN");
        skillCategoryMap.put("Teknisi Kabel Listrik", "LAYANAN");
        skillCategoryMap.put("Teknisi Komputer", "LAYANAN");
        skillCategoryMap.put("Teknisi Listrik", "LAYANAN");
        skillCategoryMap.put("Tukang Cukur", "LAYANAN");
        skillCategoryMap.put("Tukang Pijat", "LAYANAN");
        skillCategoryMap.put("Tukang Sepatu", "LAYANAN");

        skillCategoryMap.put("Pemanenan Hasil Tani", "AGRIKULTUR");
        skillCategoryMap.put("Pemangkasan Tanaman", "AGRIKULTUR");
        skillCategoryMap.put("Pemasangan Sistem Irigasi", "AGRIKULTUR");
        skillCategoryMap.put("Pembudidayaan Ikan", "AGRIKULTUR");
        skillCategoryMap.put("Pemeliharaan Alat Pertanian", "AGRIKULTUR");
        skillCategoryMap.put("Pemeliharaan Kolam Ikan", "AGRIKULTUR");
        skillCategoryMap.put("Pemeliharaan Sapi dan Unggas", "AGRIKULTUR");
        skillCategoryMap.put("Penganganan Hasil Panen", "AGRIKULTUR");
        skillCategoryMap.put("Pengolahan Hasil Laut", "AGRIKULTUR");
        skillCategoryMap.put("Pengolahan Hasil Tani", "AGRIKULTUR");
        skillCategoryMap.put("Penjaga Kebun atau Lahan", "AGRIKULTUR");
        skillCategoryMap.put("Penyemprotan Pestisida", "AGRIKULTUR");
        skillCategoryMap.put("Teknisi Mesin Traktor", "AGRIKULTUR");

        skillCategoryMap.put("Cetak Sablon", "KERAJINAN");
        skillCategoryMap.put("Pandai Besi", "KERAJINAN");
        skillCategoryMap.put("Pembuat Perabot Mebel", "KERAJINAN");
        skillCategoryMap.put("Penganyam Rotan", "KERAJINAN");
        skillCategoryMap.put("Pengrajin Logam", "KERAJINAN");
        skillCategoryMap.put("Pengrajin Mebel", "KERAJINAN");
        skillCategoryMap.put("Servis Jam Tangan", "KERAJINAN");
        skillCategoryMap.put("Servis Sepatu", "KERAJINAN");
        skillCategoryMap.put("Tukang Poles Perhiasan", "KERAJINAN");
        skillCategoryMap.put("Tukang Ukir", "KERAJINAN");

        skillCategoryMap.put("Petugas Sanitasi", "KEBERSIHAN");
        skillCategoryMap.put("Petugas Kebersihan Jalan", "KEBERSIHAN");
        skillCategoryMap.put("Pembersih Kaca Gedung Tinggi", "KEBERSIHAN");
        skillCategoryMap.put("Pembersih Gedung", "KEBERSIHAN");
        skillCategoryMap.put("Tukang Kebun", "KEBERSIHAN");
        skillCategoryMap.put("Pembersih Rumah", "KEBERSIHAN");

        skillCategoryMap.put("Operator CCTV", "KEAMANAN");
        skillCategoryMap.put("Pemasangan CCTV", "KEAMANAN");
        skillCategoryMap.put("Pengawas Keselamatan Kerja", "KEAMANAN");
        skillCategoryMap.put("Penjaga Pintu Gerbang Industri", "KEAMANAN");
        skillCategoryMap.put("Penjaga toko", "KEAMANAN");
        skillCategoryMap.put("Petugas Keamanan", "KEAMANAN");
        skillCategoryMap.put("Petugas Pemadam Kebakaran Pabrik", "KEAMANAN");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pekerja_applyjob, container, false);

        FirebaseApp.initializeApp(requireContext());
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        jobsRef = firebaseDatabase.getReference("jobs");
        usersRef = firebaseDatabase.getReference("users");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();

            usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        DataSnapshot skillSnapshot = snapshot.child("skill");
                        if (skillSnapshot.exists() && skillSnapshot.hasChildren()) {
                            String firstSkill = skillSnapshot.getChildren().iterator().next().getValue(String.class);
                            String category = mapSkillToCategory(firstSkill);
                            fetchJobsFromFirebase(category);
                            Log.d("Firebase", "Skill pertama: " + firstSkill + ", Kategori: " + category);
                        } else {
                            fetchJobsFromFirebase("no_skill");
                            Log.d("Firebase", "Skill kosong");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error fetching user data", error.toException());
                }
            });

        } else {
            currentUserId = "noId";
        }

        tvViewAll = view.findViewById(R.id.viewAllTextView);
        tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onViewAllClick(view);
            }
        });

        rvCategorizedJobs = view.findViewById(R.id.recyclerViewCategory);
        rvCategorizedJobs.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        List<Category> items = new ArrayList<>();
        items.add(new Category(R.drawable.img_gardener, "AGRIKULTUR"));
        items.add(new Category(R.drawable.img_security_guard, "KEAMANAN"));
        items.add(new Category(R.drawable.img_maid, "KEBERSIHAN"));
        items.add(new Category(R.drawable.img_craftsman, "KERAJINAN"));
        items.add(new Category(R.drawable.img_builder, "KONSTRUKSI"));
        items.add(new Category(R.drawable.img_barber, "LAYANAN"));
        items.add(new Category(R.drawable.img_factory_worker, "MANUFAKTUR"));
        items.add(new Category(R.drawable.img_driver, "TRANSPORT"));

        ListCategoryAdapter listCategoryAdapter = new ListCategoryAdapter(getActivity(), items);
        rvCategorizedJobs.setAdapter(listCategoryAdapter);

        rvRecommendedJobs = view.findViewById(R.id.recyclerView);
        rvRecommendedJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListJobAdapter(getActivity(), jobList);
        rvRecommendedJobs.setAdapter(adapter);

        return view;
    }

    public void onViewAllClick(View view) {
        Intent intent = new Intent(getActivity(), PekerjaRecommendJobActivity.class);
        startActivity(intent);
    }

    private void fetchJobsFromFirebase(String category) {
        counter = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        Date currentDate = new Date();
        String currentDateString = dateFormat.format(currentDate);

        Date referenceDate = null;
        try {
            referenceDate = dateFormat.parse(currentDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date finalReferenceDate = referenceDate;
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                if (category.equalsIgnoreCase("no_skill")) {
                    for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job != null) {
                            if ("OPEN".equalsIgnoreCase(job.getJobStatus())) {
                                try {
                                    Date jobDeadline = dateFormat.parse(job.getJobDateClose());
                                    if (jobDeadline != null && !jobDeadline.before(finalReferenceDate)) {
                                        jobList.add(job);
                                        counter++;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (counter == 5) {
                            break;
                        }
                    }
                } else {
                    for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                        Job job = jobSnapshot.getValue(Job.class);
                        if (job != null) {
                            if ("OPEN".equalsIgnoreCase(job.getJobStatus()) && category.equalsIgnoreCase(job.getJobCategory())) {
                                try {
                                    Date jobDeadline = dateFormat.parse(job.getJobDateClose());
                                    if (jobDeadline != null && !jobDeadline.before(finalReferenceDate)) {
                                        jobList.add(job);
                                        counter++;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (counter == 5) {
                            break;
                        }
                    }

                    // Handler if no suitable data found
                    if (counter < 5) {
                        for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                            Job job = jobSnapshot.getValue(Job.class);
                            if (job != null) {
                                if ("OPEN".equalsIgnoreCase(job.getJobStatus())) {
                                    try {
                                        Date jobDeadline = dateFormat.parse(job.getJobDateClose());
                                        if (jobDeadline != null && !jobDeadline.before(finalReferenceDate)) {
                                            jobList.add(job);
                                            counter++;
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (counter == 5) {
                                break;
                            }
                        }
                    }
                }
                adapter.updateList(jobList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch jobs: " + error.getMessage());
            }
        });
    }

    private String mapSkillToCategory(String skill) {
        return skillCategoryMap.get(skill);
    }
}
