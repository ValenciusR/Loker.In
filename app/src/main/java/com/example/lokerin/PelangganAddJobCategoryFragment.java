package com.example.lokerin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PelangganAddJobCategoryFragment extends Fragment {

    private CardView[] cards;
    private Button btnConfirmCategory;
    private final int selectedColor = Color.parseColor("#21458B");
    private final int defaultColor = Color.parseColor("#FFFFFF");
    private int selectedCardIndex = -1;
    private String selectedCategory = "";

    private String jobTitle, description, salary, frequentSalary, selectedProvince, selectedRegency, address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pelanggan_addjob_category, container, false);
        initializeCards(view);

        Bundle args = getArguments();
        if (args != null) {
            jobTitle = args.getString("jobTitle", "No Title");
            description = args.getString("description", "No Description");
            salary = args.getString("salary", "No Salary");
            frequentSalary = args.getString("salaryFrequent", "No Salary");
            selectedCategory = args.getString("selectedCategory", "");
            selectedProvince = args.getString("selectedProvince", "");
            selectedRegency = args.getString("selectedRegency", "");
            address = args.getString("address", "No Address");
            //Toast.makeText(getContext(), jobTitle + " " + description + " " + salary + " " + frequentSalary + " " + selectedCategory + " " + selectedProvince + " " + selectedRegency + " "  + address, Toast.LENGTH_SHORT).show();
        }

        btnConfirmCategory = view.findViewById(R.id.btn_category_confirm);
        btnConfirmCategory.setOnClickListener(v -> confirmCategorySelection());
        return view;
    }

    private void initializeCards(View view) {
        cards = new CardView[]{
                view.findViewById(R.id.card1),
                view.findViewById(R.id.card2),
                view.findViewById(R.id.card3),
                view.findViewById(R.id.card4),
                view.findViewById(R.id.card5),
                view.findViewById(R.id.card6),
                view.findViewById(R.id.card7)
        };

        for (int i = 0; i < cards.length; i++) {
            int index = i;
            cards[i].setOnClickListener(v -> selectCard(index));
        }
    }

    private String[] categoryNames = {
            "Barber",
            "Builder",
            "Driver",
            "Gardener",
            "Maid",
            "Peddler",
            "Porter",
            "Secretary",
    };

    private void selectCard(int index) {
        selectedCardIndex = index;
        selectedCategory = categoryNames[index];

        int[] cardIds = {
                R.id.card1, R.id.card2, R.id.card3, R.id.card4, R.id.card5, R.id.card6, R.id.card7
        };

        int[] textIds = {
                R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.text5, R.id.text6, R.id.text7
        };

        for (int i = 0; i < cardIds.length; i++) {
            CardView card = getView().findViewById(cardIds[i]);
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
            Toast.makeText(getContext(), "Please select a category first", Toast.LENGTH_SHORT).show();
        } else {
            Bundle result = new Bundle();
            result.putString("selectedCategory", selectedCategory);
            result.putString("selectedProvince", selectedProvince);
            result.putString("selectedRegency", selectedRegency);
            getParentFragmentManager().setFragmentResult("requestKey", result);
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
