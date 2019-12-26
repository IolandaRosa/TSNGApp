package com.example.tsngapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tsngapp.R;
import com.example.tsngapp.api.AuthManager;
import com.example.tsngapp.api.SMARTAAL;
import com.example.tsngapp.ui.adapter.LightStateRecyclerAdapter;
import com.example.tsngapp.ui.adapter.decorator.SimpleHorizontalDividerItemDecoration;

public class LightStateFragment extends BaseNestedFragment {
    private RecyclerView rvStateList;
    private LightStateRecyclerAdapter listAdapter;
    private SwipeRefreshLayout refreshLayout;

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
        return R.layout.fragment_light_state;
    }

    private void loadListData() {
        this.loadListData("all");
    }

    private void loadListData(String division) {
        refreshLayout.setRefreshing(true);
        new SMARTAAL.LightLastValues(AuthManager.getInstance().getElder().getId(), division,
            AuthManager.getInstance().getUser().getAcessToken(), r -> {
                listAdapter.setList(r);
                refreshLayout.setRefreshing(false);
            },
            e -> {
                Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        ).execute();
    }

    private void setupRecyclerView() {
        listAdapter = new LightStateRecyclerAdapter();
        rvStateList.setAdapter(listAdapter);
        final LinearLayoutManager lm = new LinearLayoutManager(rootView.getContext());
        rvStateList.setLayoutManager(lm);

        final SimpleHorizontalDividerItemDecoration dividerItemDecoration =
                new SimpleHorizontalDividerItemDecoration(rvStateList.getContext());
        rvStateList.addItemDecoration(dividerItemDecoration);
    }

    private void bindViews() {
        refreshLayout = rootView.findViewById(R.id.srl);
        refreshLayout.setOnRefreshListener(this::loadListData);
        rvStateList = rootView.findViewById(R.id.rv_light_state);
        ((Spinner) rootView.findViewById(R.id.sp_divisions))
            .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String[] divisions = getResources().getStringArray(R.array.array_divisions);
                    final String division = divisions[position].toLowerCase();
                    loadListData(division);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
    }
}

