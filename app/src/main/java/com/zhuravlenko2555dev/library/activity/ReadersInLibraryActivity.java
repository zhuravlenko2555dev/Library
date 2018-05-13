package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.fragment.RetainedReadersInLibraryFragment;
import com.zhuravlenko2555dev.library.provider.ReadersInLibraryRow;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadersInLibraryActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private TableLayout tableLayoutReadersInLibrary;

    private FragmentManager fragmentManager;
    private RetainedReadersInLibraryFragment retainedReadersInLibraryFragment;

    private ArrayList<ReadersInLibraryRow> rowList;

    private boolean firstLaunch = true;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask rowTask = null;
    private FillTableLayout fillTableLayout = null;

    boolean rowTaskIsFinished = true;

    private ActivityManager activityManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readers_in_library);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        fragmentManager = getSupportFragmentManager();
        retainedReadersInLibraryFragment = (RetainedReadersInLibraryFragment) fragmentManager.findFragmentByTag(RetainedReadersInLibraryFragment.FRAGMENT_TAG);

        if (retainedReadersInLibraryFragment == null) {
            retainedReadersInLibraryFragment  = new RetainedReadersInLibraryFragment();
            fragmentManager.beginTransaction().add(retainedReadersInLibraryFragment, RetainedReadersInLibraryFragment.FRAGMENT_TAG).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tableLayoutReadersInLibrary = findViewById(R.id.tableLayoutReadersInLibrary);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeIntentRow();
            }
        });

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
                            case APIAsyncTask.ACTION_GET_READERS_IN_LIBRARY:
                                rowTaskIsFinished = true;

                                fillTableLayout = new FillTableLayout(responseJsonArray);
                                fillTableLayout.execute();

                                break;
                        }

                        if (rowTaskIsFinished && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        break;
                    case APIAsyncTask.RESPONSE_ERROR:
                        break;
                }
            }
        };
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

            executeIntentRow();
        }

        if (retainedReadersInLibraryFragment.getRowList().size() != 0) {
            rowList = retainedReadersInLibraryFragment.getRowList();

            for (int i = 0; i < rowList.size(); i++) {
                LinearLayout tableRow = (LinearLayout) View.inflate(ReadersInLibraryActivity.this, R.layout.row_readers_in_library, null);

                ((TextView) tableRow.findViewById(R.id.textViewAccountName)).setText(rowList.get(i).getAccountName());
                ((TextView) tableRow.findViewById(R.id.textViewAccountSurname)).setText(rowList.get(i).getAccountSurname());
                ((TextView) tableRow.findViewById(R.id.textViewAccountMiddleName)).setText(rowList.get(i).getAccountMiddleName());
                ((TextView) tableRow.findViewById(R.id.textViewBookName)).setText(rowList.get(i).getBookName());
                ((TextView) tableRow.findViewById(R.id.textViewBookEditionLanguage)).setText(rowList.get(i).getBookEditionLanguage());
                ((TextView) tableRow.findViewById(R.id.textViewBookEditionPublisher)).setText(rowList.get(i).getBookEditionPublisher());
                tableLayoutReadersInLibrary.addView(tableRow);

                if (i < rowList.size()) {
                    View delimeter = new View(ReadersInLibraryActivity.this);
                    delimeter.setLayoutParams( new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, (int) (1 * getResources().getDisplayMetrics().density)));
                    delimeter.setBackgroundColor(Color.parseColor("#ffffff"));

                    tableLayoutReadersInLibrary.addView(delimeter);
                }
            }
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
    protected void onDestroy() {
        super.onDestroy();

        retainedReadersInLibraryFragment.setRowList(rowList);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        killAllTask();

        fragmentManager.beginTransaction().remove(retainedReadersInLibraryFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("ROW_TASK_IS_FINISHED", rowTaskIsFinished);

        outState.putBoolean("SWIPE_REFRESH_LAYOUT", swipeRefreshLayout.isRefreshing());

        outState.putBoolean("FIRST_LAUNCH", firstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        rowTaskIsFinished = savedInstanceState.getBoolean("ROW_TASK_IS_FINISHED");

        swipeRefreshLayout.setRefreshing(savedInstanceState.getBoolean("SWIPE_REFRESH_LAYOUT"));

        firstLaunch = savedInstanceState.getBoolean("FIRST_LAUNCH");
    }

    private void executeIntentRow() {
        rowTaskIsFinished = false;

        Intent intentRow = new Intent(this, APIAsyncTask.class)
                .setAction(APIAsyncTask.ACTION_GET_READERS_IN_LIBRARY);

        if (rowTask != null && rowTask.getStatus() == AsyncTask.Status.RUNNING) {
            rowTask.cancel(false);
        }

        if (fillTableLayout != null && fillTableLayout.getStatus() == AsyncTask.Status.RUNNING) {
            fillTableLayout.cancel(false);
        }

        rowTask = new APIAsyncTask(this);
        rowTask.execute(intentRow);
    }

    private void killAllTask() {
        if (rowTask != null && rowTask.getStatus() == AsyncTask.Status.RUNNING) {
            rowTask.cancel(false);
        }

        if (fillTableLayout != null && fillTableLayout.getStatus() == AsyncTask.Status.RUNNING) {
            fillTableLayout.cancel(false);
        }
    }

    class FillTableLayout extends AsyncTask<Void, Void, Void> {
        JSONArray responseJsonArray;

        public FillTableLayout(JSONArray responseJsonArray) {
            this.responseJsonArray = responseJsonArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            rowList = new ArrayList<>();

            for (int i = 0; i < responseJsonArray.length(); i++) {
                try {
                    JSONObject responseJsonObject = responseJsonArray.getJSONObject(i);
                    String accountName = (String) responseJsonObject.get("a_name");
                    String accountSurname = (String) responseJsonObject.get("a_surname");
                    String accountMiddleName = (String) responseJsonObject.get("a_middle_name");
                    String bookName = (String) responseJsonObject.get("b_name");
                    String bookEditionLanguage = (String) responseJsonObject.get("l_name");
                    String bookEditionPublisher = (String) responseJsonObject.get("p_name");

                    rowList.add(new ReadersInLibraryRow(accountName, accountSurname, accountMiddleName, bookName, bookEditionLanguage, bookEditionPublisher));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!isCancelled()) {
                tableLayoutReadersInLibrary.removeAllViews();

                for (int i = 0; i < rowList.size(); i++) {
                    LinearLayout tableRow = (LinearLayout) View.inflate(ReadersInLibraryActivity.this, R.layout.row_readers_in_library, null);

                    ((TextView) tableRow.findViewById(R.id.textViewAccountName)).setText(rowList.get(i).getAccountName());
                    ((TextView) tableRow.findViewById(R.id.textViewAccountSurname)).setText(rowList.get(i).getAccountSurname());
                    ((TextView) tableRow.findViewById(R.id.textViewAccountMiddleName)).setText(rowList.get(i).getAccountMiddleName());
                    ((TextView) tableRow.findViewById(R.id.textViewBookName)).setText(rowList.get(i).getBookName());
                    ((TextView) tableRow.findViewById(R.id.textViewBookEditionLanguage)).setText(rowList.get(i).getBookEditionLanguage());
                    ((TextView) tableRow.findViewById(R.id.textViewBookEditionPublisher)).setText(rowList.get(i).getBookEditionPublisher());

                    tableLayoutReadersInLibrary.addView(tableRow);

                    if (i < rowList.size()) {
                        View delimeter = new View(ReadersInLibraryActivity.this);
                        delimeter.setLayoutParams( new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, (int) (1 * getResources().getDisplayMetrics().density)));
                        delimeter.setBackgroundColor(Color.parseColor("#ffffff"));

                        tableLayoutReadersInLibrary.addView(delimeter);
                    }
                }
            }
        }
    }
}
