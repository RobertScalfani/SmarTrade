package com.example.smartrade;

/**
 * An interface that dictates notifications and listeners for the database.
 */
public interface DatabaseListener {

    void notifyMessage(String message);
    void notifyCashBalanceUpdate(double newCashBalance);
    void notifyShareCountUpdate(double newSharesCount);
}
