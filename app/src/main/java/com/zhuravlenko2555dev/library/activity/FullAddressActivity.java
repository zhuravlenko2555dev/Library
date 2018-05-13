package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.Toast;

import com.zhuravlenko2555dev.library.fragment.RetainedFullAddressFragment;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;
import com.zhuravlenko2555dev.library.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FullAddressActivity extends AppCompatActivity implements View.OnClickListener {
    public static String EXTRA_COUNTRY = "extra_country";
    public static String EXTRA_CITY = "extra_city";
    public static String EXTRA_ADDRESS = "extra_address";

    enum CURRENT_TYPE_LIST {
        COUNTRY, CITY, ADDRESS
    }

    /*TextView textViewCurrentTypeList;*/
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton imageButtonEditCountry, imageButtonEditCity, imageButtonEditAddress;
    private SearchView searchView;
    private ListView listViewFullAddress;

    private FragmentManager fragmentManager;
    private RetainedFullAddressFragment retainedFullAddressFragment;

    private String country = "";
    private String city = "";
    private String address = "";

    private CURRENT_TYPE_LIST currentTypeList;
    private String currentSearchViewText = "";

    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;

    private boolean firstLaunch = true;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask countryTask = null;
    private APIAsyncTask cityTask = null;
    private APIAsyncTask addressTask = null;
    private FillListView fillListViewTask = null;

    private boolean countryTaskIsFinished = true;
    private boolean cityTaskIsFinished = true;
    private boolean addressTaskIsFinished = true;

    private ActivityManager activityManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_address);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        fragmentManager = getSupportFragmentManager();
        retainedFullAddressFragment = (RetainedFullAddressFragment) fragmentManager.findFragmentByTag(RetainedFullAddressFragment.FRAGMENT_TAG);

        if (retainedFullAddressFragment == null) {
            retainedFullAddressFragment  = new RetainedFullAddressFragment();
            fragmentManager.beginTransaction().add(retainedFullAddressFragment, RetainedFullAddressFragment.FRAGMENT_TAG).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        imageButtonEditCountry = findViewById(R.id.imageButtonEditCountry);
        imageButtonEditCity = findViewById(R.id.imageButtonEditCity);
        imageButtonEditAddress = findViewById(R.id.imageButtonEditAddress);
        listViewFullAddress = findViewById(R.id.listView);

        imageButtonEditCountry.setOnClickListener(this);
        imageButtonEditCity.setOnClickListener(this);
        imageButtonEditAddress.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (fillListViewTask != null && fillListViewTask.getStatus() == AsyncTask.Status.RUNNING) {
                    fillListViewTask.cancel(false);
                }

                switch (currentTypeList) {
                    case COUNTRY:
                        executeIntentCountry(currentSearchViewText);

                        break;
                    case CITY:
                        executeIntentCity(currentSearchViewText);

                        break;
                    case ADDRESS:
                        executeIntentAddress(currentSearchViewText);

                        break;
                }
            }
        });

        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                if (listViewFullAddress != null)
                    return listViewFullAddress.canScrollVertically(-1);

                return false;
            }
        });

        listViewFullAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentTypeList) {
                    case COUNTRY:
                        if (!country.equals(itemList.get(position))) {
                            country = itemList.get(position);
                            city = "";
                            address = "";

                            imageButtonEditCity.setEnabled(true);
                            imageButtonEditCity.setAlpha(1.0f);

                            imageButtonEditAddress.setEnabled(false);
                            imageButtonEditAddress.setAlpha(0.5f);
                        }

                        break;
                    case CITY:
                        if (!city.equals(itemList.get(position))) {
                            city = itemList.get(position);
                            address = "";

                            imageButtonEditAddress.setEnabled(true);
                            imageButtonEditAddress.setAlpha(1.0f);
                        }

                        break;
                    case ADDRESS:
                        if (!address.equals(itemList.get(position))) {
                            address = itemList.get(position);
                        }

                        break;
                }
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

                        fillListViewTask = new FillListView(responseJsonArray);

                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_GET_COUNTRY:
                                countryTaskIsFinished = true;

                                fillListViewTask.execute();

                                break;
                            case APIAsyncTask.ACTION_GET_CITY:
                                cityTaskIsFinished = true;

                                fillListViewTask.execute();

                                break;
                            case APIAsyncTask.ACTION_GET_ADDRESS:
                                addressTaskIsFinished = true;

                                fillListViewTask.execute();

                                break;
                        }

                        if (countryTaskIsFinished && cityTaskIsFinished && addressTaskIsFinished && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        break;
                    case APIAsyncTask.RESPONSE_ERROR:
                        break;
                }
            }
        };

        if (savedInstanceState == null) {
            currentTypeList = CURRENT_TYPE_LIST.COUNTRY;
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

            executeIntentCountry("");
        }

        if (retainedFullAddressFragment.getItemList().size() != 0) {
            itemList = retainedFullAddressFragment.getItemList();
            adapter = new ArrayAdapter<String>(FullAddressActivity.this, android.R.layout.simple_list_item_1, itemList);
            listViewFullAddress.setAdapter(adapter);
        }

        if (country.isEmpty()) {
            imageButtonEditCity.setEnabled(false);
            imageButtonEditCity.setAlpha(0.5f);
        }

        if (city.isEmpty()) {
            imageButtonEditAddress.setEnabled(false);
            imageButtonEditAddress.setAlpha(0.5f);
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

        retainedFullAddressFragment.setItemList(itemList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonEditCountry:
                if (currentTypeList != CURRENT_TYPE_LIST.COUNTRY) {
                    currentTypeList = CURRENT_TYPE_LIST.COUNTRY;

                    searchView.setQuery("", false);
                    searchView.setIconified(false);

                    executeIntentCountry("");
                }

                break;
            case R.id.imageButtonEditCity:
                if (currentTypeList != CURRENT_TYPE_LIST.CITY) {
                    currentTypeList = CURRENT_TYPE_LIST.CITY;

                    searchView.setQuery("", false);
                    searchView.setIconified(false);

                    executeIntentCity("");
                }

                break;
            case R.id.imageButtonEditAddress:
                if (currentTypeList != CURRENT_TYPE_LIST.ADDRESS) {
                    currentTypeList = CURRENT_TYPE_LIST.ADDRESS;

                    searchView.setQuery("", false);
                    searchView.setIconified(false);

                    executeIntentAddress("");
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_full_address, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_full_address).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);

        menu.add(0, 3, 3, getResources().getString(R.string.settings))
                .setIcon(R.drawable.settings)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIntent(new Intent(this, PrefActivity.class));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (currentTypeList != null && !currentSearchViewText.equals(query)) {
                    currentSearchViewText = query;

                    switch (currentTypeList) {
                        case COUNTRY:
                            executeIntentCountry(query);

                            break;
                        case CITY:
                            executeIntentCity(query);

                            break;
                        case ADDRESS:
                            executeIntentAddress(query);

                            break;
                    }
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (currentTypeList != null && !currentSearchViewText.equals(newText)) {
                    currentSearchViewText = newText;

                    switch (currentTypeList) {
                        case COUNTRY:
                            executeIntentCountry(newText);

                            break;
                        case CITY:
                            executeIntentCity(newText);

                            break;
                        case ADDRESS:
                            executeIntentAddress(newText);

                            break;
                    }
                }

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast toast;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                break;
            case R.id.ok:
                if (!country.isEmpty() && !city.isEmpty() && !address.isEmpty()) {
                    killAllTask();

                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_COUNTRY, country);
                    intent.putExtra(EXTRA_CITY, city);
                    intent.putExtra(EXTRA_ADDRESS, address);

                    Log.d("aaa", country + "   " + city + "   " + address);

                    setResult(RESULT_OK, intent);
                    finish();
                }

                if (country.isEmpty()) {
                    toast = Toast.makeText(this, "Select country!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                if (city.isEmpty()) {
                    toast = Toast.makeText(this, "Select city!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                if (address.isEmpty()) {
                    toast = Toast.makeText(this, "Select address!", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        killAllTask();

        overridePendingTransition( R.anim.right_in, R.anim.right_out);

        fragmentManager.beginTransaction().remove(retainedFullAddressFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_COUNTRY, country);
        outState.putString(EXTRA_CITY, city);
        outState.putString(EXTRA_ADDRESS, address);
        outState.putString("CURRENT_TYPE_LIST", currentTypeList.toString());
        outState.putString("CURRENT_SEARCH_VIEW_TEXT", currentSearchViewText);
        outState.putBoolean("FIRST_LAUNCH", firstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        country = savedInstanceState.getString(EXTRA_COUNTRY);
        city = savedInstanceState.getString(EXTRA_CITY);
        address = savedInstanceState.getString(EXTRA_ADDRESS);
        currentTypeList = CURRENT_TYPE_LIST.valueOf(savedInstanceState.getString("CURRENT_TYPE_LIST"));
        currentSearchViewText = savedInstanceState.getString("CURRENT_SEARCH_VIEW_TEXT");
        firstLaunch = savedInstanceState.getBoolean("FIRST_LAUNCH");
    }

    private void executeIntentCountry(String query) {
        countryTaskIsFinished = false;

        Intent intentCountry = new Intent(FullAddressActivity.this, APIAsyncTask.class)
                .putExtra(EXTRA_COUNTRY, query)
                .setAction(APIAsyncTask.ACTION_GET_COUNTRY);

        if (countryTask != null && countryTask.getStatus() == AsyncTask.Status.RUNNING) {
            countryTask.cancel(false);
        }

        countryTask = new APIAsyncTask(FullAddressActivity.this);
        countryTask.execute(intentCountry);
    }

    private void executeIntentCity(String query) {
        cityTaskIsFinished = false;

        Intent intentCity = new Intent(FullAddressActivity.this, APIAsyncTask.class)
                .putExtra(EXTRA_COUNTRY, country)
                .putExtra(EXTRA_CITY, query)
                .setAction(APIAsyncTask.ACTION_GET_CITY);

        if (cityTask != null && cityTask.getStatus() == AsyncTask.Status.RUNNING) {
            cityTask.cancel(false);
        }

        cityTask = new APIAsyncTask(FullAddressActivity.this);
        cityTask.execute(intentCity);
    }

    private void executeIntentAddress(String query) {
        addressTaskIsFinished = false;

        Intent intentAddress = new Intent(FullAddressActivity.this, APIAsyncTask.class)
                .putExtra(EXTRA_COUNTRY, country)
                .putExtra(EXTRA_CITY, city)
                .putExtra(EXTRA_ADDRESS, query)
                .setAction(APIAsyncTask.ACTION_GET_ADDRESS);

        if (addressTask != null && addressTask.getStatus() == AsyncTask.Status.RUNNING) {
            addressTask.cancel(false);
        }

        addressTask = new APIAsyncTask(FullAddressActivity.this);
        addressTask.execute(intentAddress);
    }

    private void killAllTask() {
        if (countryTask != null && countryTask.getStatus() == AsyncTask.Status.RUNNING) {
            countryTask.cancel(false);
        }

        if (cityTask != null && cityTask.getStatus() == AsyncTask.Status.RUNNING) {
            cityTask.cancel(false);
        }

        if (addressTask != null && addressTask.getStatus() == AsyncTask.Status.RUNNING) {
            addressTask.cancel(false);
        }

        if (fillListViewTask != null && fillListViewTask.getStatus() == AsyncTask.Status.RUNNING) {
            fillListViewTask.cancel(false);
        }
    }

    class FillListView extends AsyncTask<Void, Void, Void> {
        JSONArray responseJsonArray;

        public FillListView(JSONArray responseJsonArray) {
            this.responseJsonArray = responseJsonArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            itemList = new ArrayList<>();

            for (int i = 0; i < responseJsonArray.length(); i++) {
                try {
                    JSONObject responseJsonObject = responseJsonArray.getJSONObject(i);
                    String name = (String) responseJsonObject.get("name");

                    itemList.add(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new ArrayAdapter<String>(FullAddressActivity.this, android.R.layout.simple_list_item_1, itemList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!isCancelled()) {
                listViewFullAddress.setAdapter(adapter);
            }
        }
    }
}
