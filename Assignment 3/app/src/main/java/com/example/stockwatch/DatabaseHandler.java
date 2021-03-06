package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final String TAG = "DatabaseHandler";
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";

   private static final String TABLECREATE = "CREATE TABLE " + TABLE_NAME + " (" + SYMBOL + " TEXT not null unique," + COMPANY + " TEXT not null)";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(TABLECREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void addStock(Stock stock)
    {
        Log.d(TAG, "addStock: Adding " + stock.getStockSymbol());
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getStockSymbol());
        values.put(COMPANY, stock.getStockName());
        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: Add Complete");
    }

    public void deleteStock(String symbol)
    {
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{symbol});
        Log.d(TAG, "deleteStock: " + cnt);
    }

    public ArrayList<Stock> loadStocks()
    {
        ArrayList<Stock> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, // The table to query
                new String[]{SYMBOL, COMPANY}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null,   // don't filter by row groups
                null );  // The sort order

        if (cursor != null)
        {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                Stock st = new Stock();
                st.setStockSymbol(symbol);
                st.setStockName(company);
                st.setStockPrice(0.0);
                st.setPriceChange(0.0);
                st.setPercentChange(0.0);
                stocks.add(st);

                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void shutDown()
    {
        database.close();
    }
}
