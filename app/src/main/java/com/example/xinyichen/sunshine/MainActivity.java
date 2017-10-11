package com.example.xinyichen.sunshine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

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
import java.util.Map;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    Map temp;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tempText = (TextView) findViewById(R.id.tempText);
        Button poweredBy = (Button) findViewById(R.id.poweredBy);
        poweredBy.setOnClickListener(this);
        TextView rainText = (TextView) findViewById(R.id.rainText);
        TextView descriptionText = (TextView) findViewById(R.id.weatherSumText);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                new GetWeatherInfo(MainActivity.this).execute(location.getLatitude() + "," + location.getLongitude());
                            }
                        }
                    });
        }

    }

    private class GetWeatherInfo extends AsyncTask<String, Void, String> {

        Context context;

        private GetWeatherInfo() {}

        private GetWeatherInfo(Context context) {this.context = context;}

        @Override
        protected String doInBackground(String... strings) {
            String coordinates = strings[0];
            //JSONObject data = Utils.getJSON(coordinates);
            return getWeatherJSONString(coordinates);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this,"JSON Data is downloading",Toast.LENGTH_LONG).show();

        }

        protected void onPostExecute(String result) {
            
            TextView textView = (TextView) ((Activity) context).findViewById(R.id.tempText);
            textView.setText(result);
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(this, "I need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.poweredBy:
                Uri uri = Uri.parse("http://darksky.net/poweredby/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }

    static String getWeatherJSONString(String coordinates) {
        try {
            URL url = new URL(Utils.API + coordinates);
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


}
