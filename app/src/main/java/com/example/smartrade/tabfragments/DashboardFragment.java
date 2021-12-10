package com.example.smartrade.tabfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartrade.LoginActivity;
import com.example.smartrade.R;
import com.example.smartrade.recyclerviews.AbstractRecyclerViewAdapter;
import com.example.smartrade.recyclerviews.IItemClickListener;
import com.example.smartrade.recyclerviews.stocks.StockItemCard;
import com.example.smartrade.recyclerviews.stocks.StockRecyclerViewAdapter;
import com.example.smartrade.webservices.Database;
import com.example.smartrade.webservices.DatabaseListener;
import com.example.smartrade.webservices.TradeHistory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements DatabaseListener {

    // Stock Owned List.
    private AbstractRecyclerViewAdapter recyclerViewAdapter;
    private List<StockItemCard> stockItemList = new ArrayList<>();

    TextView portfolioValue;
    TextView cashBalance;

    public DashboardFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Update the current Cash Balance.
        Database.initializeDatabase(this);
        Database.getDatabase().addToCashBalance(0.0);

        try {
            Database.getDatabase().generateLeaderboardRankings();
        } catch (Database.FirebaseAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Database.initializeDatabase(this);
        // Update the current Cash Balance.
        Database.getDatabase().addToCashBalance(0.0);
        String portfolioBalance = String.format("$%.2f", Database.getDatabase().currentUserPortfolioBalance);
        if(!portfolioBalance.equals("$nu")) {
            portfolioValue.setText(portfolioBalance);
        }
        this.updateRecyclerView();

        try {
            Database.getDatabase().generateLeaderboardRankings();
        } catch (Database.FirebaseAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.portfolioValue = rootView.findViewById(R.id.textViewPortfolioValue);
        this.cashBalance = rootView.findViewById(R.id.cashBalance);

        Button dashLogoutBtn = rootView.findViewById(R.id.dash_logout_btn);
        dashLogoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);
            String signOutMsg = "You are now signed out.";
            Toast.makeText(this.getContext(), signOutMsg, Toast.LENGTH_SHORT).show();
        });

        RecyclerView recyclerView = rootView.findViewById(R.id.stockOwnedRecyclerView);
        this.recyclerViewAdapter = new StockRecyclerViewAdapter(this.stockItemList);
        recyclerView.setAdapter(recyclerViewAdapter);
        RecyclerView.LayoutManager recyclerLayoutManger = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(recyclerLayoutManger);
        IItemClickListener listener = (position, applicationContext) -> {
            stockItemList.get(position).onItemClick(position, this.getActivity());
            recyclerViewAdapter.notifyItemChanged(position);
        };
        recyclerViewAdapter.setOnItemClickListener(listener);

        return rootView;
    }

    private void updateRecyclerView(){
        int listSize = this.stockItemList.size();
        this.stockItemList.clear();
        this.recyclerViewAdapter.notifyItemRangeRemoved(0, listSize);

        // Ask the database to populate the list.
        Database.getDatabase().requestStockList();
    }

    @Override
    public void notifyMessage(String message) {
    }

    @Override
    public void notifyCashBalanceUpdate(double newCashBalance) {
        this.cashBalance.setText(String.format("$%.2f",newCashBalance));
    }

    @Override
    public void notifyShareCountUpdate(String ticker, double newSharesCount) {
    }

    @Override
    public void notifyStockList(String stock, double sharesOwned, int position) {
        this.stockItemList.add(new StockItemCard(stock, sharesOwned));
        recyclerViewAdapter.notifyItemInserted(position);
    }

    @Override
    public void notifyTradeHistory(String ticker, TradeHistory tradeHistory, int position) {
    }

    @Override
    public void notifyLogin(boolean r) {

    }

    @Override
    public void notifyPortfolioBalanceUpdated() {
        this.portfolioValue.setText("$" + Database.getDatabase().currentUserPortfolioBalance);
    }
}