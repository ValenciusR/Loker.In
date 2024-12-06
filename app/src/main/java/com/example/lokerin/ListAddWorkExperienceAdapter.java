package com.example.lokerin;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListAddWorkExperienceAdapter extends RecyclerView.Adapter<ListAddWorkExperienceAdapter.AddWorkExperienceHolder> {
    ArrayList<PortofolioJob> data;
    Context context;

    public ListAddWorkExperienceAdapter(Context context, ArrayList<PortofolioJob> data) {
        this.context = context;
        this.data = data;
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
            Intent intent = new Intent(context, PekerjaModifyWorkExperienceActivity.class);
//            Get Edit Work Experience data codes here
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            shotDeleteWorkExperienceConfirmationDialog();
        });
    }

    private void shotDeleteWorkExperienceConfirmationDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.confirmation_popup);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.7f);

        TextView title = dialog.findViewById(R.id.title_popup);
        title.setText("Hapus Pekerjaan?");

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnConfirm = dialog.findViewById(R.id.btn_confirm);

        btnCancel.setOnClickListener(view -> dialog.dismiss());

        btnConfirm.setOnClickListener(view -> {
//            Delete Work Experience from DB & user
            dialog.dismiss();
        });

        dialog.show();
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
