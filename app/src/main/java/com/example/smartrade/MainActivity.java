package com.example.smartrade;

import static java.util.stream.Collectors.joining;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    private static final String STOCKS_OWNED = "STOCKS_OWNED";
    private FirebaseAuth mAuth;
    private final static String API_KEY = "ZIWwEJPxnx3gCZVF9f6QKa6cH8MV7J4o4S5aQeSp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        Log.i("FIREBASE", "Initialized");

        /**
         * TODO
         * Things to add:
         * - Login screen (get firebase working with it)
         * - Decide firebase object structure
         * - Set up UI
         * -    - Main tabs
         */

        Button getPriceBtn = findViewById(R.id.get_price_btn);
        getPriceBtn.setOnClickListener(
                v -> {
                    this.updateTickerInfo();
                }
        );

        // Buy Button
        Button buyButton = findViewById(R.id.buyBtn);
        buyButton.setOnClickListener(
                v -> {
                    this.buyStock(this.getCurrentTicker(), 3.2);
                }
        );

        // Sell Button
        Button sellButton = findViewById(R.id.sellBtn);
        sellButton.setOnClickListener(
                v -> {
                    this.sellStock(this.getCurrentTicker(), 2);
                }
        );

    }

    /**
     * Returns the current ticker selected by the user.
     * @return
     */
    private String getCurrentTicker() {
        EditText enterTicker = findViewById(R.id.enter_ticker);
        String ticker = enterTicker.getText().toString();
        if(ticker == null){
            Toast.makeText(MainActivity.this, "No ticker selected." ,Toast.LENGTH_SHORT).show();
        }
        return ticker;
    }

    /**
     * Returns the currently logged in Firebase user.
     * @return
     */
    private FirebaseUser getUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Toast.makeText(MainActivity.this, "No user is logged in." ,Toast.LENGTH_SHORT).show();
            throw new RuntimeException("User is not logged in.");
        }
        return currentUser;
    }

    /**
     * Sends a request to buy stock.
     */
    public void buyStock(String ticker, double sharesToBuy) {
        Toast.makeText(MainActivity.this, "Buy Initiated." ,Toast.LENGTH_SHORT).show();

        if(ticker == null) {
            Toast.makeText(getApplicationContext(), "Please enter a ticker to buy.", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
            // Update the current count for this sticker for this user.
            rootReference
                    .child(this.getUser().getUid())
                    .child(STOCKS_OWNED)
                    .child(ticker)
                    .get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Buy Unsuccessful." ,Toast.LENGTH_SHORT).show();
                    Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
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
                    rootReference
                            .child(this.getUser().getUid())
                            .child(STOCKS_OWNED)
                            .child(ticker)
                            .setValue(newSharesCount);
                    this.updateUserStockOwned(ticker);
                }
            });
        }
    }

    /**
     * Sends a request to sell stock.
     */
    public void sellStock(String ticker, double sharesToSell) {
        Toast.makeText(MainActivity.this, "Sell Initiated." ,Toast.LENGTH_SHORT).show();

        if(ticker == null) {
            Toast.makeText(getApplicationContext(), "Please enter a ticker to buy.", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
            // Update the current count for this sticker for this user.
            rootReference
                    .child(this.getUser().getUid())
                    .child(STOCKS_OWNED)
                    .child(ticker)
                    .get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Sell Unsuccessful." ,Toast.LENGTH_SHORT).show();
                    Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
                } else {
                    // Return the number of shares owned by the user.
                    String result = String.valueOf(task.getResult().getValue());
                    if (task.getResult().getValue() == null) {
                        Toast.makeText(MainActivity.this, "You do not own any shares of this stock." ,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double currentSharesOwned = Double.parseDouble(result);
                    if (currentSharesOwned <= 0.0) {
                        Toast.makeText(MainActivity.this, "You do not own any shares of this stock." ,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Get the new stock count of the buy request.
                    double newSharesCount = currentSharesOwned - sharesToSell;
                    // Database Structure:
                    rootReference
                            .child(this.getUser().getUid())
                            .child(STOCKS_OWNED)
                            .child(ticker)
                            .setValue(newSharesCount);
                    this.updateUserStockOwned(ticker);
                }
            });
        }
    }

    /**
     * Requests the price of the requested ticker.
     */
    private void updateTickerInfo() {
        String ticker = this.getCurrentTicker();
        System.out.println("Requested Ticker: " + ticker);
        this.updateUserStockOwned(ticker);
        // Make ticker api request
        callWebserviceButtonHandler(ticker);
    }

    /**
     * Listens for changes in the current ticker price. When it is notified of a price change, it updates the UI.
     */
    private void notifyPriceUpdate(double price, String ticker) {

        // Update ticker.
        TextView displayTicker = findViewById(R.id.display_ticker);
        displayTicker.setText(ticker);
        // Update number of ticker owned.
        this.updateUserStockOwned(ticker);
        // Update price.
        TextView displayPrice = findViewById(R.id.tickerPrice);
        displayPrice.setText("$" + price);

        Toast.makeText(MainActivity.this, "Price: " + price ,Toast.LENGTH_SHORT).show();

    }

    /**
     * Displays how much of the requested stock the user owns.
     * @param ticker The stock ticker to check for.
     */
    private void updateUserStockOwned(String ticker) {
        Toast.makeText(MainActivity.this, "Updating user stock owned." ,Toast.LENGTH_SHORT).show();
        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference
                .child(this.getUser().getUid())
                .child(STOCKS_OWNED)
                .child(ticker)
                .get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Error updating user stock owned." ,Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE", "Error getting user stock owned data", task.getException());
            } else {
                // Update the number of shares owned by the user.
                String result = String.valueOf(task.getResult().getValue());
                if (task.getResult().getValue() != null) {
                    double sharesOwned = Double.parseDouble(result);
                    TextView sharesOwnedText = findViewById(R.id.stockOwned);
                    sharesOwnedText.setText("Shares Owned: " + String.valueOf(sharesOwned));
                    Toast.makeText(MainActivity.this, "Updated user stock owned: " + sharesOwned ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Calls the web service with the given parameter.
     * @param param
     */
    public void callWebserviceButtonHandler(String param) {
        PingWebServiceTask task = new PingWebServiceTask();
        task.execute(param);
    }

    /**
     * Inner class to ping webservice without blocking the main thread.
     */
    private class PingWebServiceTask  extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            String loadingText = "Loading";
            Toast.makeText(MainActivity.this, loadingText,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected JSONObject doInBackground(String... tickerParams) {
            JSONObject jObject = new JSONObject();
            try {
                // DOCUMENTATION: https://www.yahoofinanceapi.com/
                // https://api.polygon.io/vX/reference/tickers/AAPL?apiKey=V1aOJf2eMgIpMCt9z495xd9VJHKyPO58
                // https://yfapi.net/v6/finance/quote?region=US&lang=en&symbols=AAPL%2CBTC-USD%2CEURUSD%3DX

                String request = "https://yfapi.net/v6/finance/quote?region=US&lang=en&symbols=" + tickerParams[0].toUpperCase() + "%2CBTC-USD%2CEURUSD%3DX";
                String resp = this.getHttpResponse(request);
                jObject = new JSONObject(resp);
                return jObject;
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return jObject;
        }

        /**
         * Gets the HTTP response for the given string.
         * @param urlString The URL string.
         * @return
         * @throws IOException
         */
        private String getHttpResponse(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("x-api-key", API_KEY);
            httpConnection.setDoInput(true);
            httpConnection.connect();
            return this.convertInputStreamToString(httpConnection.getInputStream());
        }

        /**
         * Converts an input stream to a String.
         * @param inputStream the input stream to convert.
         * @return
         * @throws IOException
         */
        private String convertInputStreamToString(InputStream inputStream) {
            String newLineChar = System.getProperty("line.separator");
            try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
                return lines.collect(joining(newLineChar));
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            System.out.println(jsonObject);
            try {
                // Parse JSON response.
                JSONObject quoteResponse = jsonObject.getJSONObject("quoteResponse");
                JSONArray results = quoteResponse.getJSONArray("result");
                JSONObject result = (JSONObject) results.get(0);
                double price = result.getDouble("ask");
                String ticker = result.getString("symbol");
                // Notify the activity of the new values.
                MainActivity.this.notifyPriceUpdate(price, ticker);
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "Something went wrong" ,Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}