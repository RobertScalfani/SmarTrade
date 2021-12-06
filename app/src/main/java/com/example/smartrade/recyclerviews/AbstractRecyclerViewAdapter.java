package com.example.smartrade.recyclerviews;

import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractRecyclerViewAdapter extends RecyclerView.Adapter<AbstractRecyclerViewHolder> {

    protected IItemClickListener listener;

    public void setOnItemClickListener(IItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(AbstractRecyclerViewHolder holder, int position) {
        holder.setHolderTextValues(this.getItemCard(position));
    }

    protected abstract IItemCard getItemCard(int position);

}