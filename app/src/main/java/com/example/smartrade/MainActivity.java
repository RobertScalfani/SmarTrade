package com.example.smartrade;

import static java.util.stream.Collectors.joining;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    private final static String API_KEY = "ZIWwEJPxnx3gCZVF9f6QKa6cH8MV7J4o4S5aQeSp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    this.getTickerPrice();
                }
        );

    }

    private void getTickerPrice() {

        EditText enterTicker = findViewById(R.id.enter_ticker);
        String ticker = enterTicker.getText().toString();
        System.out.println("Requested Ticker: " + ticker);

        // Make ticker api request
        callWebserviceButtonHandler(ticker);

        // Output result to textview.
        TextView displayPrice = findViewById(R.id.display_ticker);
        displayPrice.setText(ticker);
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
                // format:
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
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            String newLineChar = System.getProperty("line.separator");
            try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
                return lines.collect(joining(newLineChar));
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            System.out.println(jsonObject);
            // Extract market Cap
            try {
                JSONObject quoteResponse = jsonObject.getJSONObject("quoteResponse");
                JSONArray results = quoteResponse.getJSONArray("result");
                JSONObject result = (JSONObject) results.get(0);
                double price = result.getDouble("ask");
                Toast.makeText(MainActivity.this, "Price: " + price ,Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "Something went wrong " ,Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}