package com.example.lokerin;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ListApplicantAdapter extends RecyclerView.Adapter<ListApplicantAdapter.UserViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    private List<String> userList;
    private String jobId, jobStatus;


    public ListApplicantAdapter(List<String> userList, String jobId, String status) {
        this.userList = userList;
        this.jobId = jobId;
        this.jobStatus = status;
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
        userReference = firebaseDatabase.getReference().child("users").child(userId).child("name");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue(String.class);

                if (userName != null && !userName.isEmpty()) {
                    holder.userNameText.setText(userName);
                } else {
                    holder.userNameText.setText("Nama tidak diketahui!");
                }

                if ("ENDED".equalsIgnoreCase(jobStatus)) {
                    holder.viewDetailButton.setText("RATE REVIEW");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if ("ENDED".equalsIgnoreCase(jobStatus)) {
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
                            Intent intent = new Intent(v.getContext(), PelangganRatingReviewActivity.class);
                            intent.putExtra("JOB_ID", jobId);
                            intent.putExtra("USER_ID", userId);
                            v.getContext().startActivity(intent);
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
                            Intent intent = new Intent(v.getContext(), PekerjaProfileActivity.class);
                            intent.putExtra("fromUserType", "PELANGGAN");
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
