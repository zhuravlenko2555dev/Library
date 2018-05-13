package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhuravlenko2555dev.library.util.APIAsyncTask;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;
import com.zhuravlenko2555dev.library.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputLayout textInputLayoutLogin, textInputLayoutPassword;
    private EditText editTextLogin, editTextPassword;
    private Button buttonLogin, buttonCreateAccount;

    private Dialog dialog, alertDialogProcessing;
    private AlertDialog.Builder builderProcessing;

    private String login = null;
    private String password = null;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask loginTask = null;

    private ActivityManager activityManager = null;

    private AnimationDrawable animationDrawableDialogProcessing = null;
    private AssetManager assetManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        textInputLayoutLogin = findViewById(R.id.textInputLayoutLogin);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        buttonLogin.setOnClickListener(this);
        buttonCreateAccount.setOnClickListener(this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case APIAsyncTask.START_REQUEST:
                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_ACCOUNT_AUTHENTICATE:
                                showDialog(APIAsyncTask.PROGRESS_DIALOG_PROCESSING);

                                break;
                        }

                        break;
                    case APIAsyncTask.RESPONSE_OK:
                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_ACCOUNT_AUTHENTICATE:
                                killAllTask();

                                AccountAuthLocalStore.AccountAuth accountAuth = new AccountAuthLocalStore.AccountAuth(login, password);
                                AccountAuthLocalStore.setAccountAuth(accountAuth);
                                AccountAuthLocalStore.setAccountLoggedIn(true);

                                dismissDialog(APIAsyncTask.PROGRESS_DIALOG_PROCESSING);
                                animationDrawableDialogProcessing.stop();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                                break;
                        }

                        break;
                    case APIAsyncTask.RESPONSE_ERROR:
                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_ACCOUNT_AUTHENTICATE:
                                buttonLogin.setEnabled(true);

                                dismissDialog(APIAsyncTask.PROGRESS_DIALOG_PROCESSING);
                                animationDrawableDialogProcessing.stop();
                                String responseError = intent.getStringExtra(APIAsyncTask.EXTRA_RESPONSE_ERROR);

                                textInputLayoutLogin.setError(responseError);
                                textInputLayoutPassword.setError(responseError);

                                break;
                        }

                        break;
                }
            }
        };
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case APIAsyncTask.PROGRESS_DIALOG_PROCESSING:
                View view = getLayoutInflater().inflate(R.layout.progress_dialog_processing, null);
                ImageView imageViewDialogProcessing = view.findViewById(R.id.imageViewDialogProcessing);

                Display display = getWindowManager().getDefaultDisplay();
                double size;

                int orientation = this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    size = display.getWidth();
                    imageViewDialogProcessing.getLayoutParams().width = imageViewDialogProcessing.getLayoutParams().height = (int) (size / 10) * 5;
                } else {
                    size = display.getHeight();
                    imageViewDialogProcessing.getLayoutParams().width = imageViewDialogProcessing.getLayoutParams().height = (int) (size / 10) * 5;
                }

                builderProcessing = new AlertDialog.Builder(this);
                builderProcessing.setCancelable(false);
                builderProcessing.setView(view);

                animationDrawableDialogProcessing = new AnimationDrawable();
                assetManager = getAssets();

                for (int i = 0; i< 90; i++) {
                    try {
                        animationDrawableDialogProcessing.addFrame(new BitmapDrawable(BitmapFactory.decodeStream(new BufferedInputStream((assetManager.open("progressbar_cat/" + Integer.toString(i) + ".gif"))))), 30);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                animationDrawableDialogProcessing.setOneShot(false);
                imageViewDialogProcessing.setBackgroundDrawable(animationDrawableDialogProcessing);

                alertDialogProcessing = builderProcessing.create();
                alertDialogProcessing.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_background));

                dialog = alertDialogProcessing;

                break;
        }

        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);

        switch (id) {
            case APIAsyncTask.PROGRESS_DIALOG_PROCESSING:
                animationDrawableDialogProcessing.start();

                break;
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
            case R.id.buttonLogin:
                buttonLogin.setEnabled(false);

                login = editTextLogin.getText().toString();
                password = editTextPassword.getText().toString();

                Intent intentLogin = new Intent(this, APIAsyncTask.class)
                        .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, login)
                        .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, password)
                        .setAction(APIAsyncTask.ACTION_ACCOUNT_AUTHENTICATE);
                loginTask = new APIAsyncTask(this);
                loginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentLogin);

                break;
            case R.id.buttonCreateAccount:
                Intent intentRegister = new Intent(this, RegisterActivity.class);
                startActivity(intentRegister);

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

        outState.putString(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, login);
        outState.putString(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, password);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        login = savedInstanceState.getString(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN);
        password = savedInstanceState.getString(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD);
    }

    private void killAllTask() {
        if (loginTask != null && loginTask.getStatus() == AsyncTask.Status.RUNNING) {
            loginTask.cancel(false);
        }
    }
}
