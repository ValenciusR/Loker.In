package com.example.lokerin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;

    private EditText etEmail, etPassword, etConfPassword;
    private Button btnRegister;
    private TextView loginLink;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        etEmail = findViewById(R.id.input_email_registerPage);
        etPassword = findViewById(R.id.input_password_registerPage);
        etConfPassword = findViewById(R.id.input_confirmPassword_registerPage);
        btnRegister = findViewById(R.id.btn_register_registerPage);
        loginLink = findViewById(R.id.login_link_registerPage);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setCancelable(false);

        loginLink.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

        btnRegister.setOnClickListener(view -> {
            progressDialog.show();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String passwordConf = etConfPassword.getText().toString();

            if (!email.contains("@") || !email.endsWith(".com")) {
                Toast.makeText(this, "Email harus mengandung '@' dan diakhiri '.com'", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (email.isEmpty() || password.isEmpty() || passwordConf.isEmpty()) {
                Toast.makeText(this, "Semua form harus diisi", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Kata sandi minimal berisi 6 huruf", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else if (!password.equals(passwordConf)) {
                Toast.makeText(this, "Kata sandi tidak sesuai", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            } else {

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Pendaftaran gagal, email sudah digunakan", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    } else {

                        Toast.makeText(this, "Pendaftaran Berhasil", Toast.LENGTH_SHORT).show();
                        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
                        userReference.setValue(new User(mAuth.getCurrentUser().getUid(),email,password,"","","","",0,"","","","","",""));
                        progressDialog.dismiss();
                        Intent loginIntent = new Intent(this, CreateProfile.class);
                        startActivity(loginIntent);
                        finish();
                    }
                });
            }
        });
    }
}