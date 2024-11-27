package com.example.lokerin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    FirebaseAuth mAuth;
    EditText emailField, passwordField;
    TextView registerLink, forgotPassLink;
    FirebaseApp firebaseApp;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.input_email_loginPage);
        passwordField = findViewById(R.id.input_password_loginPage);
        loginBtn = findViewById(R.id.btn_login_loginPage);
        registerLink = findViewById(R.id.register_link_loginPage);
        forgotPassLink = findViewById(R.id.forgotPassword_link_loginPage);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        registerLink.setOnClickListener(view -> {
            Intent registerIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerIntent);
            finish();
        });

        forgotPassLink.setOnClickListener(view -> {
//            Intent forgetIntent = new Intent(this, ForgotPassword.class);
//            startActivity(forgetIntent);
//            finish();
        });

        loginBtn.setOnClickListener(view -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(this, "Login Failed, Email doesn't exist", Toast.LENGTH_SHORT).show();
                    Log.e("Signup Error", "onCancelled", task.getException());
                }else{
//                    startActivity(new Intent(LoginActivity.this, PelangganMainActivity.class));
                    startActivity(new Intent(LoginActivity.this, PekerjaMainActivity.class));
                    finish();
                }
            });

        });

    }
}