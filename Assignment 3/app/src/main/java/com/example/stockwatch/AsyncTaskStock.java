package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncTaskStock extends AsyncTask<String,Void,String>
{
    private static final String TAG = "Async2";
    private MainActivity mainActivity;
    private static final String DATAURL1 = "https://cloud.iexapis.com/stable/stock/";
    private static final String DATAURL2 = "/quote?token=sk_d89a3f44a3c04a11ad0bfe2d2267ec87";

    public AsyncTaskStock(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        String DATA_URL = DATAURL1 + strings[0] + DATAURL2;
        String s = connectToUrl(DATA_URL);
        Log.d(TAG, "back: " + s);
        return s;
    }

    public String connectToUrl(String url1)
    {
        Uri dataUri = Uri.parse(url1);
        String url2 = dataUri.toString();
        Log.d(TAG, "doInBackground: " + url2);
        StringBuilder sBuilder = new StringBuilder();
        try
        {
            URL url3 = new URL(url2);
            HttpURLConnection httpConnection = (HttpURLConnection) url3.openConnection();
            httpConnection.setRequestMethod("GET");
            InputStream iStream = httpConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
            String line;
            while((line = bReader.readLine())!= null)
            {
                sBuilder.append(line).append('\n');
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sBuilder.toString();
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        Stock stock = jsonParse(s);
        mainActivity.updateStock(stock);
    }

    private Stock jsonParse(String s)
    {
        Stock nStock = new Stock();
        try
        {
            JSONObject jsonObject = new JSONObject(s);
            String symbol = jsonObject.getString("symbol");
            String name = jsonObject.getString("companyName");
            double price = jsonObject.getDouble("latestPrice");
            double priceChange = jsonObject.getDouble("change");
            double percentChange = jsonObject.getDouble("changePercent");

            nStock.setStockName(name);
            nStock.setStockSymbol(symbol);
            nStock.setStockPrice(price);
            nStock.setPriceChange(priceChange);
            nStock.setPercentChange(percentChange);
            return nStock;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
