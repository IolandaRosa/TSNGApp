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
import com.example.tsngapp.helpers.StringUtil;

import java.text.SimpleDateFormat;

public class WindowStateRecyclerAdapter extends
        BaseRecyclerAdapter<SMARTAAL.WindowLastValues.Data, WindowStateRecyclerAdapter.ViewHolder> {

    @Override
    protected int getItemLayoutResource() {
        return R.layout.item_two_line_state;
    }

    @Override
    protected ViewHolder getViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    protected void setViewData(@NonNull ViewHolder view, SMARTAAL.WindowLastValues.Data data) {
        view.tvDivision.setText(StringUtil.capitalize(data.getDivision()));

        if (data.isClosed()) {
            view.tvAction.setText(R.string.label_closed);
            view.ivIcon.setImageResource(R.drawable.ic_mdi_window_closed_black_24dp);
            view.ivIcon.setColorFilter(ContextCompat.getColor(view.ivIcon.getContext(),
                    R.color.md_teal_500), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            view.tvAction.setText(R.string.label_open);
            view.ivIcon.setImageResource(R.drawable.ic_mdi_window_open_black_24dp);
            view.ivIcon.setColorFilter(ContextCompat.getColor(view.ivIcon.getContext(),
                    R.color.md_red_500), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = dateFormat.format(data.getDate());
        view.tvDate.setText(formattedDate);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvDivision;
        TextView tvAction;
        TextView tvDate;

        ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.iv_icon_two_line_state_item);
            tvDivision = view.findViewById(R.id.tv_primary_two_line_state_item);
            tvAction = view.findViewById(R.id.tv_secondary_two_line_state_item);
            tvDate = view.findViewById(R.id.tv_date_two_line_state_item);
        }
    }
}
