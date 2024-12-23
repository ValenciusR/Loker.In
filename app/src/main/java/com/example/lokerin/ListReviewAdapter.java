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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListReviewAdapter extends RecyclerView.Adapter<ListReviewAdapter.ReviewHolder> {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference pelangganReference;

    private List<Review> data;

    public ListReviewAdapter(List<Review> data) {
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
        Review review = data.get(position);

        String pelangganId = review.getPelangganId();
        firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        pelangganReference = firebaseDatabase.getReference().child("users").child(pelangganId);
        pelangganReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user.getImageUrl().equals("default")){
                    holder.ivImage.setImageResource(R.drawable.default_no_profile_icon);
                } else{
                    Glide.with(holder.ivImage.getContext())
                            .load(user.getImageUrl())
                            .into(holder.ivImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.tvName.setText(review.getPelangganName());
        holder.tvReview.setText(review.getReview());

        Float rate = review.getRating();
        if (rate != null) {
            holder.rbRating.setRating(rate);
        } else {
            holder.rbRating.setRating(0.0f);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateList(List<Review> updatedList) {
        this.data = updatedList;
        notifyDataSetChanged();
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
