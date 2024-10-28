package com.example.lokerin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class PelangganAddJobCategoryFragment extends Fragment {

    private CardView[] cards;
    private Button btnConfirmCategory;
    private final int selectedColor = Color.parseColor("#21458B");
    private final int defaultColor = Color.parseColor("#FFFFFF");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelanggan_addjob_category, container, false);
        CardView card1 = view.findViewById(R.id.card1);
        CardView card2 = view.findViewById(R.id.card2);
        CardView card3 = view.findViewById(R.id.card3);
        CardView card4 = view.findViewById(R.id.card4);
        CardView card5 = view.findViewById(R.id.card5);
        CardView card6 = view.findViewById(R.id.card6);
        cards = new CardView[]{card1, card2, card3, card4, card5, card6};

        for (CardView card : cards) {
            card.setOnClickListener(v -> selectCard(card));
        }

        return view;
    }

    private void selectCard(CardView selectedCard) {
        for (CardView card : cards) {
            card.setCardBackgroundColor(defaultColor);
        }
        selectedCard.setCardBackgroundColor(selectedColor);
    }
}