package com.example.smartrade.webservices;

/**
 * A listener interface that gets notified of updates of requests by the PingFinanceApiTask class.
 */
public interface FinanceApiListener {

    /**
     * Notifies the listener of an update to the price of the given ticker.
     * @param price The new price.
     * @param ticker    The ticker the price is for.
     */
    void notifyPriceUpdate(double price, String ticker);

    /**
     * Notifies the listener of a message to display to the user.
     * @param s The message.
     */
    void notifyMessage(String s);


}
