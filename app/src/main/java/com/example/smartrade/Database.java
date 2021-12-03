package com.example.smartrade;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A class that handles all database transactions.
 */
public class Database implements FinanceApiListener {

    private static final String STOCKS_OWNED = "STOCKS_OWNED";
    private static final String CASH_BALANCE = "CASH_BALANCE";
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // Tracks the current buy or sell status of the user.
    boolean buyingStock;

    // Bad coding practice, but all I can think of for now is a field to track the current shares to sell.
    private double sharesToSellTracker = 0.0;
    private double shareToBuyTracker = 0.0;
    private double cashBalanceTracker = 0.0;

    private static DatabaseListener databaseListener;

    // Database Singleton
    private static Database database;

    /**
     * Returns the database singleton.
     * @return
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
    private Database(){
        try {
            this.initializeCashBalance();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static void initializeDatabase(DatabaseListener databaseListener) {
        Database.databaseListener = databaseListener;
        Database.database = new Database();
    }

    private void initializeCashBalance() throws LoginException {
        DatabaseReference cashBalanceReference = Database.getUserCashBalanceReference(Database.getCurrentUser());
        cashBalanceReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error setting user's cash balance.", task.getException());
                databaseListener.notifyMessage("Sell Unsuccessful.");
                return;
            } else {
                // Returns the cash balance of the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() == null) {
                    result = "0.0";
                }
                double currentCashBalance = Double.parseDouble(result);
                // Update cash balance.
                cashBalanceReference.setValue(currentCashBalance);
                this.cashBalanceTracker = currentCashBalance;
                databaseListener.notifyCashBalanceUpdate(currentCashBalance);
            }
        });
    }

    /**
     * Returns the currently logged in Firebase user.
     * @return
     */
    static FirebaseUser getCurrentUser() throws LoginException {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            throw new LoginException("No user is logged in.");
        }
        return currentUser;
    }

    /**
     * Returns the user's database reference.
     * @param user
     * @return
     */
    private static DatabaseReference getUserReference(FirebaseUser user) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        return rootReference
                .child(user.getUid());
    }

    /**
     * Returns the user's cash balance database reference.
     * @param user
     * @return
     */
    private static DatabaseReference getUserCashBalanceReference(FirebaseUser user) {
        DatabaseReference userReference = Database.getUserReference(user);
        return userReference
                .child(CASH_BALANCE);
    }

    /**
     * Returns the firebase reference data for the ticker of the current user.
     * @param ticker the ticker to check for.
     * @return
     */
    private static DatabaseReference getUserTickerReference(FirebaseUser user, String ticker) {
        DatabaseReference userReference = Database.getUserReference(user);
        return userReference
                .child(STOCKS_OWNED)
                .child(ticker);
    }

    /**
     * Sends a request to buy stock.
     */
    public void buyStock(String ticker, double sharesToBuy) {
        buyingStock = true;
        this.shareToBuyTracker = sharesToBuy;

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
    public void buyStockBasedOnPrice(String ticker, double sharesToBuy,  double price) throws LoginException {

        // Check that the user can afford the transaction.
        if((price * sharesToBuy) > this.cashBalanceTracker ) {
            databaseListener.notifyMessage("You do not have a high enough cash balance. Tried to spend " + price*sharesToBuy + " with a balance of " + this.cashBalanceTracker + ".");
            return;
        }

        FirebaseUser user = Database.getCurrentUser();

        DatabaseReference userTickerReference = Database.getUserTickerReference(user, ticker);
        // Update the current count for this sticker for this user.
        userTickerReference.get().addOnCompleteListener(task -> {
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
                // Get the new stock count of the buy request.
                double newSharesCount = currentSharesOwned + sharesToBuy;
                // Set the new shares count.
                userTickerReference.setValue(newSharesCount);
                databaseListener.notifyShareCountUpdate(newSharesCount);
                // Update the user's cash balance.
                this.decreaseUserCashBalance(user, (sharesToBuy * price));
            }
        });
    }


    /**
     * Sends a request to sell a stock.
     * @param ticker
     * @param sharesToSell
     */
    public void sellStock(String ticker, double sharesToSell) throws LoginException {
        buyingStock = false;

        if(sharesToSell <= 0) {
            databaseListener.notifyMessage("Please enter a sell value greater than 0.");
            return;
        }
        if(ticker == null) {
            databaseListener.notifyMessage("Please enter a ticker to sell.");
            return;
        }

        FirebaseUser user = Database.getCurrentUser();

        DatabaseReference userTickerReference = Database.getUserTickerReference(user, ticker);
        // Update the current count for this sticker for this user.
        userTickerReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                databaseListener.notifyMessage("Sell Unsuccessful.");
                return;
            } else {
                // Return the number of shares owned by the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() == null) {
                    databaseListener.notifyMessage("You do not own any shares of this stock.");
                    return;
                }
                double currentSharesOwned = Double.parseDouble(result);
                // Get the new stock count of the buy request.
                double newSharesCount = currentSharesOwned - sharesToSell;
                if (newSharesCount < 0.0) {
                    databaseListener.notifyMessage("You do not own enough shares of this stock. You have " + currentSharesOwned + " shares and tried to sell " + sharesToSell + ".");
                    return;
                }
                // Update shares count.
                userTickerReference.setValue(newSharesCount);
                databaseListener.notifyShareCountUpdate(newSharesCount);
                // Ping the API to update cash balance.
                this.sharesToSellTracker = sharesToSell;
                PingFinanceApiTask.callWebserviceButtonHandler(ticker, this);
            }
        });
    }

    /**
     * Displays how much of the requested stock the user owns.
     * @param ticker The stock ticker to check for.
     */
    public void updateUserStockOwned(FirebaseUser user, String ticker) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference
                .child(user.getUid())
                .child(STOCKS_OWNED)
                .child(ticker)
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
     * @param user
     * @param increaseAmount
     */
    private void increaseUserCashBalance(FirebaseUser user, double increaseAmount) {
        DatabaseReference cashBalanceReference = Database.getUserCashBalanceReference(user);
        cashBalanceReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error setting user's cash balance.", task.getException());
                databaseListener.notifyMessage("Sell Unsuccessful.");
                return;
            } else {
                // Returns the cash balance of the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() == null) {
                    result = "0.0";
                }
                double currentCashBalance = Double.parseDouble(result);
                // Get the new stock count of the buy request.
                double newCashBalance = currentCashBalance + increaseAmount;
                // Update cash balance.
                cashBalanceReference.setValue(newCashBalance);
                this.cashBalanceTracker = currentCashBalance;
                databaseListener.notifyCashBalanceUpdate(newCashBalance);
            }
        });
    }

    /**
     * Increases the current user's cash balance by the given amount.
     * @param user
     * @param decreaseAmount
     */
    private void decreaseUserCashBalance(FirebaseUser user, double decreaseAmount) {
        DatabaseReference cashBalanceReference = Database.getUserCashBalanceReference(user);
        cashBalanceReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error setting user's cash balance.", task.getException());
                databaseListener.notifyMessage("Sell Unsuccessful.");
                return;
            } else {
                // Returns the cash balance of the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() == null) {
                    result = "0.0";
                }
                double currentCashBalance = Double.parseDouble(result);
                // Get the new stock count of the buy request.
                double newCashBalance = currentCashBalance - decreaseAmount;
                // Update cash balance.
                cashBalanceReference.setValue(newCashBalance);
                this.cashBalanceTracker = currentCashBalance;
                databaseListener.notifyCashBalanceUpdate(newCashBalance);
            }
        });
    }

    @Override
    public void notifyPriceUpdate(double price, String ticker) {

        if(buyingStock){
            try {
                this.buyStockBasedOnPrice(ticker, this.shareToBuyTracker, price);
            } catch (LoginException e) {
                // Login issue.
                e.printStackTrace();
            }
        } else {
            double valueOfSell = price * this.sharesToSellTracker;
            try {
                this.increaseUserCashBalance(Database.getCurrentUser(), valueOfSell);
            } catch (LoginException e) {
                // Login issue.
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds the given value to the user's cash balance.
     * @param moneyToAdd
     */
    public void addToCashBalance(double moneyToAdd) {
        try {
            this.increaseUserCashBalance(Database.getCurrentUser(), moneyToAdd);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
