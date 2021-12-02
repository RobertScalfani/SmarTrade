package com.example.smartrade;

import static java.util.stream.Collectors.joining;

import android.os.AsyncTask;

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

public class PingFinanceApiTask extends AsyncTask<String, Integer, JSONObject> {

    private final static String API_KEY = "ZIWwEJPxnx3gCZVF9f6QKa6cH8MV7J4o4S5aQeSp";
    private FinanceApiListener listener;

    /**
     * Calls the web service with the given parameter.
     * @param param
     */
    public static void callWebserviceButtonHandler(String param, FinanceApiListener listener) {
        PingFinanceApiTask task = new PingFinanceApiTask(listener);
        task.execute(param);
    }

    public PingFinanceApiTask(FinanceApiListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        String loadingText = "Loading";
//        listener.sendToast(loadingText);
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
            listener.notifyPriceUpdate(price, ticker);
        } catch (JSONException e) {
//            mainActivity.sendToast("Something went wrong");
            e.printStackTrace();
        }
    }
}