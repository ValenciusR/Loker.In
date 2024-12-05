package com.example.lokerin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private RecyclerView rvUsers;
    private EditText etSearchBar;

    private User[] jobApplicants;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        //Dummy data
        jobApplicants = getJobUsersList().toArray(new User[0]);

        rvUsers = view.findViewById(R.id.recyclerView);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        ListUserAdapter adapter = new ListUserAdapter(Arrays.asList(jobApplicants), "ADMIN"); //Ini nanti diganti sesuai tombolnya pengen jadi apa
        rvUsers.setAdapter(adapter);

//        etSearchBar = view.findViewById(R.id.search_bar);
//        etSearchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.getFilter().filter(s);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });

        return view;
    }

    private List<User> getJobUsersList() {
        List<User> applicants = new ArrayList<>();
        applicants.add(new User("bangjago@gmail.com", "BangJago"));
        applicants.add(new User("jagoan@gmail.com", "Jagoan"));
        applicants.add(new User("jagoan@gmail.com", "Jagoan"));
        applicants.add(new User("jagoan@gmail.com", "Jagoan"));
        applicants.add(new User("jagoan@gmail.com", "Jagoan"));
        applicants.add(new User("jagoan@gmail.com", "Jagoan"));
        applicants.add(new User("jagoan@gmail.com", "Jagoan"));

        return applicants;
    }
}
