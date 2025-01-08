package com.lokerin.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListAddWorkExperienceAdapter extends RecyclerView.Adapter<ListAddWorkExperienceAdapter.AddWorkExperienceHolder> {
    ArrayList<PortofolioJob> data;
    Context context;
    String source;

    public ListAddWorkExperienceAdapter(Context context, ArrayList<PortofolioJob> data, String source) {
        this.context = context;
        this.data = data;
        this.source = source;
    }

    @NonNull
    @Override
    public AddWorkExperienceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_add_work_experience, parent, false);
        return new ListAddWorkExperienceAdapter.AddWorkExperienceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddWorkExperienceHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.tvTitle.setText(data.get(position).getTitle());
        holder.tvDate.setText(dateFormat.format(data.get(position).getDate()));
        holder.tvLocation.setText(data.get(position).getLocation());
        holder.tvCategory.setText(data.get(position).getCategory());
        holder.ivEdit.setOnClickListener(v -> {
            Intent intent;
            if(source.equals("CreateProfile")){
                intent = new Intent(context, PekerjaModifyWorkExperienceActivity.class);
                intent.putExtra("activityOrigin", "CreateProfile");
            }else{
                intent = new Intent(context, PekerjaModifyWorkExperienceActivity.class);
                intent.putExtra("activityOrigin", "Profile");
            }
            intent.putExtra("portofolioData", data.get(position));
            intent.putExtra("pos",position);
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            showDeleteWorkExperienceConfirmationDialog(position);
        });
    }

    public void updateList(ArrayList<PortofolioJob> updatedList) {
        this.data = updatedList;
        notifyDataSetChanged();
    }

    private void showDeleteWorkExperienceConfirmationDialog(int position) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.confirmation_popup);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setDimAmount(0.7f);

            TextView title = dialog.findViewById(R.id.title_popup);
            title.setText("Hapus Portofolio?");

            Button btnCancel = dialog.findViewById(R.id.btn_cancel);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

            btnCancel.setOnClickListener(view -> dialog.dismiss());

            btnConfirm.setOnClickListener(view -> {
                removeWorkExperience(position);
                dialog.dismiss();
            });

            dialog.show();
        }
    }

    private void removeWorkExperience(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://lokerin-2d090-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userReference = firebaseDatabase.getReference().child("users").child(firebaseUser.getUid());


        Map<String, Object> updates = new HashMap<>();
        updates.put("portofolioJob", data);

        userReference.updateChildren(updates);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class AddWorkExperienceHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvLocation, tvCategory;
        ImageView ivEdit, ivDelete;
        public AddWorkExperienceHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_jobTitle_rvTambahPekerjaan);
            tvDate = itemView.findViewById(R.id.tv_date_rvTambahPekerjaan);
            tvLocation = itemView.findViewById(R.id.tv_location_rvTambahPekerjaan);
            tvCategory = itemView.findViewById(R.id.tv_category_rvTambahPekerjaan);
            ivEdit = itemView.findViewById(R.id.iv_edit_rvTambahPekerjaan);
            ivDelete = itemView.findViewById(R.id.iv_delete_rvTambahPekerjaan);
        }
    }
}
