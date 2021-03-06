package com.example.stockwatch;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

public class StockWatchAdapter extends RecyclerView.Adapter<StockWatchViewHolder>
{
    private List<Stock> stockList;
    private MainActivity mActivity ;

    public StockWatchAdapter(List<Stock> stockList, MainActivity mainActivity)
    {
        this.stockList = stockList;
        mActivity = mainActivity;
    }

    @NonNull
    @Override
    public StockWatchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_list,viewGroup,false);
        view.setOnClickListener(mActivity);
        view.setOnLongClickListener(mActivity);
        return new StockWatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockWatchViewHolder stockWatchViewHolder, int position)
    {
        Stock stock = stockList.get(position);
        stockWatchViewHolder.stockName.setText(stock.getStockName());
        stockWatchViewHolder.stockSymbol.setText(stock.getStockSymbol());
        stockWatchViewHolder.stockPrice.setText(String.format(Locale.US, "%.2f", stock.getStockPrice()));
        stockWatchViewHolder.priceChange.setText(String.format(Locale.US, "%.2f", stock.getPriceChange()));
        stockWatchViewHolder.percentChange.setText(String.format(Locale.US, "(%.2f%%)", stock.getPercentChange()));

        if (stock.getPriceChange() < 0)
        {
            stockWatchViewHolder.stockName.setTextColor(Color.RED);
            stockWatchViewHolder.stockSymbol.setTextColor(Color.RED);
            stockWatchViewHolder.stockPrice.setTextColor(Color.RED);
            stockWatchViewHolder.priceChange.setTextColor(Color.RED);
            stockWatchViewHolder.percentChange.setTextColor(Color.RED);
            stockWatchViewHolder.arrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
            stockWatchViewHolder.arrow.setColorFilter(Color.RED);
        }
        else
        {
            stockWatchViewHolder.stockName.setTextColor(Color.GREEN);
            stockWatchViewHolder.stockSymbol.setTextColor(Color.GREEN);
            stockWatchViewHolder.stockPrice.setTextColor(Color.GREEN);
            stockWatchViewHolder.priceChange.setTextColor(Color.GREEN);
            stockWatchViewHolder.percentChange.setTextColor(Color.GREEN);
            stockWatchViewHolder.arrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
            stockWatchViewHolder.arrow.setColorFilter(Color.GREEN);
        }
    }

    @Override
    public int getItemCount()
    {
        return stockList.size();
    }
}
