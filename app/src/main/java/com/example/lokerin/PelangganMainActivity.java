package com.example.lokerin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PelangganMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_main);

        ImageView btnBackToolbar = findViewById(R.id.btn_back_toolbar);
        ImageView btnProfileToolbar = findViewById(R.id.btn_back_toolbar);
        TextView tvPageToolbar = findViewById(R.id.tv_page_toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                    selectedFragment = new PelangganMyJobFragment();
                    tvPageToolbar.setText("Home");
            } else if (id == R.id.bottom_search) {
                    selectedFragment = new PelangganAddJobFragment();
                    tvPageToolbar.setText("Search");
            } else if (id == R.id.bottom_chat) {
                    selectedFragment = new PelangganChatFragment();
                    tvPageToolbar.setText("Chat");
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Load the default fragment (home)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new PelangganMyJobFragment())
                    .commit();
        }
    }
}