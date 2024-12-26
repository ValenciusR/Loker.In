package com.example.lokerin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListEditPortofolioAdapter extends RecyclerView.Adapter<ListEditPortofolioAdapter.EditPortofolioHolder> {
    ArrayList<PortofolioJob> data;

    public ListEditPortofolioAdapter(ArrayList<PortofolioJob> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public EditPortofolioHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_edit_portofolio, parent, false);
        return new ListEditPortofolioAdapter.EditPortofolioHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditPortofolioHolder holder, int position) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.tvTitle.setText(data.get(position).getTitle());
        holder.tvDate.setText(dateFormat.format(data.get(position).getDate()));
        holder.tvLocation.setText(data.get(position).getLocation());
        holder.tvCategory.setText(data.get(position).getCategory());;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class EditPortofolioHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvLocation, tvCategory;
        CheckBox cbCheckBox;

        public EditPortofolioHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_jobTitle_rvEditPortofolio);
            tvDate = itemView.findViewById(R.id.tv_date_rvEditPortofolio);
            tvLocation = itemView.findViewById(R.id.tv_location_rvEditPortofolio);
            tvCategory = itemView.findViewById(R.id.tv_category_rvEditPortofolio);
            cbCheckBox = itemView.findViewById(R.id.cb_check_rvEditPortofolio);
        }
    }
}
