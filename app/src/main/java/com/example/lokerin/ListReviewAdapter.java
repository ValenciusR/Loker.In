package com.example.lokerin;

import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListReviewAdapter extends RecyclerView.Adapter<ListReviewAdapter.ReviewHolder> {

    ArrayList<Review> data;

    public ListReviewAdapter(ArrayList<Review> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_review, parent, false);
        return new ListReviewAdapter.ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, int position) {
        holder.tvName.setText(data.get(position).getPelangganName());
        holder.tvReview.setText(data.get(position).getReview());
        holder.rbRating.setRating(data.get(position).getRate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvReview;
        RatingBar rbRating;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_profilePicture_rvReview);
            tvName = itemView.findViewById(R.id.tv_name_rvReview);
            tvReview = itemView.findViewById(R.id.tv_review_rvReview);
            rbRating = itemView.findViewById(R.id.rb_rating_rvReview);
        }
    }
}
