package com.example.smartrade;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.smartrade.recyclerviews.AbstractRecyclerViewAdapter;
import com.example.smartrade.recyclerviews.stocks.StockItemCard;
import com.example.smartrade.recyclerviews.stocks.StockRecyclerViewAdapter;
import com.example.smartrade.recyclerviews.tradehistory.TradeHistoryItemCard;
import com.example.smartrade.recyclerviews.tradehistory.TradeHistoryRecyclerViewAdapter;
import com.example.smartrade.webservices.Database;
import com.example.smartrade.webservices.DatabaseListener;
import com.example.smartrade.webservices.TradeHistory;

import java.util.ArrayList;
import java.util.List;

public class TradeHistoryActivity extends AppCompatActivity implements DatabaseListener {

    // Trade History List.
    private AbstractRecyclerViewAdapter recyclerViewAdapter;
    private List<TradeHistoryItemCard> tradeHistorylist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_history);

        String ticker = getIntent().getStringExtra(StockItemCard.TICKER);

        this.updateRecyclerView(ticker);
    }

    private void updateRecyclerView(String ticker){
        this.tradeHistorylist = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.trade_history_recycler_view);
        this.recyclerViewAdapter = new TradeHistoryRecyclerViewAdapter(this.tradeHistorylist);
        recyclerView.setAdapter(recyclerViewAdapter);
        RecyclerView.LayoutManager recyclerLayoutManger = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManger);

        // Need to get data from Database
        Database.initializeDatabase(this);
        Database.getDatabase().requestTickerHistory(ticker);

    }

    @Override
    public void notifyMessage(String message) {

    }

    @Override
    public void notifyCashBalanceUpdate(double newCashBalance) {

    }

    @Override
    public void notifyShareCountUpdate(String ticker, double newSharesCount) {

    }

    @Override
    public void notifyStockList(String result, double sharesOwned, int position) {

    }

    @Override
    public void notifyTradeHistory(String ticker, TradeHistory tradeHistory, int position) {
        this.tradeHistorylist.add(new TradeHistoryItemCard(ticker, tradeHistory));
        recyclerViewAdapter.notifyItemInserted(position);
    }
}