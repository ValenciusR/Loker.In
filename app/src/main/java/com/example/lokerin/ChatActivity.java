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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ImageView ivProfileImage, btnBack;
    TextView tvUsername;


    FirebaseUser fuser;

    Intent intent;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersReference, chatsReference;

    ImageButton btnSend;
    EditText etMessage;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView rvChat;


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

        rvChat = findViewById(R.id.rv_chatting);
        rvChat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);

        intent = getIntent();
        String userid = intent.getStringExtra("userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        usersReference = firebaseDatabase.getReference().child("users").child(userid);
        chatsReference = firebaseDatabase.getReference().child("chats");


        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tvUsername.setText(user.getName());
                if(user.getImageUrl().equals("default")){
                    ivProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else{
                    Glide.with(ChatActivity.this).load(user.getImageUrl()).into(ivProfileImage);
                }

                mChat = new ArrayList<>();
                chatsReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mChat.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Chat chat = snapshot.getValue(Chat.class);
                            if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(fuser.getUid())){
                                mChat.add(chat);
                            }
                            messageAdapter = new MessageAdapter(ChatActivity.this,mChat,user.getImageUrl());
                            rvChat.setAdapter(messageAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
                hashMap.put("date", new Date());

                chatsReference.push().setValue(hashMap);

                DatabaseReference chatRef = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chatlist").child(fuser.getUid()).child(userid);
                DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chatlist").child(userid).child(fuser.getUid());

                chatRefReceiver.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()){
                            chatRefReceiver.child("id").setValue(fuser.getUid());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                chatRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            chatRef.child("id").setValue(userid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }else{
                Toast.makeText(this, "Can't send empty message", Toast.LENGTH_SHORT).show();
            }
            etMessage.setText("");
        });

    }

}