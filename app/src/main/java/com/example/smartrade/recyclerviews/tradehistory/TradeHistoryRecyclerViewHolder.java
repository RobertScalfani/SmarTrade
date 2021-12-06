package com.example.smartrade.recyclerviews.tradehistory;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrade.R;
import com.example.smartrade.recyclerviews.AbstractRecyclerViewHolder;
import com.example.smartrade.recyclerviews.IItemCard;
import com.example.smartrade.recyclerviews.IItemClickListener;

public class TradeHistoryRecyclerViewHolder extends AbstractRecyclerViewHolder {

    private final TextView ticker;
    private final TextView sharesTransacted;
    private final TextView pricePerShare;
    private final TextView tradeDate;
    private final TextView transactionType;

    public TradeHistoryRecyclerViewHolder(View itemView, IItemClickListener listener) {
        super(itemView);
        this.ticker = itemView.findViewById(R.id.ticker_history_card);
        this.sharesTransacted =  itemView.findViewById(R.id.shares_transacted_card);
        this.pricePerShare =  itemView.findViewById(R.id.cost_per_share_card);
        this.tradeDate =  itemView.findViewById(R.id.date_card);
        this.transactionType =  itemView.findViewById(R.id.transaction_type_card);

        itemView.setOnClickListener(v -> {
            if (listener != null) {
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, null);
                }
            }
        });
    }

    /**
     * Sets the text values of the holder.
     * @param currentItem The current item.
     */
    public void setHolderTextValues(IItemCard currentItem) {
        TradeHistoryItemCard currentTradeItem = (TradeHistoryItemCard) currentItem;
        this.ticker.setText("Ticker: " + currentTradeItem.ticker);
        this.sharesTransacted.setText("Shares Transacted: " + currentTradeItem.numberOfShares);
        this.pricePerShare.setText("Price per Share: " + currentTradeItem.pricePerShare);
        this.tradeDate.setText("Trade Date: " + currentTradeItem.tradeDate);
        this.transactionType.setText("Transaction type: " + currentTradeItem.transactionType);
    }
}