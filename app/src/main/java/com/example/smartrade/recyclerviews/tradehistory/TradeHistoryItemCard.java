package com.example.smartrade.recyclerviews.tradehistory;

import android.app.Activity;

import com.example.smartrade.recyclerviews.IItemCard;
import com.example.smartrade.recyclerviews.IItemClickListener;
import com.example.smartrade.webservices.TradeHistory;

public class TradeHistoryItemCard implements IItemCard, IItemClickListener {

    public final String ticker;
    public final double numberOfShares;
    public final double pricePerShare;
    public final String tradeDate;
    public final String transactionType;

    public TradeHistoryItemCard(String ticker, TradeHistory tradeHistory) {
        this.ticker = ticker;
        this.numberOfShares = tradeHistory.numberOfShares;
        this.pricePerShare = tradeHistory.costPerShare;
        this.tradeDate = tradeHistory.tradeDate;
        this.transactionType = tradeHistory.transactionType;
    }

    @Override
    public void onItemClick(int position, Activity activity) {
        // Do nothing.
    }

}
