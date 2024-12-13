package com.example.lokerin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSkillAdapter extends RecyclerView.Adapter<ListSkillAdapter.SkillHolder> {
    ArrayList<String> data;
    Context context;
    private FirebaseApp firebaseApp;
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase ;
    private DatabaseReference userReference;

    public ListSkillAdapter(Context context, ArrayList<String> data) {
        this.context = context;
        this.data = data;
        this.firebaseApp = FirebaseApp.initializeApp(context);
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.userReference = firebaseDatabase.getReference().child("users").child(mAuth.getCurrentUser().getUid());
    }

    @NonNull
    @Override
    public ListSkillAdapter.SkillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_skill, parent, false);
        return new SkillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListSkillAdapter.SkillHolder holder, int position) {
        holder.tvName.setText(data.get(position));
        holder.ivDelete.setOnClickListener(v -> {
            showDeleteSkillConfirmationDialog(position);
        });
    }

    private void showDeleteSkillConfirmationDialog(int position) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Hapus Keterampilan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
            removeSkill(position);
//            data.remove(position);
//            Delete Skill from DB & user
            dialog.dismiss();
        });

        dialog.show();
    }

    private void removeSkill(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());

        Map<String, Object> updates = new HashMap<>();
        updates.put("skill", data);
        this.userReference.updateChildren(updates).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
            } else {
            }
        });
    }

    public void updateList(ArrayList<String> updatedList) {
        this.data = updatedList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SkillHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        ImageView ivDelete;
        public SkillHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name_rvAddKeterampilan);
            ivDelete = itemView.findViewById(R.id.iv_delete_rvAddKeterampilan);
        }
    }
}
