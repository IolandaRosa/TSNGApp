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

public class LightStateRecyclerAdapter extends
        BaseRecyclerAdapter<SMARTAAL.LightLastValues.Data, LightStateRecyclerAdapter.ViewHolder> {

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
    protected void setViewData(@NonNull ViewHolder view, SMARTAAL.LightLastValues.Data data) {
        view.tvDivision.setText(StringUtil.capitalize(data.getDivision()));

        if (data.isTurnedOn()) {
            view.tvState.setText(R.string.label_on);
            view.ivIcon.setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
            view.ivIcon.setColorFilter(ContextCompat.getColor(view.ivIcon.getContext(),
                    R.color.md_teal_500), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            view.tvState.setText(R.string.label_off);
            view.ivIcon.setImageResource(R.drawable.ic_mdi_lightbulb_outline_off_black_24dp);
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
        TextView tvState;
        TextView tvDate;

        ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.iv_icon_two_line_state_item);
            tvDivision = view.findViewById(R.id.tv_primary_two_line_state_item);
            tvState = view.findViewById(R.id.tv_secondary_two_line_state_item);
            tvDate = view.findViewById(R.id.tv_date_two_line_state_item);
        }
    }
}
