package com.example.smartrade;

import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A class that handles all database transactions.
 */
public class Database {

    private static final String STOCKS_OWNED = "STOCKS_OWNED";
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
     * Displays how much of the requested stock the user owns.
     * @param ticker The stock ticker to check for.
     * @param mainActivity
     */
    public static void updateUserStockOwned(FirebaseUser user, String ticker, MainActivity mainActivity) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference
                .child(user.getUid())
                .child(STOCKS_OWNED)
                .child(ticker)
                .get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                mainActivity.sendToast("Error updating user stock owned.");
            } else {
                // Update the number of shares owned by the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() != null) {
                    double sharesOwned = Double.parseDouble(result);
                    mainActivity.setSharesOwnedTo(sharesOwned);
                }
            }
        });
    }

    /**
     * Returns the firebase reference data for the ticker of the current user.
     * @param ticker the ticker to check for.
     * @return
     */
    private static DatabaseReference getUserTickerReference(FirebaseUser user, String ticker) {
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        return rootReference
                .child(user.getUid())
                .child(STOCKS_OWNED)
                .child(ticker);
    }

    /**
     * Sends a request to buy stock.
     */
    public static void buyStock(String ticker, double sharesToBuy,  MainActivity mainActivity) throws LoginException {

        if(sharesToBuy <= 0) {
            mainActivity.sendToast("Please enter a buy value greater than 0.");
            return;
        }
        if(ticker == null) {
            mainActivity.sendToast("Please enter a ticker to buy.");
            return;
        }

        FirebaseUser user = Database.getCurrentUser();

        DatabaseReference userTickerReference = Database.getUserTickerReference(user, ticker);
        // Update the current count for this sticker for this user.
        userTickerReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                mainActivity.sendToast("Buy Unsuccessful.");
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
                // Database Structure:
                userTickerReference.setValue(newSharesCount);
                Database.updateUserStockOwned(user, ticker, mainActivity);
            }
        });
    }

    /**
     * Sends a request to sell a stock.
     * @param ticker
     * @param sharesToSell
     */
    public static void sellStock(String ticker, double sharesToSell, MainActivity mainActivity) throws LoginException {

        if(sharesToSell <= 0) {
            mainActivity.sendToast("Please enter a sell value greater than 0.");
            return;
        }
        if(ticker == null) {
            mainActivity.sendToast("Please enter a ticker to sell.");
            return;
        }

        FirebaseUser user = Database.getCurrentUser();

        DatabaseReference userTickerReference = Database.getUserTickerReference(user, ticker);
        // Update the current count for this sticker for this user.
        userTickerReference.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                mainActivity.sendToast("Sell Unsuccessful.");
                return;
            } else {
                // Return the number of shares owned by the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() == null) {
                    mainActivity.sendToast("You do not own any shares of this stock.");
                    return;
                }
                double currentSharesOwned = Double.parseDouble(result);
                // Get the new stock count of the buy request.
                double newSharesCount = currentSharesOwned - sharesToSell;
                if (newSharesCount < 0.0) {
                    mainActivity.sendToast("You do not own enough shares of this stock. You have " + currentSharesOwned + " shares and tried to sell " + sharesToSell + ".");
                    return;
                }
                // Database Structure:
                userTickerReference.setValue(newSharesCount);
                Database.updateUserStockOwned(user, ticker, mainActivity);
            }
        });
    }

}
