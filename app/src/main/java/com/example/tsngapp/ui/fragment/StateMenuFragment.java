package com.example.tsngapp.ui.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tsngapp.R;

/**
 * Fragment to hold menu of the house state fragment
 */
public class StateMenuFragment extends BaseFragment {
    private FragmentActionListener actionListener;

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        setupMenuButtons();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_state_menu;
    }

    private void setupMenuButtons() {
        final View currentMenuItemView = rootView.findViewById(R.id.menu_item_current);
        ((ImageView) currentMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_power_black_24dp);
        ((TextView) currentMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_current_state);
        final View bedMenuItemView = rootView.findViewById(R.id.menu_item_bed);
        ((ImageView) bedMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_hotel_black_24dp);
        ((TextView) bedMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_bed_state);
        final View doorMenuItemView = rootView.findViewById(R.id.menu_item_door);
        ((ImageView) doorMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_mdi_door_closed_black_24dp);
        ((TextView) doorMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_door_state);
        final View cameraMenuItemView = rootView.findViewById(R.id.menu_item_camera);
        ((ImageView) cameraMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_camera_alt_black_24dp);
        ((TextView) cameraMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_camera_state);
        final View divisionMenuItemView = rootView.findViewById(R.id.menu_item_divisions);
        ((ImageView) divisionMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_mdi_home_modern_black_24dp);
        ((TextView) divisionMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_division_state);
        final View internalTempMenuItemView = rootView.findViewById(R.id.menu_item_internal_temp);
        ((ImageView) internalTempMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_mdi_thermometer_black_24dp);
        ((TextView) internalTempMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_internal_temp_state);
        final View windowsMenuItemView = rootView.findViewById(R.id.menu_item_windows);
        ((ImageView) windowsMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_mdi_window_closed_black_24dp);
        ((TextView) windowsMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_window_state);
        final View lightsMenuItemView = rootView.findViewById(R.id.menu_item_lights);
        ((ImageView) lightsMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_lightbulb_outline_black_24dp);
        ((TextView) lightsMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_lights_state);
        final View sosMenuItemView = rootView.findViewById(R.id.menu_item_sos);
        ((ImageView) sosMenuItemView.findViewById(R.id.iv_item_state_menu_icon))
                .setImageResource(R.drawable.ic_alert_sign_black_24dp);
        ((TextView) sosMenuItemView.findViewById(R.id.tv_item_state_menu_label))
                .setText(R.string.label_sos_state);
    }

    public interface FragmentActionListener {
        void onMenuItemClicked(Class fragmentClass);
    }
}

