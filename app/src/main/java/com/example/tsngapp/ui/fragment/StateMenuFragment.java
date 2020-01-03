package com.example.tsngapp.ui.fragment;


import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tsngapp.R;
import com.example.tsngapp.ui.fragment.listener.BaseFragmentActionListener;
import com.example.tsngapp.ui.view.StateMenuItem;

/**
 * Fragment to hold menu of the house state fragment
 */
public class StateMenuFragment extends BaseFragment {
    public enum MenuItem {
        CURRENT (R.id.menu_item_current, R.string.label_current_state),
        BED (R.id.menu_item_bed, R.string.label_bed_state),
        DOOR (R.id.menu_item_door, R.string.label_door_state),
        DIVISIONS (R.id.menu_item_divisions, R.string.label_division_state),
        // INTERNAL_TEMP (R.id.menu_item_internal_temp, R.string.label_internal_temp_state),
        WINDOWS (R.id.menu_item_windows, R.string.label_window_state),
        LIGHTS (R.id.menu_item_lights, R.string.label_lights_state),
        SOS (R.id.menu_item_sos, R.string.label_sos_state);

        private @IdRes int itemViewId;
        private @StringRes int title;

        MenuItem(@IdRes int itemViewId, @StringRes int title) {
            this.itemViewId = itemViewId;
            this.title = title;
        }

        public int getItemViewId() {
            return itemViewId;
        }

        public int getTitle() {
            return title;
        }
    }

    /**
     * Listener specific to this fragment (defined on the bottom)
     */
    private FragmentActionListener actionListener;

    @Override
    protected void onCreateViewActions(@NonNull LayoutInflater inflater,
                                       @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        setupMenuActions();
        onAttachToParentFragment(getParentFragment());
    }

    private void onAttachToParentFragment(Fragment fragment) {
        try {
            actionListener = (FragmentActionListener)fragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString() + " must implement FragmentActionListener");
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_state_menu;
    }

    private void setupMenuActions() {
        for (MenuItem item : MenuItem.values()) {
            setupMenuItem(item);
        }
    }

    private void setupMenuItem(MenuItem item) {
        rootView.findViewById(item.getItemViewId())
                .setOnClickListener(v -> actionListener.onMenuItemClicked(item));
    }

    public interface FragmentActionListener {
        void onMenuItemClicked(MenuItem item);
    }
}

