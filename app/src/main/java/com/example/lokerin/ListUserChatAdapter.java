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

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class ListUserChatAdapter extends RecyclerView.Adapter<ListUserChatAdapter.ListUserChatViewHolder> {

    private Context mContext;
    private List<User> mUsers;

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
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else{
            Glide.with(mContext).load(user.getImageUrl()).into(holder.profile_image);
        }


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

        public ListUserChatViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.tv_username);
            profile_image = itemView.findViewById(R.id.iv_profile_icon);

        }
    }

}
