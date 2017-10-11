package com.example.xinyichen.sunshine;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by xinyichen on 10/10/17.
 */

public class Utils {
    final static String API = "https://api.darksky.net/forecast/423a191d69058abd2c4c98832c332adc/%s";

    public static JSONObject getJSON(String coord){
        try {
            URL url = new URL(String.format((API), coord));

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

    /* static String getWeatherJSONString(String coord) {
        try {
            URL url = new URL(String.format((API), coord));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String response = convertStreamToString(in);
            return response;
        } catch (MalformedURLException e) {
            Log.e("error", e.getMessage());
        } catch (IOException e) {
            Log.e("error", e.getMessage());
        }
        return "404";
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    } //ty stackOverflow

    static class JSONHandler extends AsyncTask<String, Void, String> {
        Context context;

        private JSONHandler() {

        }
        public JSONHandler(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 1) {
                String pokemonName = strings[0];
                return getWeatherJSONString(pokemonName);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView textView = (TextView) ((Activity) context).findViewById(R.id.tempText);
            textView.setText(s);
        }
    } */
}
