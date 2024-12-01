package com.example.lokerin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ImageView ivProfileImage, btnBack;
    TextView tvUsername;


    FirebaseUser fuser;

    Intent intent;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersReference, chatsReference;

    ImageButton btnSend;
    EditText etMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ivProfileImage = findViewById(R.id.imageView_profile_icon);
        tvUsername = findViewById(R.id.tv_page_toolbar);
        btnSend = findViewById(R.id.btn_send);
        etMessage = findViewById(R.id.et_message);

        btnBack = findViewById(R.id.btn_back_toolbar);
        btnBack.setOnClickListener(view -> {
            finish();
        });

        intent = getIntent();
        String userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        usersReference = firebaseDatabase.getReference().child("users").child(userid);
        chatsReference = firebaseDatabase.getReference().child("chats");


        Log.d("userID", userid);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                tvUsername.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSend.setOnClickListener(view -> {
            String msg = etMessage.getText().toString();
            if(!msg.equals("")){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender",fuser.getUid());
                hashMap.put("receiver",userid);
                hashMap.put("message",msg);

                chatsReference.push().setValue(hashMap);
            }else{
                Toast.makeText(this, "Can't send empty message", Toast.LENGTH_SHORT).show();
            }
        });

    }

}