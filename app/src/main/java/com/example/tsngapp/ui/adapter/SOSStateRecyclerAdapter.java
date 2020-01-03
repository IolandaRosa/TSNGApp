package com.example.tsngapp.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tsngapp.R;
import com.example.tsngapp.helpers.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SOSStateRecyclerAdapter extends
        BaseRecyclerAdapter<Date, SOSStateRecyclerAdapter.ViewHolder> {

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_icon_label_state;
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    protected void setViewData(@NonNull ViewHolder view, Date date) {
        final String dateStr = DateUtil.getStringFromDate(date);
        view.tvLabel.setText(dateStr);
        view.ivIcon.setImageResource(R.drawable.ic_alert_sign_black_24dp);
        view.ivIcon.setColorFilter(ContextCompat.getColor(view.ivIcon.getContext(),
                R.color.md_red_500), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvLabel;

        ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.tv_il_icon_state_item);
            tvLabel = view.findViewById(R.id.tv_il_label_state_item);
        }
    }
}
