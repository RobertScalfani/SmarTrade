package com.example.smartrade.recyclerviews.stocks;

import android.app.Activity;
import android.content.Intent;

import com.example.smartrade.TradeHistoryActivity;
import com.example.smartrade.recyclerviews.IItemCard;
import com.example.smartrade.recyclerviews.IItemClickListener;

public class StockItemCard implements IItemCard, IItemClickListener {

    public final static String TICKER = "TICKER";

    private final String ticker;
    private final double sharesOwned;

    public StockItemCard(String ticker, double sharesOwned) {
        this.ticker = ticker;
        this.sharesOwned = sharesOwned;
    }

    @Override
    public void onItemClick(int position, Activity activity) {
        Intent intent = new Intent(activity, TradeHistoryActivity.class);
        intent.putExtra(TICKER, this.ticker);
        activity.startActivity(intent);
    }

    public String getTicker() {
        return this.ticker;
    }

    public String getSharesOwned() {
        return String.valueOf(this.sharesOwned);
    }

}
