package com.example.lokerin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText etEmail, etPassword;
    private TextView registerLink, forgotPassLink, emailError, passwordError, loginError;
    private FirebaseApp firebaseApp;

    private FrameLayout loadingView;
    private LinearLayout linearView, btnGoogle;

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;

    GoogleSignInClient googleSignInClient;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                linearView.setClickable(false);
                loadingView.setVisibility(View.VISIBLE);

                if (result.getResultCode() == RESULT_OK) {
                    Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);

                        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                        mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                loadingView.setVisibility(View.GONE);
                                linearView.setClickable(true);

                                if (task.isSuccessful()) {
                                    mAuth = FirebaseAuth.getInstance();
                                    String userId = mAuth.getUid();
                                    userReference = firebaseDatabase.getReference().child("users").child(userId);

                                    // Check if the user exists
                                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                // User exists, proceed based on user type
                                                User user = dataSnapshot.getValue(User.class);
                                                if (user != null) {
                                                    handleUserType(user);
                                                }
                                            } else {
                                                HashMap<String, Object> hashMap = getObjectHashMap(signInAccount.getEmail(), signInAccount.getPhotoUrl().toString(), signInAccount.getDisplayName());
                                                userReference.setValue(hashMap);
                                                userReference.updateChildren(hashMap);

                                                // Navigate to CreateProfile activity
                                                Intent loginIntent = new Intent(LoginActivity.this, CreateProfile.class);
                                                startActivity(loginIntent);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            loadingView.setVisibility(View.GONE);
                                            linearView.setClickable(true);
                                            Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } catch (ApiException e) {
                        loadingView.setVisibility(View.GONE);
                        linearView.setClickable(true);
                        Toast.makeText(LoginActivity.this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where the activity is canceled
                    linearView.setClickable(true);
                    loadingView.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Sign-In canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    );

    private void handleUserType(User user) {
        String userType = user.getType();
        if ("pelanggan".equals(userType)) {
            if (!user.getName().isEmpty()) {
                startActivity(new Intent(LoginActivity.this, PelangganMainActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, CreateProfile_PersonalInfo.class));
            }
        } else if ("pekerja".equals(userType)) {
            if (!user.getName().isEmpty()) {
                startActivity(new Intent(LoginActivity.this, PekerjaMainActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, CreateProfile_PersonalInfo.class));
            }
        } else if ("admin".equals(userType)) {
            startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, CreateProfile.class));
        }
        finish();
    }

    private @NonNull HashMap<String, Object> getObjectHashMap(String email, String imgUri, String name) {
        ArrayList<String> arraySkill = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", mAuth.getCurrentUser().getUid());
        hashMap.put("email", email);
        hashMap.put("password", "");
        hashMap.put("type", "");
        hashMap.put("name", name);
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
        hashMap.put("imageUrl",imgUri);
        return hashMap;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String userType = sharedPreferences.getString("userType", "");
            if (userType.equals("pelanggan")) {
                startActivity(new Intent(this, PelangganMainActivity.class));
            } else if (userType.equals("pekerja")) {
                startActivity(new Intent(this, PekerjaMainActivity.class));
            } else if (userType.equals("admin")) {
                startActivity(new Intent(this, AdminMainActivity.class));
            } else {
                startActivity(new Intent(this, CreateProfile.class));
            }
            finish();
        }

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
        btnGoogle = findViewById(R.id.btn_loginGoogle_loginPage);

        firebaseApp = FirebaseApp.initializeApp(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, options);

        mAuth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });
        //check if user is null
        if(firebaseUser != null){
            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user!=null){
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
                                if(user != null){
                                    String userType = user.getType();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.putString("userType", userType);
                                    editor.putString("userName", user.getName());
                                    editor.putString("userEmail", user.getEmail());
                                    editor.apply();

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