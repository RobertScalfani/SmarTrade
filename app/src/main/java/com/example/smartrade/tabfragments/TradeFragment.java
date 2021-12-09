package com.example.smartrade.tabfragments;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.smartrade.R;
import com.example.smartrade.webservices.Database;
import com.example.smartrade.webservices.DatabaseListener;
import com.example.smartrade.webservices.FinanceApiListener;
import com.example.smartrade.webservices.PingFinanceApiTask;
import com.example.smartrade.webservices.TradeHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TradeFragment extends Fragment implements FinanceApiListener, DatabaseListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trade,container, false);

        Button stockInfoButton = rootView.findViewById(R.id.get_price_btn);
        stockInfoButton.setOnClickListener(
                v -> {this.updateTickerInfo();}
        );

        Button addMoneyBtn = rootView.findViewById(R.id.getMoney);
        addMoneyBtn.setOnClickListener(
                v -> {this.addToCashBalance(10000);}
        );

        // Force-initialize the database.
        Database.initializeDatabase(this);

        // Buy/Sell input.
        EditText buySellEditText = rootView.findViewById(R.id.buySellEditText);
        buySellEditText.setHint("Shares to Buy/Sell");

        // Buy Button
        Button buyButton = rootView.findViewById(R.id.buyBtn);
        buyButton.setOnClickListener(
                v -> {this.buyStock(this.getCurrentTicker(), this.getSharesToBuySell());}
        );

        // Sell Button
        Button sellButton = rootView.findViewById(R.id.sellBtn);
        sellButton.setOnClickListener(
                v -> {this.sellStock(this.getCurrentTicker(), this.getSharesToBuySell());}
        );

        // Logout Button
//        Button logoutBtn = rootView.findViewById(R.id.logoutBtn);
//        logoutBtn.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            // May be wrong...
//            Intent intent = new Intent(this.getContext(), LoginActivity.class);
//            startActivity(intent);
////            finish();
//            String signOutMsg = "You are now signed out.";
//            Toast.makeText(this.getContext(), signOutMsg, Toast.LENGTH_SHORT).show();
//        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Database.initializeDatabase(this);
        // Update the current Cash Balance.
        Database.getDatabase().addToCashBalance(0.0);
    }

    private void addToCashBalance(double moneyToAdd) {
        Database.getDatabase().addToCashBalance(moneyToAdd);
    }

    /**
     * Returns the number of shares to buy/sell as requested by the user.
     * @return
     */
    private double getSharesToBuySell() {
        EditText buySellEditText = getView().findViewById(R.id.buySellEditText);
        Editable rawBuySellCount = buySellEditText.getText();
        if(rawBuySellCount == null || rawBuySellCount.equals("")) {
            Toast.makeText(this.getContext(), "Please input the number of shares to buy/sell." ,Toast.LENGTH_SHORT).show();
            return 0.0;
        }
        double sharesToBuySell;
        try{
            sharesToBuySell = Double.parseDouble(buySellEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this.getContext(), "\'" + rawBuySellCount + "\' is not a valid number to buy or sell." ,Toast.LENGTH_SHORT).show();
            sharesToBuySell = 0.0;
        }
        return sharesToBuySell;
    }

    /**
     * Returns the current ticker selected by the user.
     * @return
     */
    private String getCurrentTicker() {
        EditText enterTicker = getView().findViewById(R.id.enter_ticker);
        String ticker = enterTicker.getText().toString();
        if(ticker.equals("")){
            Toast.makeText(this.getContext(), "No ticker selected." ,Toast.LENGTH_SHORT).show();
        }
        return ticker.toUpperCase(Locale.ROOT);
    }

    /**
     * Sends a request to buy stock.
     */
    public void buyStock(String ticker, double sharesToBuy) {
        Database.getDatabase().buyStock(ticker, sharesToBuy);
        Toast.makeText(this.getContext(), "Buy order confirmed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sends a request to sell stock.
     */
    public void sellStock(String ticker, double sharesToSell) {
        Toast.makeText(this.getContext(), "Selling shares...", Toast.LENGTH_SHORT).show();
        try {
            Database.getDatabase().sellStock(ticker, sharesToSell);
        } catch (Database.FirebaseAccessException e) {
            Toast.makeText(this.getContext(), "There was an issue getting your user information. " + e.toString() ,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Requests the price of the requested ticker.
     */
    private void updateTickerInfo() {
        String ticker = this.getCurrentTicker();
        Database.getDatabase().updateUserStockOwned(ticker);
        // Make ticker api request.
        List<String> tickerList = new ArrayList<>();
        tickerList.add(ticker);
        PingFinanceApiTask.callWebserviceButtonHandler(tickerList, this);
    }

    @Override
    public void notifyPriceUpdate(double price, String ticker, String longName) {
        // Update ticker.
        TextView displayTicker = getView().findViewById(R.id.display_ticker);
        displayTicker.setText(ticker);
        // Update number of shares of this ticker owned.
        Database.getDatabase().updateUserStockOwned(ticker);
        // Update price.
        TextView displayPrice = getView().findViewById(R.id.tickerPrice);
        displayPrice.setText("$" + price);

        TextView longNameDisplay = getView().findViewById(R.id.companyName);
        longNameDisplay.setText(longName);
    }

    @Override
    public void notifyMessage(String message) {
        Toast.makeText(this.getContext(), message ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void notifyDoneFetchingPrices() {

    }

    @Override
    public void notifyCashBalanceUpdate(double newCashBalance) {
        TextView cashBalanceMainActivity = getView().findViewById(R.id.CashBalanceMain);
        cashBalanceMainActivity.setText("Cash Balance: " + String.format("%.2f",newCashBalance));
    }

    @Override
    public void notifyShareCountUpdate(String ticker, double newSharesCount) {
        TextView sharesOwnedText = getView().findViewById(R.id.stockOwned);
        sharesOwnedText.setText("Shares Owned: " + newSharesCount);
    }

    @Override
    public void notifyStockList(String result, double sharesOwned, int position) {

    }

    @Override
    public void notifyTradeHistory(String ticker, TradeHistory tradeHistory, int position) {

    }

    @Override
    public String toString() {
        return "Trade";
    }

}
