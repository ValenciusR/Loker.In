package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

public class PekerjaEditPortofolioActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvPageTitle;
    private EditText etSearchBar;
    private RecyclerView rvPortofolioJob;
    private ArrayList<PortofolioJob> portofolios;
    private LinearLayoutManager linearLayoutManager;
    private ListEditPortofolioAdapter editPortofolioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_edit_portofolio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        etSearchBar = findViewById(R.id.et_searchBar_editPortofolioPage);
        rvPortofolioJob = findViewById(R.id.rv_portofolioList_editPortofolioPage);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Ubah Portofolio");

//        Set Edit Portofolio Recycler View
        PortofolioJob templatePortofolioJob = new PortofolioJob("Plumbing", "Jakarta", "Construction", new Date(), false);
        portofolios = new ArrayList<>();
        portofolios.add(templatePortofolioJob);
        portofolios.add(templatePortofolioJob);
        portofolios.add(templatePortofolioJob);
        portofolios.add(templatePortofolioJob);
        portofolios.add(templatePortofolioJob);

        linearLayoutManager = new LinearLayoutManager(PekerjaEditPortofolioActivity.this, LinearLayoutManager.VERTICAL, false);
        editPortofolioAdapter = new ListEditPortofolioAdapter(portofolios);
        rvPortofolioJob.setLayoutManager(linearLayoutManager);
        rvPortofolioJob.setAdapter(editPortofolioAdapter);
    }

    private void backPage() {
        startActivity(new Intent(PekerjaEditPortofolioActivity.this, PekerjaMainActivity.class));
        finish();
    }

}