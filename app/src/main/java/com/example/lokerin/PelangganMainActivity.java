package com.example.lokerin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PelangganMainActivity extends AppCompatActivity {

    private ImageView ivProfileNavbar;
    private TextView tvPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PelangganMyJobFragment())
                .commit();

        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        tvPageTitle.setText("RIWAYAT");
        ivProfileNavbar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(PelangganMainActivity.this, PelangganProfileActivity.class));
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                selectedFragment = new PelangganMyJobFragment();
                tvPageTitle.setText("RIWAYAT");
            } else if (id == R.id.bottom_search) {
                selectedFragment = new PelangganAddJobFragment();
                tvPageTitle.setText("TAMBAH PEKERJAAN");
            } else if (id == R.id.bottom_chat) {
//                selectedFragment = new ChatFragment();
                selectedFragment = new PekerjaChatFragment();
                tvPageTitle.setText("PESAN");
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
}