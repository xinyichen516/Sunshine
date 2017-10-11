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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    Map temp;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button poweredBy = (Button) findViewById(R.id.poweredBy);
        poweredBy.setOnClickListener(this);

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

    private class GetWeatherInfo extends AsyncTask<String, Void, ArrayList> {

        Context context;
        String rainTime;
        boolean willRain = false;

        private GetWeatherInfo() {}

        private GetWeatherInfo(Context context) {this.context = context;}

        @Override
        protected ArrayList<Object> doInBackground(String... strings) {
            String coordinates = strings[0];
            JSONObject result = Utils.getJSON(coordinates);
            ArrayList<Object> returned = new ArrayList<>();
            try {
                String currTemp = result.getJSONObject("currently").getString("temperature");
                Integer tempInt = (int) Float.parseFloat(currTemp);
                returned.add(tempInt);

                String description = result.getJSONObject("daily").getString("summary");
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
            Toast.makeText(MainActivity.this,"JSON Data is downloading",Toast.LENGTH_LONG).show();

        }

        protected void onPostExecute(ArrayList result) {
            ((TextView) ((Activity) context).findViewById(R.id.tempText)).setText(result.get(0) + "");
            ((TextView) ((Activity) context).findViewById(R.id.weatherSumText)).setText(result.get(1) + "");
            ((TextView) ((Activity) context).findViewById(R.id.rainText)).setText(result.get(2) + "");
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

}
