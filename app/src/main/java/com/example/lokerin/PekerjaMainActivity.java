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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pekerja_main);

        ImageView btnBackToolbar = findViewById(R.id.btn_back_toolbar);
        ImageView btnProfileToolbar = findViewById(R.id.btn_profile_toolbar);
        TextView tvPageToolbar = findViewById(R.id.tv_page_toolbar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PelangganMyJobFragment())
                .commit();
        tvPageToolbar.setText("Home");

        btnProfileToolbar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(PekerjaMainActivity.this, ProfilePekerjaActivity.class));
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                    selectedFragment = new PekerjaMyJobFragment();
                    tvPageToolbar.setText("Home");
            } else if (id == R.id.bottom_search) {
                    selectedFragment = new PekerjaApplyJobFragment();
                    tvPageToolbar.setText("Search");
            } else if (id == R.id.bottom_chat) {
                    selectedFragment = new PekerjaChatFragment();
                    tvPageToolbar.setText("Chat");
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