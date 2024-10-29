package com.example.lokerin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
                view.findViewById(R.id.card6)
        };

        for (int i = 0; i < cards.length; i++) {
            int index = i;
            cards[i].setOnClickListener(v -> selectCard(index));
        }
    }

    private String[] categoryNames = {
            "Pembantu Rumah Tangga",
            "Tukang Kebun",
            "Tukang Bangunan",
            "Kuli",
            "Penjual",
            "Buruh Cuci",
    };

    private void selectCard(int index) {
        selectedCardIndex = index;
        selectedCategory = categoryNames[index];
        for (int i = 0; i < cards.length; i++) {
            cards[i].setCardBackgroundColor(i == index ? selectedColor : defaultColor);
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
