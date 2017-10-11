package com.example.xinyichen.sunshine;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int WELCOME_TIMEOUT = 2500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent welcome = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(welcome);

            }
        }, WELCOME_TIMEOUT);
    }
}
