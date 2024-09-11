package com.example.lokerin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView btnBackToolbar = findViewById(R.id.btn_back_toolbar);
        ImageView btnProfileToolbar = findViewById(R.id.btn_back_toolbar);
        TextView tvPageToolbar = findViewById(R.id.tv_page_toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                    selectedFragment = new HomeFragment();
                    tvPageToolbar.setText("Home");
            } else if (id == R.id.bottom_search) {
                    selectedFragment = new SearchFragment();
                    tvPageToolbar.setText("Search");
            } else if (id == R.id.bottom_chat) {
                    selectedFragment = new ChatFragment();
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
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }
}