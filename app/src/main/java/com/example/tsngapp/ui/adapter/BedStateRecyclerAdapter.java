package com.example.tsngapp.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tsngapp.R;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.helpers.DateUtil;

import java.text.SimpleDateFormat;

public class BedStateRecyclerAdapter extends
        BaseRecyclerAdapter<SMARTAAL.BedState.Data, BedStateRecyclerAdapter.ViewHolder> {

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_single_line_state;
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    protected void setViewData(@NonNull ViewHolder view, SMARTAAL.BedState.Data data) {
        if (data.isAwake()) {
            view.tvAction.setText(R.string.label_awake);
            view.ivIcon.setImageResource(R.drawable.ic_mdi_bed_empty_black_24dp);
            view.ivIcon.setColorFilter(ContextCompat.getColor(view.ivIcon.getContext(),
                    R.color.md_teal_500), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            view.tvAction.setText(R.string.label_asleep);
            view.ivIcon.setImageResource(R.drawable.ic_hotel_black_24dp);
            view.ivIcon.setColorFilter(ContextCompat.getColor(view.ivIcon.getContext(),
                    R.color.md_red_500), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        String formattedDate = DateUtil.getStringFromDate(data.getUpdatedAt());
        view.tvDate.setText(formattedDate);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvAction;
        TextView tvDate;

        ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.iv_icon_single_line_state_item);
            tvAction = view.findViewById(R.id.tv_primary_single_line_state_item);
            tvDate = view.findViewById(R.id.tv_date_single_line_state_item);
        }
    }
}
