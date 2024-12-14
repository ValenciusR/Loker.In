package com.example.lokerin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class AdminDetailProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private TextView tvPageTitle, tvName, tvPhone, tvLocation, tvEmail, tvAge, tvGender, tvJob, tvJobDesc, tvType;
    private AppCompatButton btnDeleteUser;
    private ImageView btnBack, ivProfileNavbar;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail_profile);

        userId = getIntent().getStringExtra("USER_ID");
        if (userId == null) {
            Toast.makeText(this, "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference().child("users").child(userId);

        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvLocation = findViewById(R.id.tv_location);
        tvEmail = findViewById(R.id.tv_email);
        tvAge = findViewById(R.id.tv_age);
        tvGender = findViewById(R.id.tv_gender);
        tvJob = findViewById(R.id.tv_job);
        tvJobDesc = findViewById(R.id.tv_job_desc);
        tvType = findViewById(R.id.tv_type);

        btnDeleteUser = findViewById(R.id.btn_delete);

        btnBack = findViewById(R.id.btn_back_toolbar);
        tvPageTitle = findViewById(R.id.tv_page_toolbar);
        ivProfileNavbar = findViewById(R.id.btn_profile_toolbar);
        btnBack.setOnClickListener(v -> backPage());
        tvPageTitle.setText("Detail Pengguna");
        ivProfileNavbar.setVisibility(View.GONE);

        btnDeleteUser.setOnClickListener(v -> showDeleteConfirmationDialog());

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
                    Toast.makeText(AdminDetailProfileActivity.this, "Data pengguna tidak ditemukan.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AdminDetailProfileActivity.this, "Error mengambil data pengguna.", Toast.LENGTH_SHORT).show();
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

        String type = task.getResult().child("type").getValue(String.class);
        tvType.setText(type != null && !type.isEmpty() ? type : "N/A");
    }

    private void showDeleteConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Hapus User?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            if (userId != null) {
                databaseReference.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AdminDetailProfileActivity.this, "Data pengguna berhasil dihapus.", Toast.LENGTH_SHORT).show();
                            navigateToAdminMainActivity();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AdminDetailProfileActivity.this, "Gagal menghapus data pengguna.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(AdminDetailProfileActivity.this, "User ID pengguna tidak valid!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void navigateToAdminMainActivity() {
        Intent intent = new Intent(AdminDetailProfileActivity.this, AdminMainActivity.class);
        intent.putExtra("targetFragment", "AdminUsersFragment");
        startActivity(intent);
        finish();
    }

    private void backPage() {
        navigateToAdminMainActivity();
    }
}
