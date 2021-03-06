package com.example.stockwatch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener
{
    private static final String TAG = "MainActivity";
    List<Stock> stockList = new ArrayList<>();
    HashMap<String, String> hMap;
    RecyclerView recyclerView;
    StockWatchAdapter stockWatchAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        stockWatchAdapter = new StockWatchAdapter(stockList, this);
        Log.d(TAG, "onCreate: " + stockWatchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(stockWatchAdapter);
        
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!networkConnectivity()) {
                    swipeRefreshLayout.setRefreshing(false);
                    refreshAlert();
                } else {
                    doRefresh();
                }
            }
        });

        dbHandler = new DatabaseHandler(this);
        new AsynTaskCompany(MainActivity.this).execute();
        ArrayList<Stock> sList = dbHandler.loadStocks();
        if (!networkConnectivity()) {
            stockList.addAll(sList);
            Collections.sort(stockList, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                }
            });
            stockWatchAdapter.notifyDataSetChanged();
        } else {
            for (int i = 0; i < sList.size(); i++) {
                String symbol = sList.get(i).getStockSymbol();
                new AsyncTaskStock(MainActivity.this).execute(symbol);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: start");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu: end");
        return true;

    }

    @Override
    public void onClick(View v) {
        int i = recyclerView.getChildLayoutPosition(v);
        String marketPlaceURL = "http://www.marketwatch.com/investing/stock/" + stockList.get(i).getStockSymbol();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(marketPlaceURL));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int id = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_delete_black_24dp);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + ((TextView) v.findViewById(R.id.stockSymbol)).getText().toString() + "?");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHandler.deleteStock(stockList.get(id).getStockSymbol());
                stockList.remove(id);
                stockWatchAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }


    public void setData(HashMap<String, String> hashMap) {
        if (hashMap != null && !hashMap.isEmpty()) {
            this.hMap = hashMap;
        }
    }


    private void refreshAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stocks cannot be updated without a network connection");
        builder.setTitle("No Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + stockList.size());
        stockWatchAdapter.notifyDataSetChanged();
    }

    private void doRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        ArrayList<Stock> slist = dbHandler.loadStocks();
        for (int i = 0; i < slist.size(); i++) {
            String symbol = slist.get(i).getStockSymbol();
            new AsyncTaskStock(MainActivity.this).execute(symbol);
        }
        Toast.makeText(this, "Data Refreshed", Toast.LENGTH_SHORT).show();
    }

    private boolean networkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Toast.makeText(this, "No Network Connection!", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void errorAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Stocks cannot be added without a Network Connection");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected: start");
        if (!networkConnectivity()) {
            errorAlert();
            return false;
        } else {
            switch (id) {
                case R.id.add_stock:
                    Log.d(TAG, "onOptionsItemSelected: start");
                    addStockAlert();
                    Log.d(TAG, "onOptionsItemSelected: end/dialog");
                    return true;
                default:
                    Log.d(TAG, "onOptionsItemSelected: end/default");
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    private void addStockAlert() {
        if (hMap == null) {
            new AsynTaskCompany(MainActivity.this).execute();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please Enter A Stock Symbol");
        builder.setCancelable(false);
        final EditText eText = new EditText(this);
        eText.setInputType(InputType.TYPE_CLASS_TEXT);
        eText.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(eText);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!networkConnectivity()) {
                    errorAlert();
                    return;
                } else if (eText.getText().toString().isEmpty()) {           //empty search
                    Toast.makeText(MainActivity.this, "Please Enter Valid Input", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    ArrayList<String> temp = searchStock(eText.getText().toString());
                    if (!temp.isEmpty()) {
                        ArrayList<String> stockOptions = new ArrayList<>(temp);
                        if (stockOptions.size() == 1) {
                            if (checkDuplicateStock(stockOptions.get(0))) {
                                duplicateStockAlert(eText.getText().toString());
                            } else {
                                addStock(stockOptions.get(0));
                            }
                        } else {
                            duplicateDialogAlert(eText.getText().toString(), stockOptions, stockOptions.size());
                        }
                    } else {
                        stockNotFoundAlert(eText.getText().toString());
                    }

                }
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void stockNotFoundAlert(String toString)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Symbol Not Found: " +toString);
        builder.setMessage("Data for stock symbol");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void duplicateDialogAlert(final String s, ArrayList<String> stockOptions, int size) {
        final String[] strings = new String[size];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = stockOptions.get(i);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make a Selection");
        builder.setItems(strings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkDuplicateStock(strings[which])) {
                    duplicateStockAlert(s);
                } else {
                    addStock(strings[which]);
                }
            }
        });
        builder.setNegativeButton("NEVER MIND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addStock(String s) {
        String sym = s.split("-")[0].trim();
        new AsyncTaskStock(MainActivity.this).execute(sym);
        Stock ss = new Stock();
        ss.setStockSymbol(sym);
        ss.setStockName(hMap.get(sym));
        dbHandler.addStock(ss);
    }

    private void duplicateStockAlert(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Duplicate Stock");
        builder.setMessage("Stock symbol  "+s+" is already displayed");
        builder.setIcon(R.drawable.ic_warning_black_24dp);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean checkDuplicateStock(String s) {
        Log.d(TAG, "isDuplicateStock: ");
        String sym = s.split("-")[0].trim();
        Stock t = new Stock();
        t.setStockSymbol(sym);
        return stockList.contains(t);
    }

    private ArrayList<String> searchStock(String s) {
        ArrayList<String> sList = new ArrayList<>();
        if (hMap != null && !hMap.isEmpty()) {
            for (String symbol : hMap.keySet()) {
                String name = hMap.get(symbol);
                if (symbol.toUpperCase().contains(s.toUpperCase())) {
                    sList.add(symbol + " - " + name);
                } else if (name.toUpperCase().contains(s.toUpperCase())) {
                    sList.add(symbol + " - " + name);
                }

            }
        }
        return sList;
    }


    public void updateStock(Stock stock) {
        if (stock != null) {
            Log.d(TAG, "In Stock !=null condition");
            int index = stockList.indexOf(stock);
            Log.d(TAG, "The index " + index);
            if (index > -1) {
                Log.d(TAG, "In Stock index");
                stockList.remove(index);
            }
            stockList.add(stock);
            Collections.sort(stockList, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    return o1.getStockSymbol().compareTo(o2.getStockSymbol());
                }
            });
            stockWatchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHandler.shutDown();
    }
}


























