package com.example.smartrade.webservices;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

/**
 * A class that handles all database transactions.
 */
public class Database implements FinanceApiListener {

    private static final String STOCK_TICKERS = "STOCK_TICKERS";
    private static final String SHARES_OWNED = "SHARES_OWNED";
    private static final String CASH_BALANCE = "CASH_BALANCE";
    private static final String TRADE_HISTORY = "TRADE_HISTORY";

    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Database Singleton
    private static Database database;

    // The listener that should be notified of updates to database requests.
    private static DatabaseListener databaseListener;

    // Probably bad coding practice, but all I can think of for now is fields to track these attributes. It works.
    private double sharesToSellTracker = 0.0;
    private double sharesToBuyTracker = 0.0;
    private double cashBalanceTracker = 0.0;
    // Tracks the current buy or sell status of the user.
    TradeHistory.TransactionType transactionType;

    /**
     * Initializes the database with the give database listener.
     * @param databaseListener  The listener that the database should send updates to.
     */
    public static void initializeDatabase(DatabaseListener databaseListener) {
        Database.databaseListener = databaseListener;
        Database.database = new Database();
    }

    /**
     * Returns the database singleton.
     * @return  An instance of the database singleton.
     */
    public static Database getDatabase(){
        if(database == null){
            throw new RuntimeException("Tried to use a non-initialized database.");
        }
        return database;
    }

    /**
     * Private constructor to enforce the database singleton.
     */
    private Database() {
        try {
            // Force the current tracked cash balance to update.
            this.changeUserCashBalance(this.getCurrentUser(), 0.0);
        } catch (FirebaseAccessException e) {
            Database.databaseListener.notifyMessage("There was an issue getting your user information.");
            e.printStackTrace();
        }
    }

    /**
     * Returns the currently logged in Firebase user.
     * @return  The current firebase user.
     */
    public FirebaseUser getCurrentUser() throws FirebaseAccessException {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            throw new FirebaseAccessException("No user is logged in.");
        }
        return currentUser;
    }

    /**
     * Returns the user's database reference.
     * @param user  The user to get the reference for.
     * @return  The user's database reference.
     */
    private static DatabaseReference getUserReference(FirebaseUser user) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        return rootReference.child(user.getUid());
    }

    /**
     * Returns the user's cash balance database reference.
     * @param user The user to get the reference for.
     * @return  The user's cash balance database reference.
     */
    private static DatabaseReference getUserCashBalanceReference(FirebaseUser user) {
        DatabaseReference userReference = Database.getUserReference(user);
        return userReference
                .child(CASH_BALANCE);
    }

    /**
     * Returns the firebase reference data for the ticker of the current user.
     * @param ticker the ticker to get the database reference for.
     * @return The user's ticker database reference.
     */
    private static DatabaseReference getUserTickerReference(FirebaseUser user, String ticker) {
        DatabaseReference userReference = Database.getUserReference(user);
        return userReference
                .child(STOCK_TICKERS)
                .child(ticker);
    }

    /**
     * Sends a request to buy stock.
     */
    public void buyStock(String ticker, double sharesToBuy) {
        transactionType = TradeHistory.TransactionType.BUY;
        this.sharesToBuyTracker = sharesToBuy;

        if(sharesToBuy <= 0) {
            databaseListener.notifyMessage("Please enter a buy value greater than 0.");
            return;
        }
        if(ticker == null) {
            databaseListener.notifyMessage("Please enter a ticker to buy.");
            return;
        }

        // Make the request for the price.
        PingFinanceApiTask.callWebserviceButtonHandler(ticker, this);
    }

    /**
     * Buys stock based on a price.
     */
    public void buyStockBasedOnPrice(String ticker, double sharesToBuy,  double price) throws FirebaseAccessException {

        // Check that the user can afford the transaction.
        if((price * sharesToBuy) > this.cashBalanceTracker ) {
            databaseListener.notifyMessage("You do not have a high enough cash balance. Tried to spend " + price*sharesToBuy + " with a balance of " + this.cashBalanceTracker + ".");
            return;
        }

        FirebaseUser user = this.getCurrentUser();

        DatabaseReference userTickerSharesOwnedReference = Database.getUserTickerReference(user, ticker).child(SHARES_OWNED);
        // Update the current count for this sticker for this user.
        userTickerSharesOwnedReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                databaseListener.notifyMessage("Buy Unsuccessful.");
                return;
            } else {
                // Return the number of shares owned by the user.
                String result = String.valueOf(task.getResult().getValue());
                double currentSharesOwned = 0.0;
                if (task.getResult().getValue() != null) {
                    currentSharesOwned = Double.parseDouble(result);
                }

                // Add transaction to the trade history.
                TradeHistory newTrade = new TradeHistory(new Date(), TradeHistory.TransactionType.BUY, sharesToBuy, price);
                this.addTradeHistory(user, ticker, newTrade);
                // Get the new stock count of the buy request.
                double newSharesCount = currentSharesOwned + sharesToBuy;
                // Set the new shares count.
                userTickerSharesOwnedReference.setValue(newSharesCount);
                databaseListener.notifyShareCountUpdate(newSharesCount);
                // Update the user's cash balance.
                this.decreaseUserCashBalance(user, (sharesToBuy * price));
            }
        });
    }

    /**
     * Sends a request to sell a stock.
     * @param ticker    The ticker to sell.
     * @param sharesToSell  The number of shares to sell.
     */
    public void sellStock(String ticker, double sharesToSell) throws FirebaseAccessException {
        transactionType = TradeHistory.TransactionType.SELL;
        this.sharesToSellTracker = sharesToSell;

        if(sharesToSell <= 0) {
            databaseListener.notifyMessage("Please enter a sell value greater than 0.");
            return;
        }
        if(ticker == null) {
            databaseListener.notifyMessage("Please enter a ticker to sell.");
            return;
        }

        PingFinanceApiTask.callWebserviceButtonHandler(ticker, this);
    }

    /**
     * Sends a request to sell a stock.
     * @param ticker
     * @param sharesToSell
     */
    public void sellStockBasedOnPrice(String ticker, double sharesToSell, double price) throws FirebaseAccessException {

        FirebaseUser user = this.getCurrentUser();

        DatabaseReference userTickerSharesOwnedReference = Database.getUserTickerReference(user, ticker).child(SHARES_OWNED);
        // Update the current count for this sticker for this user.
        userTickerSharesOwnedReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                databaseListener.notifyMessage("Sell Unsuccessful.");
                return;
            } else {
                // Return the number of shares owned by the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() == null) {
                    databaseListener.notifyMessage("You do not own any shares of " + ticker + " stock.");
                    return;
                }
                double currentSharesOwned = Double.parseDouble(result);
                // Get the new stock count of the buy request.
                double newSharesCount = currentSharesOwned - sharesToSell;
                if (newSharesCount < 0.0) {
                    databaseListener.notifyMessage("You do not own enough shares of this stock. You have " + currentSharesOwned + " shares and tried to sell " + sharesToSell + ".");
                    return;
                }
                // Sell stock and increase the user's cash balance.
                double valueOfSell = price * this.sharesToSellTracker;
                this.increaseUserCashBalance(user, valueOfSell);
                // Update shares count.
                userTickerSharesOwnedReference.setValue(newSharesCount);
                Database.databaseListener.notifyShareCountUpdate(newSharesCount);
                // Add transaction to trade history.
                TradeHistory newTrade = new TradeHistory(new Date(), TradeHistory.TransactionType.SELL, sharesToSell, price);
                this.addTradeHistory(user, ticker, newTrade);
            }
        });
    }

    /**
     * Adds the given trade history to the given ticker for the current user.
     * @param ticker    The stock ticker to add the trade history to.
     * @param newTrade  The new Trade transaction object.
     */
    private void addTradeHistory(FirebaseUser user, String ticker, TradeHistory newTrade) {
        DatabaseReference userTickerTradeHistoryReference = Database.getUserTickerReference(user, ticker).child(TRADE_HISTORY);
        userTickerTradeHistoryReference.child(newTrade.getDate()).setValue(newTrade);
    }

    /**
     * Displays how much of the requested stock the user owns.
     * @param ticker The stock ticker to check for.
     */
    public void updateUserStockOwned(String ticker) {

        FirebaseUser user = null;
        try {
            user = this.getCurrentUser();
        } catch (FirebaseAccessException e) {
            databaseListener.notifyMessage("There was an issue updating the user's stock owned.");
            return;
        }

        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference
                .child(user.getUid())
                .child(ticker)
                .child(SHARES_OWNED)
                .get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                databaseListener.notifyMessage("Error updating user stock owned.");
            } else {
                // Update the number of shares owned by the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() != null) {
                    double sharesOwned = Double.parseDouble(result);
                    databaseListener.notifyShareCountUpdate(sharesOwned);
                }
            }
        });
    }

    /**
     * Increases the current user's cash balance by the given amount.
     * @param user  The user to check for.
     * @param increaseAmount    The amount to increase the user's cash balance by.
     */
    private void increaseUserCashBalance(FirebaseUser user, double increaseAmount) {
        this.changeUserCashBalance(user, increaseAmount);
    }

    /**
     * Increases the current user's cash balance by the given amount. Should use a positive input.
     * @param user  The user to check for.
     * @param decreaseAmount    The amount to decrease the user's cash balance by. Should be positive.
     */
    private void decreaseUserCashBalance(FirebaseUser user, double decreaseAmount) {
        this.changeUserCashBalance(user, -decreaseAmount);
    }

    /**
     * Changes the user's cash balance by the given amount. May be positive or negative.
     * @param user  The user to check for.
     * @param changeAmount    The amount to increase/decrease the user's cash balance by. May be positive or negative.
     */
    private void changeUserCashBalance(FirebaseUser user, double changeAmount) {
        DatabaseReference cashBalanceReference = Database.getUserCashBalanceReference(user);
        cashBalanceReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error updating user's cash balance.", task.getException());
                Database.databaseListener.notifyMessage("Cash Balance Decrease Unsuccessful.");
                return;
            } else {
                // Returns the cash balance of the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() == null) {
                    result = "0.0";
                }
                double currentCashBalance = Double.parseDouble(result);
                // Get the new stock count of the buy request.
                double newCashBalance = currentCashBalance + changeAmount;
                // Update cash balance.
                cashBalanceReference.setValue(newCashBalance);
                this.cashBalanceTracker = currentCashBalance;
                Database.databaseListener.notifyCashBalanceUpdate(newCashBalance);
            }
        });
    }

    @Override
    public void notifyPriceUpdate(double price, String ticker) {
        try {
            if (transactionType.equals(TradeHistory.TransactionType.BUY)) {
                this.buyStockBasedOnPrice(ticker, this.sharesToBuyTracker, price);
            } else {
                this.sellStockBasedOnPrice(ticker, this.sharesToSellTracker, price);
            }
        } catch(FirebaseAccessException e){
            Database.databaseListener.notifyMessage("There was an issue getting your user information for the trade request.");
        }
    }

    @Override
    public void notifyMessage(String s) {
        Database.databaseListener.notifyMessage(s);
    }

    /**
     * Adds the given value to the user's cash balance.
     * @param moneyToAdd    The amount of money to add to the cash balance.
     */
    public void addToCashBalance(double moneyToAdd) {
        try {
            this.increaseUserCashBalance(this.getCurrentUser(), moneyToAdd);
        } catch (FirebaseAccessException e) {
            Database.databaseListener.notifyMessage("There was an issue getting your user information.");
        }
    }

    public class FirebaseAccessException extends Exception {
        public FirebaseAccessException(String s) {
            super(s);
        }
    }
}
