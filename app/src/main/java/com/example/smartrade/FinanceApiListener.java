package com.example.smartrade;

/**
 * A listener that gets notified of updates of requests by the PingFinanceApiTask class.
 */
public interface FinanceApiListener {

    void notifyPriceUpdate(double price, String ticker);

}
