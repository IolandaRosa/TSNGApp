package com.example.tsngapp.ui.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.tsngapp.R;

public class ElectricalCurrentStateFragment extends BaseStateMenuItemFragment {

    @Override
    protected void onCreateViewPostActions(@NonNull LayoutInflater inflater,
                                           @Nullable ViewGroup container,
                                           @Nullable Bundle savedInstanceState) {
        rootView.findViewById(R.id.btn_go_back)
                .setOnClickListener(v -> {
                    parentListener.onBackToMenuPressed();
                });
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_electrical_current_state;
    }
}

