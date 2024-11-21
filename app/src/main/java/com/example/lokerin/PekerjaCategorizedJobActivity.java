package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PekerjaCategorizedJobActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_categorized_job);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        TextView categoryTextView = findViewById(R.id.tv_page_toolbar);
        if (category != null) {
            categoryTextView.setText(category);
        }
    }
}