package com.example.tsngapp.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.tsngapp.R;

public class StateMenuItem extends LinearLayout {
    private ImageView icon;
    private TextView label;
    private View divider;

    public StateMenuItem(Context context) {
        super(context);
        init(context, null);
    }

    public StateMenuItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StateMenuItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public StateMenuItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.item_state_menu, this);
        icon = findViewById(R.id.iv_item_state_menu_icon);
        label = findViewById(R.id.tv_item_state_menu_label);
        divider = findViewById(R.id.v_item_state_menu_divider);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StateMenuItem, 0, 0);

        try {
            final @DrawableRes int iconRes = ta.getResourceId(R.styleable.StateMenuItem_menuIcon, 0);
            final @StringRes int labelRes = ta.getResourceId(R.styleable.StateMenuItem_menuLabel, 0);
            final boolean showDivider = ta.getBoolean(R.styleable.StateMenuItem_showDivider, false);
            icon.setImageResource(iconRes);
            label.setText(labelRes);
            divider.setVisibility(showDivider ? VISIBLE : GONE);
        } finally {
            ta.recycle();
        }
    }
}
