package com.example.lokerin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListJobAdapter extends RecyclerView.Adapter<ListJobAdapter.CardViewHolder>{

    private List<Job> jobList;
    private Context context;
    private FirebaseDatabase firebaseDatabase;

    public ListJobAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_txt, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Job data = jobList.get(position);
        if (data.getJobStatus().equalsIgnoreCase("OPEN")){
            holder.jobStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        }else if (data.getJobStatus().equalsIgnoreCase("ON GOING")){
            holder.jobStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
        }else {
            holder.jobStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
        }
        holder.jobTitle.setText(data.getJobTitle() != null ? data.getJobTitle() : "-");
        holder.jobLocation.setText(data.getJobProvince() != null ? data.getJobProvince() : "-");
        holder.jobDateUpload.setText(data.getJobDateUpload() != null ? data.getJobDateUpload() : "-");
        holder.jobCategory.setText(data.getJobCategory() != null ? data.getJobCategory() : "-");

        holder.itemView.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = firebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userType = "";

                    if (dataSnapshot.exists() && dataSnapshot.child("type").getValue() != null) {
                        userType = dataSnapshot.child("type").getValue(String.class);
                    }

                    if(userType.equals("pelanggan")) {
                        Intent intent = new Intent(context.getApplicationContext(), PelangganDetailJobActivity.class);
                        intent.putExtra("jobId", data.getJobId());
                        context.startActivity(intent);
                    } else if(userType.equals("pekerja")){
                        Intent intent = new Intent(context.getApplicationContext(), PekerjaDetailJobActivity.class);
                        intent.putExtra("jobId", data.getJobId());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context.getApplicationContext(), AdminDetailJobActivity.class);
                        intent.putExtra("jobId", data.getJobId());
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateList(List<Job> updatedList) {
        this.jobList = updatedList;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        LinearLayout jobStatus;
        TextView jobTitle, jobLocation, jobDateUpload, jobCategory;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            jobStatus = itemView.findViewById(R.id.status_indicator);
            jobTitle = itemView.findViewById(R.id.text_left_upper);
            jobLocation = itemView.findViewById(R.id.text_left_bottom);
            jobDateUpload = itemView.findViewById(R.id.text_right_upper);
            jobCategory = itemView.findViewById(R.id.text_right_bottom);
        }
    }
}
