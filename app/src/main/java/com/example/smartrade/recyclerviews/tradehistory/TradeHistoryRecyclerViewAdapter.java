package com.example.smartrade.recyclerviews.tradehistory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartrade.R;
import com.example.smartrade.recyclerviews.AbstractRecyclerViewAdapter;
import com.example.smartrade.recyclerviews.AbstractRecyclerViewHolder;
import com.example.smartrade.recyclerviews.IItemCard;

import java.util.List;

public class TradeHistoryRecyclerViewAdapter extends AbstractRecyclerViewAdapter {

    private final List<TradeHistoryItemCard> itemList;

    /**
     * Constructor.
     *
     * @param itemList The list of items.
     */
    public TradeHistoryRecyclerViewAdapter(List<TradeHistoryItemCard> itemList) {
        this.itemList = itemList;
    }

    @Override
    public AbstractRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trade_history_item_card, parent, false);
        return new TradeHistoryRecyclerViewHolder(view, this.listener);
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