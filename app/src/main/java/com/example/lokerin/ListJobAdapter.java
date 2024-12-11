package com.example.lokerin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListJobAdapter extends RecyclerView.Adapter<ListJobAdapter.CardViewHolder> implements Filterable {

    private List<JobData> jobDataList;
    private List<JobData> filteredList;
    private Context context;
    private FirebaseDatabase firebaseDatabase;

    public ListJobAdapter(Context context, List<JobData> jobDataList) {
        this.context = context;
        this.jobDataList = jobDataList;
        this.filteredList = new ArrayList<>(jobDataList);
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_txt, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        JobData data = filteredList.get(position); // Use filteredList
        holder.jobTitle.setText(data.getJobTitle());
        holder.jobLocation.setText(data.getJobLocation());
        holder.jobDateUpload.setText(data.getJobDateUpload());
        holder.jobCategory.setText(data.getJobCategory());

        holder.itemView.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userType = dataSnapshot.child("type").getValue().toString();
                    if(userType.equals("pelanggan")) {
                        Intent intent = new Intent(context, PelangganDetailJobActivity.class);
                        intent.putExtra("jobTitle", data.getJobTitle());
                        intent.putExtra("jobLocation", data.getJobLocation());
                        intent.putExtra("jobDateUpload", data.getJobDateUpload());
                        intent.putExtra("jobCategory", data.getJobCategory());
                        intent.putExtra("jobStatus", data.getJobStatus());
                        intent.putExtra("jobApplicants", data.getApplicants().toArray(new User[0]));
                        context.startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(context, PekerjaDetailJobActivity.class);
                        intent.putExtra("jobTitle", data.getJobTitle());
                        intent.putExtra("jobLocation", data.getJobLocation());
                        intent.putExtra("jobDateUpload", data.getJobDateUpload());
                        intent.putExtra("jobCategory", data.getJobCategory());
                        intent.putExtra("jobStatus", data.getJobStatus());
                        intent.putExtra("jobApplicants", data.getApplicants().toArray(new User[0]));
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            Intent intent = new Intent(context, PekerjaDetailJobActivity.class);
//            intent.putExtra("jobTitle", data.getJobTitle());
//            intent.putExtra("jobLocation", data.getJobLocation());
//            intent.putExtra("jobDateUpload", data.getJobDateUpload());
//            intent.putExtra("jobCategory", data.getJobCategory());
//            intent.putExtra("jobStatus", data.getJobStatus());
//            intent.putExtra("jobApplicants", data.getApplicants().toArray(new User[0]));
//            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                List<JobData> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered.addAll(jobDataList);
                } else {
                    for (JobData job : jobDataList) {
                        if (job.getJobTitle().toLowerCase().contains(query)) {
                            filtered.add(job);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                filteredList.addAll((List<JobData>) results.values);
                notifyDataSetChanged();
            }
        };
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
