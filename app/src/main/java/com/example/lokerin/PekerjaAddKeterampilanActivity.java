package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PekerjaAddKeterampilanActivity extends AppCompatActivity {

//    Navbar
    private ImageView btnBack, ivProfilePicture;
    private TextView tvPageTitle;

    private EditText etSearch;
    private RecyclerView rvKeterampilanList;
    private AppCompatButton acbTambahKeterampilan;

    private ArrayList<Skill> skills, dbSkills;
    private LinearLayoutManager linearLayoutManager;
    private ListSkillAdapter listSkillAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pekerja_add_keterampilan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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
                startActivity(new Intent(PekerjaAddKeterampilanActivity.this, PekerjaProfileActivity.class));
                finish();
            }
        });
        tvPageTitle.setText("Edit Keterampilan");
        ivProfilePicture.setImageResource(R.drawable.settings_icon);

        etSearch = findViewById(R.id.et_searchBar_addKeterampilanPage);
        rvKeterampilanList = findViewById(R.id.rv_keterampilanList_addKeterampilanPage);
        acbTambahKeterampilan = findViewById(R.id.acb_tambahKeterampilan_addKeterampilanPage);

        Skill templatePortofolioJob = new Skill("Skill A");
        skills = new ArrayList<>();
        skills.add(templatePortofolioJob);

        linearLayoutManager = new LinearLayoutManager(PekerjaAddKeterampilanActivity.this, LinearLayoutManager.VERTICAL, false);
        listSkillAdapter = new ListSkillAdapter(PekerjaAddKeterampilanActivity.this, skills);
        rvKeterampilanList.setLayoutManager(linearLayoutManager);
        rvKeterampilanList.setAdapter(listSkillAdapter);

//        Get all Skills from DB
        acbTambahKeterampilan.setOnClickListener(v -> {
            showDeleteSkillConfirmationDialog();
        });
    }

    private void showDeleteSkillConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_add_skill_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        dbSkills = new ArrayList<>();
        dbSkills.add(new Skill("Skill A"));
        dbSkills.add(new Skill("Skill B"));
        dbSkills.add(new Skill("Skill C"));
        dbSkills.add(new Skill("Skill D"));
        dbSkills.add(new Skill("Skill E"));
        dbSkills.add(new Skill("Skill F"));
        TextView title = dialog.findViewById(R.id.tv_title_addSkillPopUp);
        AutoCompleteTextView searchSkill = dialog.findViewById(R.id.actv_search_addSkillPopup);
        ArrayList<String> userSkills = new ArrayList<>();
        for (int i = 0; i < skills.size(); i++) {
            userSkills.add(skills.get(i).getName());
        }

        ArrayList<String> skillItems = new ArrayList<>();
        for (int i = 0; i < dbSkills.size(); i++) {
            skillItems.add(dbSkills.get(i).getName());
        }
//        Remove duplicate skills
        skillItems.removeAll(userSkills);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, skillItems);
        searchSkill.setAdapter(arrayAdapter);
        searchSkill.setOnClickListener(v -> {
            searchSkill.showDropDown();
        });
        searchSkill.setOnFocusChangeListener((v, hasFocus) -> searchSkill.showDropDown());

        Button btnCancel = dialog.findViewById(R.id.acb_cancel_addSkillPopUp);
        Button btnConfirm = dialog.findViewById(R.id.acb_confirm_addSkillPopUp);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
//            Delete Skill from DB & user
            String result = searchSkill.getText().toString();
            if(skillItems.contains(result)) {
                this.skills.add(new Skill(searchSkill.getText().toString()));
                this.listSkillAdapter = new ListSkillAdapter(PekerjaAddKeterampilanActivity.this, this.skills);
                this.rvKeterampilanList.setAdapter(listSkillAdapter);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}