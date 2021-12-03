package com.example.smartrade;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

/**
 * Trading screen.
 */
public class MainActivity extends AppCompatActivity implements FinanceApiListener, DatabaseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * TODO
         * Things to add:
         * - Set up UI
         * -    - Main tabs
         */

        Button getPriceBtn = findViewById(R.id.get_price_btn);
        getPriceBtn.setOnClickListener(
                v -> {
                    this.updateTickerInfo();
                }
        );

        Button addMoneyBtn = findViewById(R.id.getMoney);
        addMoneyBtn.setOnClickListener(
                v -> {
                    this.addToCashBalance(10000);
                }
        );

        // Force-initialize the database.
        Database.initializeDatabase(this);

        // Buy/Sell input.
        EditText buySellEditText = findViewById(R.id.buySellEditText);
        buySellEditText.setHint("Shares to Buy/Sell");

        // Buy Button
        Button buyButton = findViewById(R.id.buyBtn);
        buyButton.setOnClickListener(
                v -> {
                    this.buyStock(this.getCurrentTicker(), this.getSharesToBuySell());
                }
        );

        // Sell Button
        Button sellButton = findViewById(R.id.sellBtn);
        sellButton.setOnClickListener(
                v -> {
                    this.sellStock(this.getCurrentTicker(), this.getSharesToBuySell());
                }
        );

        // START NAVIGATION ACTIVITIES
        // this is the trade page so we need..
        // - logout DONE
        // - dashboard
        // - leaderboard DONE

        // Logout Button
        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            String signOutMsg = "You are now signed out.";
            Toast.makeText(MainActivity.this, signOutMsg, Toast.LENGTH_SHORT).show();
        });

        // Dashboard Button
        Button tradeToDash = findViewById(R.id.trade_to_dashboard);
        tradeToDash.setOnClickListener(v -> {
            Intent intentDash = new Intent(this, Dashboard.class);
            startActivity(intentDash);
        });

        // Leaderboard Button
        Button tradeToLeaderboard = findViewById(R.id.trade_to_leaderboard);
        // END NAVIGATION ACTIVITIES

    }

    private void addToCashBalance(double moneyToAdd) {
        Database.getDatabase().addToCashBalance(moneyToAdd);
    }

    /**
     * Returns the number of shares to buy/sell as requested by the user.
     * @return
     */
    private double getSharesToBuySell() {
        EditText buySellEditText = findViewById(R.id.buySellEditText);
        Editable rawBuySellCount = buySellEditText.getText();
        if(rawBuySellCount == null) {
            Toast.makeText(MainActivity.this, "Please input the number of shares to buy/sell." ,Toast.LENGTH_SHORT).show();
            return 0.0;
        }
        return Double.parseDouble(buySellEditText.getText().toString());
    }

    /**
     * Returns the current ticker selected by the user.
     * @return
     */
    private String getCurrentTicker() {
        EditText enterTicker = findViewById(R.id.enter_ticker);
        String ticker = enterTicker.getText().toString();
        if(ticker.equals("")){
            Toast.makeText(MainActivity.this, "No ticker selected." ,Toast.LENGTH_SHORT).show();
        }
        return ticker.toUpperCase(Locale.ROOT);
    }

    /**
     * Sends a request to buy stock.
     */
    public void buyStock(String ticker, double sharesToBuy) {
        Toast.makeText(MainActivity.this, "Buy Initiated." ,Toast.LENGTH_SHORT).show();
        Database.getDatabase().buyStock(ticker, sharesToBuy);
    }

    /**
     * Sends a request to sell stock.
     */
    public void sellStock(String ticker, double sharesToSell) {
        Toast.makeText(MainActivity.this, "Sell Initiated." ,Toast.LENGTH_SHORT).show();
        try {
            Database.getDatabase().sellStock(ticker, sharesToSell);
        } catch (LoginException e) {
            Toast.makeText(MainActivity.this, "There was an issue getting your user information. " + e.toString() ,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Requests the price of the requested ticker.
     */
    private void updateTickerInfo() {
        String ticker = this.getCurrentTicker();
        try {
            Database.getDatabase().updateUserStockOwned(Database.getCurrentUser(), ticker);
        } catch (LoginException e) {
            Toast.makeText(MainActivity.this, "There was an issue getting your user information. " + e.toString() ,Toast.LENGTH_SHORT).show();
        }
        // Make ticker api request
        PingFinanceApiTask.callWebserviceButtonHandler(ticker, this);
    }

    /**
     * Listens for changes in the current ticker price. When it is notified of a price change, it updates the UI.
     */
    @Override
    public void notifyPriceUpdate(double price, String ticker) {
        // Update ticker.
        TextView displayTicker = findViewById(R.id.display_ticker);
        displayTicker.setText(ticker);
        // Update number of ticker owned.
        try {
            Database.getDatabase().updateUserStockOwned(Database.getCurrentUser(), ticker);
        } catch (LoginException e) {
            Toast.makeText(MainActivity.this, "There was an issue getting your user information. " + e.toString() ,Toast.LENGTH_SHORT).show();
        }
        // Update price.
        TextView displayPrice = findViewById(R.id.tickerPrice);
        displayPrice.setText("$" + price);
    }

    /**
     * Sends a toast with the given message.
     * @param message
     */
    @Override
    public void notifyMessage(String message) {
        Toast.makeText(MainActivity.this, message ,Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the cash balance displayed on screen.
     * @param newCashBalance
     */
    @Override
    public void notifyCashBalanceUpdate(double newCashBalance) {
        TextView cashBalanceMainActivity = findViewById(R.id.CashBalanceMain);
        cashBalanceMainActivity.setText("Cash Balance: " + newCashBalance);
    }

    @Override
    public void notifyShareCountUpdate(double newSharesCount) {
        TextView sharesOwnedText = findViewById(R.id.stockOwned);
        sharesOwnedText.setText("Shares Owned: " + newSharesCount);
    }
}