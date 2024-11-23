package com.example.lokerin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListPortofolioAdapter extends RecyclerView.Adapter<ListPortofolioAdapter.PortofolioHolder> {
    ArrayList<Portofolio> data;

    public ListPortofolioAdapter(ArrayList<Portofolio> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PortofolioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_portofolio, parent, false);
        return new PortofolioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PortofolioHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
        holder.tvJob.setText(data.get(position).jobTitle);
        holder.tvDate.setText(dateFormat.format(data.get(position).date));
        holder.tvDescription.setText(data.get(position).jobDescription);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PortofolioHolder extends RecyclerView.ViewHolder{
        ImageView ivImage;
        TextView tvJob, tvDate, tvDescription;

        public PortofolioHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_image_rvPortofolio);
            tvJob = itemView.findViewById(R.id.tv_jobTitle_rvPortofolio);
            tvDate = itemView.findViewById(R.id.tv_date_rvPortofolio);
            tvDescription = itemView.findViewById(R.id.tv_jobDescription_rvPortofolio);
        }
    }
}
