package com.example.lokerin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEmail, etPassword;
    private TextView registerLink, forgotPassLink, emailError, passwordError, loginError;
    private FirebaseApp firebaseApp;

    private FrameLayout loadingView;
    private LinearLayout linearView;

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.input_email_loginPage);
        etPassword = findViewById(R.id.input_password_loginPage);
        btnLogin = findViewById(R.id.btn_login_loginPage);
        registerLink = findViewById(R.id.register_link_loginPage);
        forgotPassLink = findViewById(R.id.forgotPassword_link_loginPage);
        emailError = findViewById(R.id.tv_emailError_loginPage);
        passwordError = findViewById(R.id.tv_passwordError_loginPage);
        loginError = findViewById(R.id.tv_loginError_loginPage);
        loadingView = findViewById(R.id.loading_overlay);
        linearView = findViewById(R.id.linearLayout_loginPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        
        //check if user is null
        if(firebaseUser != null){
            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
            Log.d("TAG", "onCreate: " + mAuth.getUid());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String userType = user.getType();
                    if(userType.equals("pelanggan")) {
                        if(!user.getName().isEmpty()){
                            startActivity(new Intent(LoginActivity.this, PelangganMainActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(LoginActivity.this, CreateProfile_PersonalInfo.class));
                            finish();
                        }
                    } else if (userType.equals("pekerja")){
                        if(!user.getName().isEmpty()){
                            startActivity(new Intent(LoginActivity.this, PekerjaMainActivity.class));
                            finish();
                        }else{
                            startActivity(new Intent(LoginActivity.this, CreateProfile_PersonalInfo.class));
                            finish();
                        }
                    } else if (userType.equals("admin")){
                        startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                        finish();
                    } else{
                        startActivity(new Intent(LoginActivity.this, CreateProfile.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        registerLink.setOnClickListener(view -> {
            Intent registerIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerIntent);
            finish();
        });

        forgotPassLink.setOnClickListener(view -> {
            Intent forgetIntent = new Intent(this, ForgetPasswordActivity.class);
            startActivity(forgetIntent);
            finish();
        });

        btnLogin.setOnClickListener(view -> {
            Boolean isValid = true;
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            emailError.setText("");
            passwordError.setText("");
            loginError.setText("");
            etEmail.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            etPassword.setBackgroundResource(R.drawable.shape_rounded_blue_border);
            if (email.isEmpty()) {
                emailError.setText("Email harus diisi");
                etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailError.setText("Email harus mengandung '@' dan diakhiri '.com'");
                etEmail.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }
            if (password.isEmpty()) {
                passwordError.setText("Password harus diisi");
                etPassword.setBackgroundResource(R.drawable.shape_rounded_red_border);
                isValid = false;
            }
            if(isValid) {
                linearView.setClickable(false);
                loadingView.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    loadingView.setVisibility(View.GONE);
                    linearView.setClickable(true);
                    if(!task.isSuccessful()){
                        Exception exception = task.getException();
                        if (exception != null) {
                            String message;
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Email atau kata sandi tidak valid!";
                            } else if (exception instanceof FirebaseAuthInvalidUserException) {
                                message = "Pengguna tidak ditemukan atau akun dinonaktifkan!";
                            } else if (exception instanceof FirebaseNetworkException) {
                                message = "Koneksi internet bermasalah!";
                            } else {
                                message = "Terjadi kesalahan, coba lagi nanti!";
                            }
                            loginError.setText(message);
                        }
                    }else{
                        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
                        userReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                String userType = user.getType();
                                if(userType.equals("pelanggan")) {
                                    if(!user.getName().isEmpty()){
                                        startActivity(new Intent(LoginActivity.this, PelangganMainActivity.class));
                                        finish();
                                    }else{
                                        startActivity(new Intent(LoginActivity.this, CreateProfile_PersonalInfo.class));
                                        finish();
                                    }
                                } else if (userType.equals("pekerja")) {
                                    if(!user.getName().isEmpty()){
                                        startActivity(new Intent(LoginActivity.this, PekerjaMainActivity.class));
                                        finish();
                                    }else{
                                        startActivity(new Intent(LoginActivity.this, CreateProfile_PersonalInfo.class));
                                        finish();
                                    }
                                } else if (userType.equals("admin")) {
                                    startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                    finish();
                                }else{
                                    startActivity(new Intent(LoginActivity.this, CreateProfile.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }

        });

    }
}