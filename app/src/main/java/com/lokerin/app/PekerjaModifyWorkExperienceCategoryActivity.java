package com.lokerin.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PekerjaModifyWorkExperienceCategoryActivity extends AppCompatActivity {

    private CardView[] cards;
    private AppCompatButton btnConfirm;
    private final int selectedColor = Color.parseColor("#21458B");
    private final int defaultColor = Color.parseColor("#FFFFFF");
    private int selectedCardIndex = -1;
    private String selectedCategory = "";
    private ImageView btnBack, ivProfilePicture;
    private TextView tvPageTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_modify_work_experience_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modifyWorkExperienceCategory), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfilePicture = findViewById(R.id.btn_profile_toolbar);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PekerjaModifyWorkExperienceCategoryActivity.this, PekerjaModifyWorkExperienceActivity.class));
                finish();
            }
        });
        tvPageTitle.setText("Pilih Kategori Pekerjaan Sebelumnya");
        ivProfilePicture.setVisibility(View.GONE);

        cards = new CardView[]{
                findViewById(R.id.cv_category1_modifyWorkExperienceCategoryPage),
                findViewById(R.id.cv_category2_modifyWorkExperienceCategoryPage),
                findViewById(R.id.cv_category3_modifyWorkExperienceCategoryPage),
                findViewById(R.id.cv_category4_modifyWorkExperienceCategoryPage),
                findViewById(R.id.cv_category5_modifyWorkExperienceCategoryPage),
                findViewById(R.id.cv_category6_modifyWorkExperienceCategoryPage),
                findViewById(R.id.cv_category7_modifyWorkExperienceCategoryPage),
                findViewById(R.id.cv_category8_modifyWorkExperienceCategoryPage)
        };

        for (int i = 0; i < cards.length; i++) {
            int index = i;
            cards[i].setOnClickListener(v -> selectCard(index));
        }

        btnConfirm = findViewById(R.id.btn_confirm_modifyWorkExperienceCategoryPage);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(PekerjaModifyWorkExperienceCategoryActivity.this, PekerjaModifyWorkExperienceActivity.class));
//                finish();
                if (selectedCardIndex == -1) {
                    Toast.makeText(PekerjaModifyWorkExperienceCategoryActivity.this, "Pilih satu kategori pekerjaan!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("category", selectedCategory);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private String[] categoryNames = {
            "AGRIKULTUR",
            "KEAMANAN",
            "KEBERSIHAN",
            "KERAJINAN",
            "KONSTRUKSI",
            "LAYANAN",
            "MANUFAKTUR",
            "TRANSPORT",
    };

    private void selectCard(int index) {
        selectedCardIndex = index;
        selectedCategory = categoryNames[index];

        int[] cardIds = {
                R.id.cv_category1_modifyWorkExperienceCategoryPage,
                R.id.cv_category2_modifyWorkExperienceCategoryPage,
                R.id.cv_category3_modifyWorkExperienceCategoryPage,
                R.id.cv_category4_modifyWorkExperienceCategoryPage,
                R.id.cv_category5_modifyWorkExperienceCategoryPage,
                R.id.cv_category6_modifyWorkExperienceCategoryPage,
                R.id.cv_category7_modifyWorkExperienceCategoryPage,
                R.id.cv_category8_modifyWorkExperienceCategoryPage
        };

        int[] textIds = {
                R.id.tv_text1_modifyWorkExperienceCategoryPage,
                R.id.tv_text2_modifyWorkExperienceCategoryPage,
                R.id.tv_text3_modifyWorkExperienceCategoryPage,
                R.id.tv_text4_modifyWorkExperienceCategoryPage,
                R.id.tv_text5_modifyWorkExperienceCategoryPage,
                R.id.tv_text6_modifyWorkExperienceCategoryPage,
                R.id.tv_text7_modifyWorkExperienceCategoryPage,
                R.id.tv_text8_modifyWorkExperienceCategoryPage
        };

        for (int i = 0; i < cardIds.length; i++) {
            CardView card = findViewById(cardIds[i]);
            TextView cardText = card.findViewById(textIds[i]);

            card.setCardBackgroundColor(i == index ? selectedColor : defaultColor);

            if (i == index) {
                cardText.setTextColor(Color.WHITE);
            } else {
                cardText.setTextColor(Color.parseColor("#21458B"));
            }
        }
    }

}