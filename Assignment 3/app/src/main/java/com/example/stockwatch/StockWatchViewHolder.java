package com.example.stockwatch;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StockWatchViewHolder extends RecyclerView.ViewHolder
{
    public TextView stockName;
    public TextView stockSymbol;
    public TextView stockPrice;
    public TextView priceChange;
    public TextView percentChange;
    public ImageView arrow;

    public StockWatchViewHolder(@NonNull View itemView)
    {
        super(itemView);
        stockName = itemView.findViewById(R.id.stockName);
        stockSymbol = itemView.findViewById(R.id.stockSymbol);
        stockPrice = itemView.findViewById(R.id.stockPrice);
        priceChange = itemView.findViewById(R.id.priceChange);
        percentChange = itemView.findViewById(R.id.percentChange);
        arrow = itemView.findViewById(R.id.arrow);
    }
}
