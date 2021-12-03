package com.example.smartrade.webservices;

/**
 * An interface that dictates notifications and listeners for the database.
 */
public interface DatabaseListener {
    /**
     * Notifies the listener of a message to display to the user.
     * @param message   The message to send.
     */
    void notifyMessage(String message);

    /**
     * Notifies the listener of an update the current cash balance.
     * @param newCashBalance    The new cash balance.
     */
    void notifyCashBalanceUpdate(double newCashBalance);

    /**
     * Notifies the user of an update to the number of shares the user holds.
     * @param newSharesCount    The new share count.
     */
    void notifyShareCountUpdate(double newSharesCount);
}
