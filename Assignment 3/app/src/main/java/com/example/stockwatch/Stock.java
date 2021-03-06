package com.example.stockwatch;

import java.io.Serializable;

public class Stock implements Serializable
{
    private String stockSymbol;
    private String stockName;
    private double stockPrice;
    private double priceChange;
    private double percentChange;

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public double getPriceChange() {
        return priceChange;
    }

    public void setPriceChange(double priceChange) {
        this.priceChange = priceChange;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean flag = false;
        if (obj == null || obj.getClass() != getClass())
        {
            flag = false;
        }
        else
            {
            Stock stk = (Stock) obj;
            if (this.stockSymbol.equals(stk.stockSymbol))
            {
                flag = true;
            }
        }
        return flag;
    }
}
