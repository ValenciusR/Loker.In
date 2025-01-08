package com.lokerin.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;

    private EditText etEmail, etPassword, etConfPassword;
    private Button btnRegister;
    private TextView loginLink, errorEmail, errorPassword, errorConfPassword, errorRegister;

    private FrameLayout loadingView;
    private LinearLayout linearView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.input_email_registerPage);
        etPassword = findViewById(R.id.input_password_registerPage);
        etConfPassword = findViewById(R.id.input_confirmPassword_registerPage);
        btnRegister = findViewById(R.id.btn_register_registerPage);
        loginLink = findViewById(R.id.login_link_registerPage);
        errorEmail = findViewById(R.id.tv_emailError_registerPage);
        errorPassword = findViewById(R.id.tv_passwordError_registerPage);
        errorConfPassword = findViewById(R.id.tv_passwordConfError_registerPage);
        errorRegister = findViewById(R.id.tv_registerError_registerPage);

        linearView = findViewById(R.id.linearLayout_registerPage);
        loadingView = findViewById(R.id.loading_overlay_registerPage);

        loginLink.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

        btnRegister.setOnClickListener(view -> {

            Boolean isValid = true;
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String passwordConf = etConfPassword.getText().toString();
            errorRegister.setTextColor(Color.parseColor("#F31919"));

            errorRegister.setText("");
            errorConfPassword.setText("");
            errorPassword.setText("");
            errorEmail.setText("");

            etEmail.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            etPassword.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            etConfPassword.setBackgroundResource(R.drawable.shape_rounded_blue_border);

            if (email.isEmpty()) {
                errorEmail.setText("Email harus diisi");
                etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                errorEmail.setText("Email harus mengandung '@' dan diakhiri '.com'");
                etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }

            if (password.isEmpty()) {
                errorPassword.setText("Kata Sandi harus diisi");
                etPassword.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }

            if (password.length() < 6) {
                errorPassword.setText("Kata sandi minimal berisi 6 huruf");
                etPassword.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }

            if (passwordConf.isEmpty()) {
                errorConfPassword.setText("Konfirmasi Kata Sandi harus diisi");
                etConfPassword.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }

            if (!password.equals(passwordConf)) {
                errorConfPassword.setText("Kata sandi tidak sesuai");
                etConfPassword.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }

            if (isValid) {
                loadingView.setVisibility(View.VISIBLE);
                linearView.setClickable(false);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        if (task.getException() instanceof FirebaseAuthException) {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            String errorCode = e.getErrorCode();
                            String message;
                            switch (errorCode) {
                                case "ERROR_EMAIL_ALREADY_IN_USE":
                                    message = "Email sudah terdaftar, gunakan email lain";
                                    break;
                                case "ERROR_INVALID_EMAIL":
                                    message = "Format email tidak valid";
                                    break;
                                case "ERROR_NETWORK_REQUEST_FAILED":
                                    message = "Koneksi internet bermasalah!";
                                    break;
                                case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                    message = "Akun sudah ada dengan metode login berbeda";
                                    break; // Missing break here for this case
                                default:
                                    message = "Registrasi gagal: " + e.getMessage();
                                    break;
                            }
                            errorRegister.setText(message);
                        } else {
                            // Handle other types of exceptions
                            errorRegister.setText("Terjadi kesalahan: " + task.getException().getMessage());
                        }
                        loadingView.setVisibility(View.GONE);
                        linearView.setClickable(true);
                    } else {
                        // Successfully registered, continue with database logic
                        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
                        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());

                        HashMap<String, Object> hashMap = getObjectHashMap(email, password);
                        userReference.setValue(hashMap);
                        userReference.updateChildren(hashMap);

                        // Navigate to CreateProfile activity
                        Intent loginIntent = new Intent(this, CreateProfile.class);
                        startActivity(loginIntent);
                        finish();
                    }
                });
            }
        });
    };

    private @NonNull HashMap<String, Object> getObjectHashMap(String email, String password) {
        ArrayList<String> arraySkill = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", mAuth.getCurrentUser().getUid());
        hashMap.put("email", email);
        hashMap.put("password", password);
        hashMap.put("type", "");
        hashMap.put("name", "");
        hashMap.put("nameLowerCase", "");
        hashMap.put("phoneNumber","");
        hashMap.put("location","");
        hashMap.put("age", 0);
        hashMap.put("gender", "");
        hashMap.put("aboutMe","");
        hashMap.put("skill", arraySkill);
        hashMap.put("skillDesc", "");
        hashMap.put("job", "");
        hashMap.put("jobDesc","");
        hashMap.put("imageUrl","default");
        return hashMap;
    }
}