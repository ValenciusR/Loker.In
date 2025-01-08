package com.example.lokerin;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;

    private RecyclerView rvUsers;
    private EditText etSearchBar;

    private TextView tvEmpthyRv;

    private List<User> userList = new ArrayList<>();
    private ListUserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_users, container, false);

        FirebaseApp.initializeApp(requireContext());
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        usersRef = firebaseDatabase.getReference("users");

        tvEmpthyRv = view.findViewById(R.id.tv_empthyRv);

        rvUsers = view.findViewById(R.id.recyclerView);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ListUserAdapter(userList, "");
        rvUsers.setAdapter(adapter);

        etSearchBar = view.findViewById(R.id.search_bar);
        etSearchBar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        fetchUsersFromFirebase();

        return view;
    }

    private void filterUsers(String query) {
        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getName() != null && user.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }

        if(filteredList.isEmpty()){
            tvEmpthyRv.setVisibility(View.VISIBLE);
        }else{
            tvEmpthyRv.setVisibility(View.GONE);
        }
        adapter.updateList(filteredList);
    }

    private void fetchUsersFromFirebase() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setId(userSnapshot.getKey());
                        userList.add(user);
                    }
                }

                if(userList.isEmpty()){
                    tvEmpthyRv.setVisibility(View.VISIBLE);
                }else{
                    tvEmpthyRv.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminUsersFragment", "Failed to fetch users: " + error.getMessage());
            }
        });
    }
}
