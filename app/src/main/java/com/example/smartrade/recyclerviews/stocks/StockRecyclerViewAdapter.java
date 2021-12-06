package com.example.smartrade.recyclerviews.stocks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartrade.R;
import com.example.smartrade.recyclerviews.AbstractRecyclerViewAdapter;
import com.example.smartrade.recyclerviews.AbstractRecyclerViewHolder;
import com.example.smartrade.recyclerviews.IItemCard;

import java.util.List;

public class StockRecyclerViewAdapter extends AbstractRecyclerViewAdapter {

    private final List<StockItemCard> itemList;

    /**
     * Constructor.
     *
     * @param itemList The list of items.
     */
    public StockRecyclerViewAdapter(List<StockItemCard> itemList) {
        this.itemList = itemList;
    }

    @Override
    public AbstractRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_item_card, parent, false);
        return new StockRecyclerViewHolder(view, this.listener);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    @Override
    protected IItemCard getItemCard(int position) {
        return this.itemList.get(position);
    }
}