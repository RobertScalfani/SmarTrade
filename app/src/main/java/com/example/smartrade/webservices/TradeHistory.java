package com.example.smartrade.webservices;

import java.io.Serializable;
import java.util.Date;

/**
 * A class that maintains a trade history snapshot for the database.
 */
public class TradeHistory implements Serializable {

    public final  String tradeDate;
    public final  TransactionType transactionType;
    public final  double numberOfShares;
    public final  double costPerShare;

    /**
     * Constructor.
     * @param tradeDate
     * @param transactionType
     * @param numberOfShares
     * @param costPerShare
     */
    public TradeHistory(Date tradeDate, TransactionType transactionType, double numberOfShares, double costPerShare){
        this.tradeDate = tradeDate.toString();
        this.transactionType = transactionType;
        this.numberOfShares = numberOfShares;
        this.costPerShare = costPerShare;
    }

    public String getDate() {
        return this.tradeDate;
    }

    public enum TransactionType {
        BUY,
        SELL
    }

}
