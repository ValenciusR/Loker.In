package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private TextView tvPageTitle, tvName, tvPhone, tvLocation, tvEmail, tvAge, tvGender, tvJob, tvJobDesc;
    private AppCompatButton btnDeleteUser;
    private ImageView btnBack, ivProfileNavbar;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile);

        userId = getIntent().getStringExtra("USER_ID");

        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvLocation = findViewById(R.id.tv_location);
        tvEmail = findViewById(R.id.tv_email);
        tvAge = findViewById(R.id.tv_age);
        tvGender = findViewById(R.id.tv_gender);
        tvJob = findViewById(R.id.tv_job);
        tvJobDesc = findViewById(R.id.tv_job_desc);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        btnBack.setOnClickListener(v -> backPage());
        tvPageTitle.setText(userId);
        ivProfileNavbar.setVisibility(View.GONE);

        btnDeleteUser = findViewById(R.id.btn_delete);
        btnDeleteUser.setOnClickListener(v -> deleteUserData());

        if (userId == null) {
            Toast.makeText(this, "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("users").child(userId);

        getUserData();
    }

    private void getUserData() {
        if (userId == null) {
            return;
        }

        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    updateUserProfile(task);
                } else {
                    Log.d("DetailProfile", "Data does not exist in Firebase.");
                    Toast.makeText(DetailProfileActivity.this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("DetailProfile", "Error fetching data: " + task.getException());
                Toast.makeText(DetailProfileActivity.this, "Error mengambil data pengguna.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile(Task<DataSnapshot> task) {
        String name = task.getResult().child("name").getValue(String.class);
        tvName.setText(name != null && !name.isEmpty() ? name : "N/A");

        String phone = task.getResult().child("phoneNumber").getValue(String.class);
        tvPhone.setText(phone != null && !phone.isEmpty() ? phone : "N/A");

        String location = task.getResult().child("location").getValue(String.class);
        tvLocation.setText(location != null && !location.isEmpty() ? location : "N/A");

        String email = task.getResult().child("email").getValue(String.class);
        tvEmail.setText(email != null && !email.isEmpty() ? email : "N/A");

        Integer age = task.getResult().child("age").getValue(Integer.class);
        tvAge.setText(age != null && age != 0 ? String.valueOf(age) : "N/A");

        String gender = task.getResult().child("gender").getValue(String.class);
        tvGender.setText(gender != null && !gender.isEmpty() ? gender : "N/A");

        String job = task.getResult().child("job").getValue(String.class);
        tvJob.setText(job != null && !job.isEmpty() ? job : "N/A");

        String jobDesc = task.getResult().child("jobDesc").getValue(String.class);
        tvJobDesc.setText(jobDesc != null && !jobDesc.isEmpty() ? jobDesc : "N/A");
    }

    private void deleteUserData() {
        if (userId != null) {
            databaseReference.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(DetailProfileActivity.this, "Data pengguna berhasil dihapus.", Toast.LENGTH_SHORT).show();
                        navigateToAdminMainActivity();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(DetailProfileActivity.this, "Gagal menghapus data pengguna.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(DetailProfileActivity.this, "User ID pengguna tidak valid!", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToAdminMainActivity() {
        Intent intent = new Intent(DetailProfileActivity.this, AdminMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void backPage() {
        navigateToAdminMainActivity();
    }
}
