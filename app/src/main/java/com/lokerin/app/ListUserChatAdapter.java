package com.lokerin.app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListUserChatAdapter extends RecyclerView.Adapter<ListUserChatAdapter.ListUserChatViewHolder> {

    private Context mContext;
    private List<User> mUsers;

    String last_message;

    public ListUserChatAdapter(Context mContext, List<User> mUsers){
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ListUserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new ListUserChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserChatViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getName());
        if(user.getImageUrl().equals("default")){
            holder.profile_image.setImageResource(R.drawable.default_no_profile_icon);
        } else{
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
        }

        checkLastMessage(user.getId(), holder.lastMessage);


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("userid",user.getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ListUserChatViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        private TextView lastMessage;

        public ListUserChatViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.tv_username);
            profile_image = itemView.findViewById(R.id.iv_profile_icon);
            lastMessage = itemView.findViewById(R.id.tv_lastMessage);

        }
    }

    private void checkLastMessage(String userid, TextView lastMsg){
        last_message = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                        last_message = chat.getMessage();
                    }
                }

                switch (last_message){
                    case "default":
                        lastMsg.setText("No Message");
                        break;
                    default:
                        lastMsg.setText(last_message);
                        break;
                }

                last_message = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
