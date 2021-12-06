package com.example.smartrade.recyclerviews.stocks;

import android.app.Activity;

import com.example.smartrade.recyclerviews.IItemCard;
import com.example.smartrade.recyclerviews.IItemClickListener;

public class StockItemCard implements IItemCard, IItemClickListener {

    private final String ticker;
    private final double sharesOwned;

    public StockItemCard(String ticker, double sharesOwned) {
        this.ticker = ticker;
        this.sharesOwned = sharesOwned;
    }

    @Override
    public void onItemClick(int position, Activity activity) {
        // Should start an activity to view the trade history of the stock.
    }

    public String getTicker() {
        return this.ticker;
    }

    public String getSharesOwned() {
        return String.valueOf(this.sharesOwned);
    }

}
