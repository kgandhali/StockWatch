package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AsynTaskCompany extends AsyncTask<String,Void,String>
{
    private static final String TAG = "Async1";
    private MainActivity mainActivity;
    private static final String DATAURL = "https://api.iextrading.com/1.0/ref-data/symbols";
    public AsynTaskCompany(MainActivity mainActivity) {
        this.mainActivity =mainActivity;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        HashMap<String,String> hashMap = jsonParse(s);
        mainActivity.setData(hashMap);
    }

    @Override
    protected String doInBackground(String... strings)
    {
        String s = connectToUrl(DATAURL);
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

    private HashMap<String, String> jsonParse(String s)
    {
        HashMap<String,String> hashMap = new HashMap<>();
        try
        {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                String symbol = jsonObject.getString("symbol");
                String name = jsonObject.getString("name");
                hashMap.put(symbol,name);
            }
            return hashMap;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
