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

import java.util.List;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.CardViewHolder> {

        private List<Category> itemList;
        private Context context;

        public ListCategoryAdapter(Context context, List<Category> itemList) {
            this.context = context;
            this.itemList = itemList;
        }

        @NonNull
        @Override
        public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_category, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
            Category data = itemList.get(position);
            holder.image.setImageResource(data.getImageResId());
            holder.text.setText(data.getTitle());

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, PekerjaCategorizedJobActivity.class);
                intent.putExtra("category", data.getTitle());
                context.startActivity(intent);
            });

        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public static class CardViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView text;

            public CardViewHolder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image1);
                text = itemView.findViewById(R.id.text1);
            }
        }
}
