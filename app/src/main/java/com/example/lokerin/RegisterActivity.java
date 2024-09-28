package com.example.lokerin;

import android.annotation.SuppressLint;
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

    EditText emailField, passwordField, confPasswordField;
    Button registerBtn;
    TextView loginLink;

    FirebaseApp firebaseApp;
    FirebaseAuth mAuth ;
    FirebaseDatabase firebaseDatabase ;
    DatabaseReference userReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        firebaseApp = FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");

        emailField = findViewById(R.id.input_email_registerPage);
        passwordField = findViewById(R.id.input_password_registerPage);
        confPasswordField = findViewById(R.id.input_confirmPassword_registerPage);
        registerBtn = findViewById(R.id.btn_register_registerPage);
        loginLink = findViewById(R.id.login_link_registerPage);


        loginLink.setOnClickListener(view -> {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        });

        registerBtn.setOnClickListener(view -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String passwordConf = confPasswordField.getText().toString();

            if (!email.contains("@") || !email.endsWith(".com")) {
                Toast.makeText(this, "Email must contain '@' and ends with '.com'", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty() || password.isEmpty() || passwordConf.isEmpty()) {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Password length must be more than 5 characters", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(passwordConf)) {
                Toast.makeText(this, "Password and Confirm Password does not match", Toast.LENGTH_SHORT).show();
            } else {

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Register Failed, email already exits", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Register Success, go Back to Login", Toast.LENGTH_SHORT).show();
                        userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
                        userReference.setValue(new User(email,password,"","","","",0,"","","","","",""));
                        Intent loginIntent = new Intent(this, CreateProfileActivity.class);
                        startActivity(loginIntent);
                        finish();

                    }
                });
            }

        });
    }
}