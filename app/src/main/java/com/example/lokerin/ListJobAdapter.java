package com.example.lokerin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListJobAdapter extends RecyclerView.Adapter<ListJobAdapter.CardViewHolder> {

    private List<JobData> jobDataList;

    public ListJobAdapter(List<JobData> jobDataList) {
        this.jobDataList = jobDataList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        JobData data = jobDataList.get(position);
        holder.jobTitle.setText(data.getJobTitle());
        holder.jobLocation.setText(data.getJobLocation());
        holder.jobDateUpload.setText(data.getJobDateUpload());
        holder.jobCategory.setText(data.getJobCategory());
    }

    @Override
    public int getItemCount() {
        return jobDataList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobLocation, jobDateUpload, jobCategory;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_left_upper);
            jobLocation = itemView.findViewById(R.id.text_left_bottom);
            jobDateUpload = itemView.findViewById(R.id.text_right_upper);
            jobCategory = itemView.findViewById(R.id.text_right_bottom);
        }
    }
}
