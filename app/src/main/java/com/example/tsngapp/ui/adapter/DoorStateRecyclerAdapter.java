package com.example.tsngapp.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tsngapp.R;
import com.example.tsngapp.api.SMARTAAL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DoorStateRecyclerAdapter extends
        BaseRecyclerAdapter<SMARTAAL.DoorState.Data, DoorStateRecyclerAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_door_state, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        if (itemShortClickListener != null) {
            view.setOnClickListener(v -> itemShortClickListener
                    .onItemShortClick(v, viewHolder.getLayoutPosition()));
        }
        if (itemLongClickListener != null) {
            view.setOnLongClickListener(v -> {
                itemLongClickListener.onItemLongClick(v, viewHolder.getLayoutPosition());
                return true;
            });
        }
        return viewHolder;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SMARTAAL.DoorState.Data sensorData = listData.get(position);
        if (sensorData.isInside()) {
            holder.tvAction.setText(R.string.label_entered);
            holder.ivIcon.setImageResource(R.drawable.ic_exit_to_app_black_24dp);
            holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.ivIcon.getContext(),
                    R.color.md_teal_500), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.tvAction.setText(R.string.label_left);
            holder.ivIcon.setImageResource(R.drawable.ic_mdi_logout_variant_black_24dp);
            holder.ivIcon.setColorFilter(ContextCompat.getColor(holder.ivIcon.getContext(),
                    R.color.md_red_500), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = dateFormat.format(sensorData.getUpdatedAt());
        holder.tvDate.setText(formattedDate);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvAction;
        TextView tvDate;

        ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.iv_icon_door_state_item);
            tvAction = view.findViewById(R.id.tv_action_door_state_item);
            tvDate = view.findViewById(R.id.tv_date_door_state_item);
        }
    }
}
