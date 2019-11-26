package com.example.tsngapp.ui.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.tsngapp.R;
import com.example.tsngapp.ui.fragment.listener.StateMenuFragmentActionListener;

public class ElectricalCurrentStateFragment extends BaseNestedFragment {

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

