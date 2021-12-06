package com.example.smartrade.recyclerviews;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class AbstractRecyclerViewHolder extends RecyclerView.ViewHolder {

    public AbstractRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setHolderTextValues(IItemCard currentItemCard);
}