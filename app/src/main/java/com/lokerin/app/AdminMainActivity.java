package com.lokerin.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);

        String targetFragment = getIntent().getStringExtra("targetFragment");
        if (targetFragment != null) {
            if ("AdminUsersFragment".equals(targetFragment)) {
                loadFragment(new AdminUsersFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottom_users);
            } else if ("AdminJobsFragment".equals(targetFragment)) {
                loadFragment(new AdminJobsFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottom_jobs);
            } else {
                loadFragment(new AdminHomeFragment());
                bottomNavigationView.setSelectedItemId(R.id.bottom_home);
            }
        } else {
            loadFragment(new AdminHomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        }

        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        tvPageTitle.setText("DASBOR");
        ivProfileNavbar.setVisibility(View.GONE);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                selectedFragment = new AdminHomeFragment();
                tvPageTitle.setText("DASBOR");
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

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}