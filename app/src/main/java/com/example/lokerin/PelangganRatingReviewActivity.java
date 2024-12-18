package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PelangganRatingReviewActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvPageTitle, tvTitle, tvSubtitle;
    private RatingBar rbRating;
    private CheckBox cbRecommend;
    private EditText etReviewRate;
    private AppCompatButton btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pelanggan_rating_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvTitle = findViewById(R.id.title);
        tvSubtitle = findViewById(R.id.subtitle);
        rbRating = findViewById(R.id.rating_bar);
        cbRecommend = findViewById(R.id.checkbox_recommend);
        etReviewRate = findViewById(R.id.et_review_rate);
        btnPublish = findViewById(R.id.btn_login);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPage();
            }
        });
        tvPageTitle.setText("Ulasan");

        btnPublish.setOnClickListener(v -> {
            boolean isValid = validateInput();
            if (isValid) {
                Toast.makeText(this, "Ulasan berhasil diunggah!", Toast.LENGTH_SHORT).show();
                // Logic publish data
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        String review = etReviewRate.getText().toString().trim();
        if (review.length() < 20) {
            etReviewRate.setBackgroundResource(R.drawable.shape_rounded_red_border);
            Toast.makeText(this, "Ulasan minimal berisi 20 huruf!", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else {
            etReviewRate.setBackgroundResource(R.drawable.shape_rounded_blue_border);
        }

        if (rbRating.getRating() == 0) {
            Toast.makeText(this, "Pilih skala rating (1 - 5)!", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void backPage() {
        finish();
    }
}
