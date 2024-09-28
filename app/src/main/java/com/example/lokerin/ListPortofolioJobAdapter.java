package com.example.lokerin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ListPortofolioJobAdapter extends RecyclerView.Adapter<ListPortofolioJobAdapter.ViewHolder> {

    private List<JobData> jobDataList;

    public ListPortofolioJobAdapter(List<JobData> jobDataList) {
        this.jobDataList = jobDataList;
    }

    @NonNull
    @Override
    public ListPortofolioJobAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ListPortofolioJobAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListPortofolioJobAdapter.ViewHolder holder, int position) {
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobLocation, jobDateUpload, jobCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.text_left_upper);
            jobLocation = itemView.findViewById(R.id.text_left_bottom);
            jobDateUpload = itemView.findViewById(R.id.text_right_upper);
            jobCategory = itemView.findViewById(R.id.text_right_bottom);
        }
    }
}
