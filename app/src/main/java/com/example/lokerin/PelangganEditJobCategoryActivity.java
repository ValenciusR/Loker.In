package com.example.lokerin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class PelangganEditJobCategoryActivity extends AppCompatActivity {

    private CardView[] cards;
    private Button btnConfirmCategory;
    private final int selectedColor = Color.parseColor("#21458B");
    private final int defaultColor = Color.parseColor("#FFFFFF");
    private int selectedCardIndex = -1;
    private String selectedCategory = "";

    private String jobId, jobTitle, jobCategory, jobDescription, jobSalary, jobSalaryFrequent, jobProvince, jobRegency, jobAddress;

    private final String[] categoryNames = {
            "AGRIKULTUR",
            "KEAMANAN",
            "KEBERSIHAN",
            "KERAJINAN",
            "KONTRUKSI",
            "LAYANAN",
            "MANUFAKTUR",
            "TRANSPORT"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pelanggan_edit_job_category);

        initializeCards();
        retrieveIntentData();
        preSelectCard();

        btnConfirmCategory = findViewById(R.id.btn_category_confirm);
        btnConfirmCategory.setOnClickListener(v -> confirmCategorySelection());
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent(this, PelangganEditJobActivity.class);
        resultIntent.putExtra("JOB_ID", jobId);
        resultIntent.putExtra("jobTitle", jobTitle);
        resultIntent.putExtra("jobCategory", jobCategory);
        resultIntent.putExtra("jobDescription", jobDescription);
        resultIntent.putExtra("jobSalary", jobSalary);
        resultIntent.putExtra("jobSalaryFrequent", jobSalaryFrequent);
        resultIntent.putExtra("jobProvince", jobProvince);
        resultIntent.putExtra("jobRegency", jobRegency);
        resultIntent.putExtra("jobAddress", jobAddress);
        //Toast.makeText(this, jobTitle + " " + selectedCategory + " " + jobDescription + " " + jobSalary + " " + jobProvince + " " + jobAddress, Toast.LENGTH_SHORT).show();
        startActivity(resultIntent);
        finish();
        super.onBackPressed();
    }

    private void initializeCards() {
        cards = new CardView[]{
                findViewById(R.id.card1),
                findViewById(R.id.card2),
                findViewById(R.id.card3),
                findViewById(R.id.card4),
                findViewById(R.id.card5),
                findViewById(R.id.card6),
                findViewById(R.id.card7),
                findViewById(R.id.card8)
        };

        for (int i = 0; i < cards.length; i++) {
            int index = i;
            cards[i].setOnClickListener(v -> selectCard(index));
        }
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            jobId = intent.getStringExtra("jobId");
            jobTitle = intent.getStringExtra("jobTitle");
            jobCategory = intent.getStringExtra("jobCategory");
            jobDescription = intent.getStringExtra("jobDescription");
            jobSalary = intent.getStringExtra("jobSalary");
            jobSalaryFrequent = intent.getStringExtra("jobSalaryFrequent");
            jobProvince = intent.getStringExtra("jobProvince");
            jobRegency = intent.getStringExtra("jobRegency");
            jobAddress = intent.getStringExtra("jobAddress");
        }
    }

    private void preSelectCard() {
        if (jobCategory == null || jobCategory.isEmpty()) {
            return;
        }

        for (int i = 0; i < categoryNames.length; i++) {
            if (categoryNames[i].equalsIgnoreCase(jobCategory)) {
                selectCard(i);
                break;
            }
        }
    }

    private void selectCard(int index) {
        selectedCardIndex = index;
        selectedCategory = categoryNames[index];

        int[] cardIds = {
                R.id.card1, R.id.card2, R.id.card3, R.id.card4, R.id.card5, R.id.card6, R.id.card7, R.id.card8
        };

        int[] textIds = {
                R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.text5, R.id.text6, R.id.text7, R.id.text8
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

    private void confirmCategorySelection() {
        if (selectedCardIndex == -1) {
            Toast.makeText(this, "Pilih satu kategori pekerjaan!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent resultIntent = new Intent(this, PelangganEditJobActivity.class);
        resultIntent.putExtra("JOB_ID", jobId);
        resultIntent.putExtra("jobTitle", jobTitle);
        resultIntent.putExtra("jobCategory", selectedCategory);
        resultIntent.putExtra("jobDescription", jobDescription);
        resultIntent.putExtra("jobSalary", jobSalary);
        resultIntent.putExtra("jobSalaryFrequent", jobSalaryFrequent);
        resultIntent.putExtra("jobProvince", jobProvince);
        resultIntent.putExtra("jobRegency", jobRegency);
        resultIntent.putExtra("jobAddress", jobAddress);
        //Toast.makeText(this, jobTitle + " " + selectedCategory + " " + jobDescription + " " + jobSalary + " " + jobProvince + " " + jobAddress, Toast.LENGTH_SHORT).show();
        startActivity(resultIntent);
        finish();
    }
}
