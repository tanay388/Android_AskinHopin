package com.askinhopin.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BatchHorizontalAdaptar extends RecyclerView.Adapter<BatchHorizontalAdaptar.ViewHolder> {

    List<BatchModel> batchModelList = new ArrayList<>();

    public BatchHorizontalAdaptar(List<BatchModel> batchModelList) {
        this.batchModelList = batchModelList;
    }

    @NonNull
    @Override
    public BatchHorizontalAdaptar.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.batches_item_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchHorizontalAdaptar.ViewHolder holder, int position) {
        holder.getIv_batch_item().setImageResource(batchModelList.get(position).getIcon());
        holder.getTv_batch_item().setText(batchModelList.get(position).getBatchDesc());
    }

    @Override
    public int getItemCount() {
        return batchModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        ImageView iv_batch_item = itemView.findViewById(R.id.iv_batch_item);
        TextView tv_batch_item = itemView.findViewById(R.id.tv_batch_item);

        public ImageView getIv_batch_item() {
            return iv_batch_item;
        }

        public TextView getTv_batch_item() {
            return tv_batch_item;
        }
    }

}
