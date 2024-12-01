package com.example.lokerin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT =1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.MessageViewHolder(view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.MessageViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.show_message.setText(chat.getMessage());

        holder.profile_image.setImageResource(R.mipmap.ic_launcher);

//        if(imageurl.equals("default")){
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
//        } else{
//            Glide
//        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;

        public MessageViewHolder(View itemView){
            super(itemView);

            show_message = itemView.findViewById(R.id.tv_show_message);
            profile_image = itemView.findViewById(R.id.imageView_profile_icon);

        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
