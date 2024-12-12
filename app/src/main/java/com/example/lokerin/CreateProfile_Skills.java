package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateProfile_Skills extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private ImageView btnBack;
    private AppCompatButton btnNext;
    private TextView tvSkip;

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
        setContentView(R.layout.activity_create_profile_skills);

        btnBack = findViewById(R.id.iv_back_skillsPage);
        btnNext = findViewById(R.id.acb_next_skillsPage);
        tvSkip = findViewById(R.id.tv_skip_skillsPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        btnBack.setOnClickListener(view -> {
            startActivity(new Intent(this, CreateProfile_PersonalInfo.class));
            finish();
        });

        btnNext.setOnClickListener(view -> {

        });

        tvSkip.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, CreateProfile_WorkExperience.class);
            startActivity(loginIntent);
            finish();
        });

        etSearch = findViewById(R.id.et_searchBar_addKeterampilanPage);
        rvKeterampilanList = findViewById(R.id.rv_keterampilanList_addKeterampilanPage);
        acbTambahKeterampilan = findViewById(R.id.acb_tambahKeterampilan_addKeterampilanPage);

        Skill templatePortofolioJob = new Skill("Skill A");
        skills = new ArrayList<>();
        skills.add(templatePortofolioJob);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listSkillAdapter = new ListSkillAdapter(this, skills);
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
                this.listSkillAdapter = new ListSkillAdapter(this, this.skills);
                this.rvKeterampilanList.setAdapter(listSkillAdapter);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}