package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PekerjaMainActivity extends AppCompatActivity {

    private ImageView ivProfileNavbar;
    private TextView tvPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pekerja_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PekerjaApplyJobFragment())
                .commit();

        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        tvPageTitle.setText("DAFTAR PEKERJAAN");
        ivProfileNavbar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(PekerjaMainActivity.this, PekerjaProfileActivity.class));
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_search);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                selectedFragment = new PekerjaMyJobFragment();
                tvPageTitle.setText("BERANDA");
            } else if (id == R.id.bottom_search) {
                selectedFragment = new PekerjaApplyJobFragment();
                tvPageTitle.setText("DAFTAR PEKERJAAN");
            } else if (id == R.id.bottom_chat) {
//                selectedFragment = new PekerjaChatFragment();
                selectedFragment = new ChatFragment();
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