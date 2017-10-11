package com.example.xinyichen.sunshine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private FusedLocationProviderClient mFusedLocationClient;
    boolean tempInFah = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button poweredBy = (Button) findViewById(R.id.poweredBy);
        poweredBy.setOnClickListener(this);
        ConstraintLayout tempLayout = (ConstraintLayout) findViewById(R.id.tempLayout);
        tempLayout.setOnClickListener(this);

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
                                new Utils.GetWeatherInfo(MainActivity.this).execute(location.getLatitude() + "," + location.getLongitude());
                            }
                        }
                    });
        }

        /*
        tried to only show rain icon if the string wasn't that it was not going to rain
         */

        ImageView drop = (ImageView) findViewById(R.id.droplet);
        if (((TextView) findViewById(R.id.rainText)).getText().toString() != "It's not going to rain!" + "") {
            drop.setVisibility(View.INVISIBLE);
        } else {
            drop.setImageResource(R.drawable.ic_drop);
            drop.setVisibility(View.VISIBLE);
        }
        /*
        attempted to switch between hot and cold icons but got a NullPointerException
         */
        ((ImageView) findViewById(R.id.hotCold)).setVisibility(View.INVISIBLE);
        /* String temp = ((TextView) (findViewById(R.id.tempText))).getText().toString();
        String[] splitted = temp.split("\u00b0");
        Integer tempInF = Integer.parseInt(splitted[0]);
        ImageView hotCold = (ImageView) findViewById(R.id.hotCold);

        if (tempInF >= 80) {
            hotCold.setImageResource(R.drawable.ic_fire);
            hotCold.setVisibility(View.VISIBLE);
        } else if (tempInF <= 55) {
            hotCold.setImageResource(R.drawable.ic_snowflake);
            hotCold.setVisibility(View.VISIBLE);
        } else {
            hotCold.setVisibility(View.INVISIBLE);
        } */

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
            case R.id.tempLayout:
                String temp = ((TextView) (findViewById(R.id.tempText))).getText().toString();
                String[] splited = temp.split("\\s+");
                Integer tempInF = Integer.parseInt(splited[0]);
                if (tempInFah) {
                    Integer tempInC = (int) ((tempInF - 32) / (1.8));
                    ((TextView) (findViewById(R.id.tempText))).setText(tempInC + " \u00b0 C");
                } else {
                    Integer tempInC = (int) (tempInF * 1.8) + 32;
                    ((TextView) (findViewById(R.id.tempText))).setText(tempInC + " \u00b0 F");
                }
                tempInFah = !tempInFah;
                break;
        }
    }

}
