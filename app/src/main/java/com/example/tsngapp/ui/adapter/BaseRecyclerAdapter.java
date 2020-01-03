package com.example.tsngapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tsngapp.ui.adapter.listener.RecyclerViewItemLongClick;
import com.example.tsngapp.ui.adapter.listener.RecyclerViewItemShortClick;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<M, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V> {

    protected List<M> listData;
    protected RecyclerViewItemShortClick itemShortClickListener;
    protected RecyclerViewItemLongClick itemLongClickListener;

    public BaseRecyclerAdapter() {
        this.listData = new ArrayList<>();
    }

    @NonNull
    @Override
    public V onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(getItemLayoutResource(), parent, false);
        final V viewHolder = getViewHolder(view);
        if (itemShortClickListener != null) {
            view.setOnClickListener(v -> itemShortClickListener
                    .onItemShortClick(v, viewHolder.getLayoutPosition()));
        }
        if (itemLongClickListener != null) {
            view.setOnLongClickListener(v -> {
                itemLongClickListener.onItemLongClick(v, viewHolder.getLayoutPosition());
                return true;
            });
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull V holder, int position) {
        M model = listData.get(position);
        setViewData(holder, model);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    protected abstract @LayoutRes int getItemLayoutResource();

    protected abstract V getViewHolder(View view);

    protected abstract void setViewData(@NonNull V view, M m);

    public void setItemShortClickListener(RecyclerViewItemShortClick listener) {
        this.itemShortClickListener = listener;
    }

    public void setItemShortClickListener(RecyclerViewItemLongClick listener) {
        this.itemLongClickListener = listener;
    }

    public void setList(List<M> ms) {
        this.listData = ms;
        notifyDataSetChanged();
    }

    public List<M> getList() {
        return listData;
    }

    public void addItem(M m) {
        this.listData.add(m);
        notifyItemInserted(listData.size());
    }

    public void addItems(List<M> ms) {
        final int previousCount = listData.size();
        this.listData.addAll(ms);
        notifyItemRangeInserted(previousCount, listData.size());
    }

    public M getItem(int index) {
        return listData.get(index);
    }

    public void clearList() {
        this.listData.clear();
        notifyDataSetChanged();
    }
}
