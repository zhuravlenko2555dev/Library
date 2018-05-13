package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;
import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearLayoutBooks, linearLayoutSettings, linearLayoutAdministration;
    private ImageView imageViewBooks, imageViewSettings, imageViewAdministration;
    private TextView textViewBooks, textViewSettings, textViewAdministration;

    private boolean roleAddBook = false;
    private boolean roleAdministration = false;

    private boolean firstLaunch = true;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask checkRoleTask = null;

    private ActivityManager activityManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (authenticate() == true) {
            setContentView(R.layout.activity_main);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

            linearLayoutBooks = findViewById(R.id.linearLayoutBooks);
            linearLayoutSettings = findViewById(R.id.linearLayoutSettings);
            linearLayoutAdministration = findViewById(R.id.linearLayoutAdministration);

            linearLayoutBooks.setOnClickListener(this);
            linearLayoutSettings.setOnClickListener(this);
            linearLayoutAdministration.setOnClickListener(this);

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    switch (intent.getAction()) {
                        case APIAsyncTask.START_REQUEST:

                            break;
                        case APIAsyncTask.RESPONSE_OK:
                            String responseOk = intent.getStringExtra(APIAsyncTask.EXTRA_RESPONSE_OK);
                            JSONArray responseJsonArray = null;
                            try {
                                responseJsonArray = new JSONArray(responseOk);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                                case APIAsyncTask.ACTION_ACCOUNT_CHECK_ROLE:
                                    ArrayList<String> arrayList = new ArrayList<>();

                                    for (int i = 0; i < responseJsonArray.length(); i++) {
                                        try {
                                            JSONObject responseJsonObject = responseJsonArray.getJSONObject(i);
                                            arrayList.add((String) responseJsonObject.get("name"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    /*if (arrayList.contains("adding books")) {
                                        roleAddBook = true;
                                        linearLayoutAddBook.setVisibility(View.VISIBLE);
                                    }*/

                                    if (arrayList.contains("administration")) {
                                        roleAdministration = true;
                                        linearLayoutAdministration.setVisibility(View.VISIBLE);
                                    }

                                    break;
                            }

                            break;
                        case APIAsyncTask.RESPONSE_ERROR:
                            break;
                    }
                }
            };

            if (savedInstanceState == null) {
                /*linearLayoutAddBook.setVisibility(View.GONE);*/
                linearLayoutAdministration.setVisibility(View.GONE);
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, APIAsyncTask.getIntentFilter());
        APIAsyncTask.isRegistered = true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (firstLaunch) {
            firstLaunch = false;

            Intent intentCheckRole = new Intent(this, APIAsyncTask.class)
                    .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                    .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, AccountAuthLocalStore.getAccountAuth().getPassword())
                    .setAction(APIAsyncTask.ACTION_ACCOUNT_CHECK_ROLE);

            checkRoleTask = new APIAsyncTask(this);
            checkRoleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentCheckRole);
        }

        /*if (!roleAddBook) {
            linearLayoutAddBook.setVisibility(View.GONE);
        }*/

        if (!roleAdministration) {
            linearLayoutAdministration.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);

        if (taskInfo.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            APIAsyncTask.isRegistered = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayoutBooks:
                Intent intentBook = new Intent(this, BookActivity.class);
                startActivity(intentBook);

                break;
            case R.id.linearLayoutSettings:
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);

                break;
            case R.id.linearLayoutAdministration:
                Intent intentAdministration = new Intent(this, AdministrationActivity.class);
                startActivity(intentAdministration);

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 3, 3, getResources().getString(R.string.settings))
                .setIcon(R.drawable.settings)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIntent(new Intent(this, PrefActivity.class));

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        killAllTask();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("ROLE_ADD_BOOK", roleAddBook);
        outState.putBoolean("ROLE_ADMINISTRATION", roleAdministration);

        outState.putBoolean("FIRST_LAUNCH", firstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roleAddBook = savedInstanceState.getBoolean("ROLE_ADD_BOOK");
        roleAdministration = savedInstanceState.getBoolean("ROLE_ADMINISTRATION");

        firstLaunch = savedInstanceState.getBoolean("FIRST_LAUNCH");
    }

    private boolean authenticate() {
        if (AccountAuthLocalStore.getAccountAuth() == null) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivity(intentLogin);
            return false;
        }

        return true;
    }

    private void killAllTask() {
        if (checkRoleTask != null && checkRoleTask.getStatus() == AsyncTask.Status.RUNNING) {
            checkRoleTask.cancel(false);
        }
    }
}
