package com.lokerin.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference, jobsReference;

    private CardView btnLogOut;
    private TextView tvPengguna, tvPekerja, tvPelanggan, tvPekerjaan, tvPekerjaanAktif;
    private int penggunaCounter, pekerjaCounter, pelangganCounter, pekerjaanCounter, pekerjaanOpenCounter, pekerjaanOnGoingCounter;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        tvPengguna = view.findViewById(R.id.tv_pengguna);
        tvPekerja = view.findViewById(R.id.tv_pekerja);
        tvPelanggan = view.findViewById(R.id.tv_pelanggan);
        tvPekerjaan = view.findViewById(R.id.tv_pekerjaan);
        tvPekerjaanAktif = view.findViewById(R.id.tv_pekerjaan_aktif);
        btnLogOut = view.findViewById(R.id.btn_logout);

        penggunaCounter = 0;
        pekerjaCounter = 0;
        pelangganCounter = 0;
        pekerjaanCounter = 0;
        pekerjaanOpenCounter = 0;
        pekerjaanOnGoingCounter = 0;

        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        userReference = firebaseDatabase.getReference().child("users");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        penggunaCounter++;
                        if(user.getType().equalsIgnoreCase("pekerja")){
                            pekerjaCounter++;
                        } else if(user.getType().equalsIgnoreCase("pelanggan")){
                            pelangganCounter++;
                        }
                    }
                }

                tvPengguna.setText(String.valueOf(penggunaCounter));
                tvPekerja.setText(String.valueOf(pekerjaCounter));
                tvPelanggan.setText(String.valueOf(pelangganCounter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminUsersFragment", "Failed to fetch users: " + error.getMessage());
            }
        });

        jobsReference = firebaseDatabase.getReference().child("jobs");
        jobsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Job job = jobSnapshot.getValue(Job.class);
                    if (job != null) {
                        pekerjaanCounter++;
                        if(job.getJobStatus().equalsIgnoreCase("OPEN")){
                            pekerjaanOpenCounter++;
                        } else if(job.getJobStatus().equalsIgnoreCase("ON GOING")){
                            pekerjaanOnGoingCounter++;
                        }
                    }
                }

                tvPekerjaan.setText(String.valueOf(pekerjaanCounter));
                tvPekerjaanAktif.setText(String.valueOf(pekerjaanOpenCounter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminUsersFragment", "Failed to fetch users: " + error.getMessage());
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
                GoogleSignInClient googleSignIn = GoogleSignIn.getClient(getActivity(), options);

                googleSignIn.revokeAccess()
                        .addOnCompleteListener(task -> {
                            sharedPreferences = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();

                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();
                        });
            }
        });

        return view;
    }
}
