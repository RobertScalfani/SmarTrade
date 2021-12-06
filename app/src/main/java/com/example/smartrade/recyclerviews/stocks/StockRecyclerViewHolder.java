package com.example.smartrade.recyclerviews.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrade.R;
import com.example.smartrade.recyclerviews.AbstractRecyclerViewHolder;
import com.example.smartrade.recyclerviews.IItemCard;
import com.example.smartrade.recyclerviews.IItemClickListener;

public class StockRecyclerViewHolder extends AbstractRecyclerViewHolder {

    private TextView ticker;
    private TextView sharesOwned;

    public StockRecyclerViewHolder(View itemView, IItemClickListener listener) {
        super(itemView);
        ticker = itemView.findViewById(R.id.ticker_card);
        sharesOwned = itemView.findViewById(R.id.shares_owned_card);

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
        StockItemCard currentUrlItem = (StockItemCard) currentItem;
        String ticker = "Ticker: " + currentUrlItem.getTicker();
        String sharesOwned = "Shares Owned: " + currentUrlItem.getSharesOwned();
        this.ticker.setText(ticker);
        this.sharesOwned.setText(sharesOwned);
    }
}