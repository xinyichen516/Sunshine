package com.example.xinyichen.sunshine;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xinyichen on 10/10/17.
 */

public class Utils {
    final static String API = "https://api.darksky.net/forecast/423a191d69058abd2c4c98832c332adc/";

    public static JSONObject getJSON(String coord){
        try {
            URL url = new URL(API + coord);

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();
            connection.getInputStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            return data;

        } catch(Exception e){

            e.printStackTrace();

            return null;
        }
    }

    static class GetWeatherInfo extends AsyncTask<String, Void, ArrayList> {

        Context context;
        String rainTime;
        boolean willRain = false;

        GetWeatherInfo() {}

        GetWeatherInfo(Context context) {this.context = context;}

        @Override
        protected ArrayList<Object> doInBackground(String... strings) {
            String coordinates = strings[0];
            JSONObject result = Utils.getJSON(coordinates);
            ArrayList<Object> returned = new ArrayList<>();
            try {
                String currTemp = result.getJSONObject("currently").getString("temperature");
                Integer tempInt = (int) Float.parseFloat(currTemp);
                returned.add(tempInt);

                String description = result.getJSONObject("hourly").getString("summary");
                returned.add(description);

                JSONArray minutes = result.getJSONObject("minutely").getJSONArray("data");
                int i = 0;
                while (i < minutes.length() && willRain == false) {
                    if (minutes.getJSONObject(i).getString("precipProbability").equals("1")) {
                        rainTime = minutes.getJSONObject(i).getString("time");
                        willRain = true;
                    }
                    i++;
                }
                if (willRain == false) {
                    returned.add("It's not going to rain!");
                } else {
                    String formatted = new SimpleDateFormat("h:mm a", Locale.US).format(new Date(Long.getLong(rainTime) * 1000L));
                    returned.add(formatted);
                }

            } catch (Exception e) {
                e.getMessage();
            }
            return returned;
            //return getWeatherJSONString(coordinates);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText((Activity) context,"JSON Data is downloading",Toast.LENGTH_LONG).show();

        }

        protected void onPostExecute(ArrayList result) {
            ((TextView) ((Activity) context).findViewById(R.id.tempText)).setText(result.get(0) + " \u00b0 F");
            ((TextView) ((Activity) context).findViewById(R.id.weatherSumText)).setText(result.get(1) + "");
            ((TextView) ((Activity) context).findViewById(R.id.rainText)).setText(result.get(2) + "");
        }
    }
}
