package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private Button btnLogOut;
    private TextView tvPengguna, tvPekerja, tvPelanggan, tvPekerjaan, tvPekerjaanAktif;

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

        tvPengguna.setText("");
        tvPekerja.setText("");
        tvPelanggan.setText("");
        tvPekerjaan.setText("");
        tvPekerjaanAktif.setText("");


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }
}
