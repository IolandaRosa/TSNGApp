package com.example.tsngapp.ui.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tsngapp.ui.adapter.listener.RecyclerViewItemLongClick;
import com.example.tsngapp.ui.adapter.listener.RecyclerViewItemShortClick;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerAdapter<E, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V> {

    protected List<E> listData;
    protected RecyclerViewItemShortClick itemShortClickListener;
    protected RecyclerViewItemLongClick itemLongClickListener;

    public BaseRecyclerAdapter() {
        this.listData = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItems(List<E> es) {
        final int previousCount = listData.size();
        this.listData.addAll(es);
        notifyItemRangeInserted(previousCount, listData.size());
    }

    public void setItemShortClickListener(RecyclerViewItemShortClick listener) {
        this.itemShortClickListener = listener;
    }

    public void setItemShortClickListener(RecyclerViewItemLongClick listener) {
        this.itemLongClickListener = listener;
    }

    public void addItem(E e) {
        this.listData.add(e);
        notifyItemInserted(listData.size());
    }

    public void setList(List<E> es) {
        this.listData = es;
        notifyDataSetChanged();
    }

    public List<E> getList() {
        return listData;
    }

    public E getItem(int index) {
        return listData.get(index);
    }

    public void clearList() {
        this.listData.clear();
        notifyDataSetChanged();
    }
}
