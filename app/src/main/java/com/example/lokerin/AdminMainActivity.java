package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private ImageView ivProfileNavbar;
    private TextView tvPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AdminHomeFragment())
                .commit();

        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        tvPageTitle.setText("BERANDA");
        ivProfileNavbar.setVisibility(View.GONE);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                selectedFragment = new AdminHomeFragment();
                tvPageTitle.setText("BERANDA");
            } else if (id == R.id.bottom_users) {
                selectedFragment = new AdminUsersFragment();
                tvPageTitle.setText("DAFTAR PENGGUNA");
            } else if (id == R.id.bottom_jobs) {
                selectedFragment = new AdminJobsFragment();
                tvPageTitle.setText("DAFTAR PEKERJAAN");
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