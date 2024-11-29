package com.example.lokerin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListJobAdapter extends RecyclerView.Adapter<ListJobAdapter.CardViewHolder> {

    private List<JobData> jobDataList;
    private Context context;

    public ListJobAdapter(Context context, List<JobData> jobDataList) {
        this.context = context;
        this.jobDataList = jobDataList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_txt, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        JobData data = jobDataList.get(position);
        holder.jobTitle.setText(data.getJobTitle());
        holder.jobLocation.setText(data.getJobLocation());
        holder.jobDateUpload.setText(data.getJobDateUpload());
        holder.jobCategory.setText(data.getJobCategory());

        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, DetailJobActivity.class);
            Intent intent = new Intent(context, PekerjaDetailJobActivity.class);
            intent.putExtra("jobTitle", data.getJobTitle());
            intent.putExtra("jobLocation", data.getJobLocation());
            intent.putExtra("jobDateUpload", data.getJobDateUpload());
            intent.putExtra("jobCategory", data.getJobCategory());
            intent.putExtra("jobStatus", data.getJobStatus());
            intent.putExtra("jobApplicants", data.getApplicants().toArray(new User[0]));
            context.startActivity(intent);
        });
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
