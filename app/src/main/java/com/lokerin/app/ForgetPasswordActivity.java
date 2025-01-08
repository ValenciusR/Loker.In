package com.lokerin.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnReset;
    TextView tvBack, tvEmailError;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        etEmail = findViewById(R.id.et_email_forgetPasswordPage);
        btnReset = findViewById(R.id.btn_reset_forgetPasswordPage);
        tvBack = findViewById(R.id.tv_back_forgetPasswordPage);
        tvEmailError = findViewById(R.id.tv_emailError_forgetPasswordPage);

        firebaseAuth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            tvEmailError.setVisibility(View.GONE);

            if(email.equals("")){
                tvEmailError.setVisibility(View.VISIBLE);
                tvEmailError.setText("Email Harus Diisi");
            }else{
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgetPasswordActivity.this, "Silahkan Cek Email Anda (Jika Email yang dimasukan benar)", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                        }else{
                            String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                            String errorMessage = translateFirebaseError(errorCode);

                            tvEmailError.setVisibility(View.VISIBLE);
                            tvEmailError.setText(errorMessage);;
                        }
                    }
                });
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(back);
                finish();
            }
        });
    }

    private String translateFirebaseError(String errorCode) {
        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                return "Email yang Anda masukkan tidak valid.";
            case "ERROR_USER_NOT_FOUND":
                return "Pengguna dengan email tersebut tidak ditemukan.";
            case "ERROR_TOO_MANY_REQUESTS":
                return "Terlalu banyak permintaan. Silakan coba lagi nanti.";
            default:
                return "Terjadi kesalahan. Silakan coba lagi.";
        }
    }
}