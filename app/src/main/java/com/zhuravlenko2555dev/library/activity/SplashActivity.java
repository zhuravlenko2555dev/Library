package com.zhuravlenko2555dev.library.activity;

import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_TIMEOUT = 1000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PreferenceManager.setDefaultValues(this, R.xml.pref, false);

        AccountAuthLocalStore.initialize(this);

        handler.postDelayed(runnable, SPLASH_DISPLAY_TIMEOUT);
    }
}
