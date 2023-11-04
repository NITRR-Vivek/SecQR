package com.wearev.secqr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.ViewHolder> {

    private final ArrayList<ParentItem> parentItemArrayList;
    private OnShareClickListener shareClickListener;
    private OnDeleteClickListener deleteClickListener;

    public MyHistoryAdapter(ArrayList<ParentItem> parentItemArrayList) {
        this.parentItemArrayList = parentItemArrayList;
     }
     public interface OnShareClickListener {
        void onShareClick(int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
    public void setOnShareClickListener(OnShareClickListener listener) {
        this.shareClickListener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qr_history_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHistoryAdapter.ViewHolder holder, int position) {
        ParentItem parentItem = parentItemArrayList.get(position);

        holder.id.setText(String.format("%d", position+1));
        holder.regNo.setText(parentItem.regNo);
        holder.vehicle_class.setText(parentItem.vehicle_class);
        holder.date_created.setText(parentItem.date_created);
        holder.ivQR.setImageBitmap(parentItem.QRimage);
        holder.shareButton.setOnClickListener(view -> {
            if (shareClickListener != null) {
                shareClickListener.onShareClick(position);
            }
        });

        holder.deleteButton.setOnClickListener(view -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parentItemArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView id, regNo,vehicle_class,date_created;
        ImageView ivQR;
        public ImageView shareButton;
        public ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.tvId);
            regNo = itemView.findViewById(R.id.tvRegNo);
            date_created = itemView.findViewById(R.id.tvCreated);
            vehicle_class = itemView.findViewById(R.id.tvVehicleClass);
            ivQR = itemView.findViewById(R.id.ivQR);
            shareButton = itemView.findViewById(R.id.shareCardQR);
            deleteButton = itemView.findViewById(R.id.deleteCardQR);

        }
    }
}
