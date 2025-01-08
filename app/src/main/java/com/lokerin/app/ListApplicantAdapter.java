package com.lokerin.app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListApplicantAdapter extends RecyclerView.Adapter<ListApplicantAdapter.UserViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference, reviewReference;

    private List<String> userList;
    private String jobId, jobStatus;


    public ListApplicantAdapter(List<String> userList, String jobId, String jobStatus) {
        this.userList = userList;
        this.jobId = jobId;
        this.jobStatus = jobStatus;
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_txtbtn, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userId = userList.get(position);
        userReference = firebaseDatabase.getReference().child("users").child(userId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);

                    ArrayList<String> reviewsList = null;
                    if (dataSnapshot.child("reviews").exists()) {
                        GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                        reviewsList = dataSnapshot.child("reviews").getValue(typeIndicator);
                    }

                    if (userName != null && !userName.isEmpty()) {
                        holder.userNameText.setText(userName);
                    } else {
                        holder.userNameText.setText("Nama tidak diketahui!");
                    }

                    if ("ENDED".equalsIgnoreCase(jobStatus)) {
                        mAuth = FirebaseAuth.getInstance();

                        if (reviewsList != null && !reviewsList.isEmpty()) {
                            for (String reviewId : reviewsList) {
                                reviewReference = firebaseDatabase.getReference().child("reviews").child(reviewId);
                                reviewReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String pelangganIdOnReview = dataSnapshot.child("pelangganId").getValue(String.class);
                                            String jobIdOnReview = dataSnapshot.child("jobId").getValue(String.class);
                                            if (pelangganIdOnReview != null && pelangganIdOnReview.equalsIgnoreCase(mAuth.getUid()) && jobIdOnReview != null && jobIdOnReview.equalsIgnoreCase(jobId)) {
                                                holder.viewDetailButton.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        holder.viewDetailButton.setText("ULASAN");
                        holder.viewDetailButton.setOnClickListener(v -> {
                            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
                            userReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String userType = "";
                                    String userName = "";
                                    String userImageUrl = "";
                                    if (dataSnapshot.exists() && dataSnapshot.child("type").getValue() != null) {
                                        userType = dataSnapshot.child("type").getValue(String.class);
                                    }

                                    if (dataSnapshot.exists() && dataSnapshot.child("name").getValue() != null) {
                                        userName = dataSnapshot.child("name").getValue(String.class);
                                    }

                                    if (dataSnapshot.exists() && dataSnapshot.child("imageUrl").getValue() != null) {
                                        userImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                                    }

                                    if(userType.equals("pelanggan")) {
                                        Intent intent = new Intent(v.getContext(), PelangganRatingReviewActivity.class);
                                        intent.putExtra("JOB_ID", jobId);
                                        intent.putExtra("PEKERJA_ID", userId);
                                        intent.putExtra("PELANGGAN_ID", mAuth.getUid());
                                        intent.putExtra("PELANGGAN_NAME", userName);
                                        intent.putExtra("PELANGGAN_IMAGE_URL", userImageUrl);
                                        v.getContext().startActivity(intent);
                                    } else if (userType.equalsIgnoreCase("admin")){
                                        Toast.makeText(v.getContext(), "Hanya pengguna yang dapat memberikan ulasan.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                    } else {
                        holder.viewDetailButton.setOnClickListener(v -> {
                            mAuth = FirebaseAuth.getInstance();
                            userReference = firebaseDatabase.getReference().child("users").child(mAuth.getUid());
                            userReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String userType = "";

                                    if (dataSnapshot.exists() && dataSnapshot.child("type").getValue() != null) {
                                        userType = dataSnapshot.child("type").getValue(String.class);
                                    }

                                    if(userType.equals("pelanggan")) {
                                        Intent intent = new Intent(v.getContext(), PelangganViewProfilePekerjaActivity.class);
                                        intent.putExtra("JOB_ID", jobId);
                                        intent.putExtra("USER_ID", userId);
                                        v.getContext().startActivity(intent);
                                    } else if (userType.equals("pekerja")){
                                        Intent intent = new Intent(v.getContext(), PekerjaProfileActivity.class);
                                        v.getContext().startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(v.getContext(), AdminDetailProfileActivity.class);
                                        intent.putExtra("USER_ID", userId);
                                        v.getContext().startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                    }
                } else {
                    holder.userNameText.setText("Data tidak ditemukan!");
                    holder.viewDetailButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<String> updatedList) {
        this.userList = updatedList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameText;
        Button viewDetailButton;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.txt_username);
            viewDetailButton = itemView.findViewById(R.id.btn_view_dtl);
        }
    }
}
