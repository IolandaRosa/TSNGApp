package com.example.tsngapp.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tsngapp.R;
import com.example.tsngapp.api.AuthManager;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.ui.adapter.BedStateRecyclerAdapter;
import com.example.tsngapp.ui.adapter.DivisionStateRecyclerAdapter;
import com.example.tsngapp.ui.adapter.decorator.SimpleHorizontalDividerItemDecoration;

public class DivisionStateFragment extends BaseNestedFragment {
    private RecyclerView rvStateList;
    private DivisionStateRecyclerAdapter listAdapter;

    @Override
    protected void onCreateViewPostActions(@NonNull LayoutInflater inflater,
                                           @Nullable ViewGroup container,
                                           @Nullable Bundle savedInstanceState) {
        rootView.findViewById(R.id.btn_go_back)
                .setOnClickListener(v -> parentListener.onBackToMenuPressed());

        bindViews();
        setupRecyclerView();
        loadListData();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_division_state;
    }

    private void loadListData() {
        new SMARTAAL.DivisionValues(AuthManager.getInstance().getElder().getId(), "all",
                AuthManager.getInstance().getUser().getAcessToken(), r -> listAdapter.setList(r),
                e -> Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
        ).execute();
    }

    private void setupRecyclerView() {
        listAdapter = new DivisionStateRecyclerAdapter();
        rvStateList.setAdapter(listAdapter);
        final LinearLayoutManager lm = new LinearLayoutManager(rootView.getContext());
        rvStateList.setLayoutManager(lm);

        final SimpleHorizontalDividerItemDecoration dividerItemDecoration =
                new SimpleHorizontalDividerItemDecoration(rvStateList.getContext());
//        rvStateList.addItemDecoration(new DividerItemDecoration(rvStateList.getContext(), lm.getOrientation()));
        rvStateList.addItemDecoration(dividerItemDecoration);
    }

    private void bindViews() {
        rvStateList = rootView.findViewById(R.id.rv_division_state);
    }
}

