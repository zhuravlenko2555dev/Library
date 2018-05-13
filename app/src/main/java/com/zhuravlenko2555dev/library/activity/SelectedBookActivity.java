package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.fragment.RetainedSelectedBookFragment;
import com.zhuravlenko2555dev.library.provider.BookEditionAdapter;
import com.zhuravlenko2555dev.library.provider.BookEditionItem;
import com.zhuravlenko2555dev.library.ui.RecyclerViewItemDecoration;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;
import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.util.ImageCache;
import com.zhuravlenko2555dev.library.util.ImageFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectedBookActivity extends AppCompatActivity implements View.OnClickListener {
    public static String EXTRA_KEY_OF_BOOK = "extra_key_of_book";

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewBookName, textViewBookAuthor, textViewBookGenre, textViewBookOLID;
    private ImageView imageViewBookMedium;
    private RecyclerView recyclerViewBookEdition;

    private FragmentManager fragmentManager;
    private RetainedSelectedBookFragment retainedSelectedBookFragment;

    private String queryOfBook = "";
    private String keyOfBook = "";
    private String authorOfBook = "";
    private String genreOfBook = "";
    private String path = "";

    private ArrayList<BookEditionItem> itemList;
    private BookEditionAdapter adapter;

    private boolean firstLaunch = true;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask authorTask = null;
    private APIAsyncTask genreTask = null;
    private APIAsyncTask bookEditionTask = null;
    private APIAsyncTask bookImageMediumTask = null;
    private FillRecyclerView fillRecyclerView = null;

    private boolean authorTaskIsFinished = true;
    private boolean genreTaskIsFinished = true;
    private boolean bookEditionTaskIsFinished = true;
    private boolean bookImageMediumTaskIsFinished = true;

    private ActivityManager activityManager = null;

    private static final String IMAGE_CACHE_DIR = "image_book_medium";

    private ImageFetcher imageFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_book);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        int longest = ((height > width ? height : width) / 2) / 5 * 2;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        imageFetcher = new ImageFetcher(this, longest);
        imageFetcher.setLoadingImage(R.drawable.placeholder_book);
        imageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        fragmentManager = getSupportFragmentManager();
        retainedSelectedBookFragment = (RetainedSelectedBookFragment) fragmentManager.findFragmentByTag(RetainedSelectedBookFragment.FRAGMENT_TAG);

        if (retainedSelectedBookFragment == null) {
            retainedSelectedBookFragment  = new RetainedSelectedBookFragment();
            fragmentManager.beginTransaction().add(retainedSelectedBookFragment, RetainedSelectedBookFragment.FRAGMENT_TAG).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        textViewBookName = findViewById(R.id.textViewBookName);
        textViewBookAuthor = findViewById(R.id.textViewBookAuthor);
        textViewBookGenre = findViewById(R.id.textViewBookGenre);
        textViewBookOLID = findViewById(R.id.textViewBookOLID);
        imageViewBookMedium = findViewById(R.id.imageViewBookMedium);
        recyclerViewBookEdition = findViewById(R.id.recyclerViewBookEdition);

        recyclerViewBookEdition.setLayoutManager(new LinearLayoutManager(SelectedBookActivity.this));
        recyclerViewBookEdition.addItemDecoration(new RecyclerViewItemDecoration(this));
        recyclerViewBookEdition.setNestedScrollingEnabled(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeIntentAuthor("");
                executeIntentGenre("");
                executeIntentBookEdition();
                executeIntentBookImageMedium();
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

                        StringBuilder result = null;
                        boolean first = true;

                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_GET_AUTHOR:
                                authorTaskIsFinished = true;

                                result = new StringBuilder();
                                first = true;

                                for (int i = 0; i < responseJsonArray.length(); i++) {
                                    try {
                                        JSONObject responseJsonObject = responseJsonArray.getJSONObject(i);
                                        String name = (String) responseJsonObject.get("name");

                                        if (first)
                                            first = false;
                                        else
                                            result.append(", ");

                                        result.append(name);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                authorOfBook = result.toString();
                                textViewBookAuthor.setText(authorOfBook);

                                break;
                            case APIAsyncTask.ACTION_GET_GENRE:
                                genreTaskIsFinished = true;

                                result = new StringBuilder();
                                first = true;

                                for (int i = 0; i < responseJsonArray.length(); i++) {
                                    try {
                                        JSONObject responseJsonObject = responseJsonArray.getJSONObject(i);
                                        String name = (String) responseJsonObject.get("name");

                                        if (first)
                                            first = false;
                                        else
                                            result.append(", ");

                                        result.append(name);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                genreOfBook = result.toString();
                                textViewBookGenre.setText(genreOfBook);

                                break;
                            case APIAsyncTask.ACTION_GET_BOOK_EDITION:
                                bookEditionTaskIsFinished = true;

                                fillRecyclerView = new FillRecyclerView(responseJsonArray);
                                fillRecyclerView.execute();

                                break;
                            case APIAsyncTask.ACTION_GET_BOOK_IMAGE_MEDIUM:
                                bookImageMediumTaskIsFinished = true;

                                try {
                                    path = responseJsonArray.getJSONObject(0).get("path").toString();

                                    imageFetcher.loadImage(APIAsyncTask.serverAddress + APIAsyncTask.apiPath + path, imageViewBookMedium);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;
                        }

                        if (authorTaskIsFinished && genreTaskIsFinished && bookEditionTaskIsFinished && bookImageMediumTaskIsFinished && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        break;
                    case APIAsyncTask.RESPONSE_ERROR:
                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_GET_AUTHOR:
                                authorTaskIsFinished = true;

                                break;
                            case APIAsyncTask.ACTION_GET_GENRE:
                                genreTaskIsFinished = true;

                                break;
                            case APIAsyncTask.ACTION_GET_BOOK_EDITION:
                                bookEditionTaskIsFinished = true;

                                break;
                            case APIAsyncTask.ACTION_GET_BOOK_IMAGE_MEDIUM:
                                bookImageMediumTaskIsFinished = true;

                                break;
                        }

                        if (authorTaskIsFinished && genreTaskIsFinished && bookEditionTaskIsFinished && bookImageMediumTaskIsFinished && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

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

            Intent intentSelectedBook = getIntent();
            queryOfBook = intentSelectedBook.getStringExtra(BookActivity.EXTRA_QUERY_OF_BOOK);
            keyOfBook = intentSelectedBook.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK);

            textViewBookName.setText(queryOfBook);
            textViewBookOLID.setText(keyOfBook);

            executeIntentAuthor("");
            executeIntentGenre("");
            executeIntentBookEdition();
            executeIntentBookImageMedium();

        }

        getSupportActionBar().setTitle(queryOfBook);

        if (!queryOfBook.isEmpty()) {
            textViewBookName.setText(queryOfBook);
        }

        if (!keyOfBook.isEmpty()) {
            textViewBookOLID.setText(keyOfBook);
        }

        if (!authorOfBook.isEmpty()) {
            textViewBookAuthor.setText(authorOfBook);
        }

        if (!genreOfBook.isEmpty()) {
            textViewBookGenre.setText(genreOfBook);
        }

        itemList = retainedSelectedBookFragment.getItemList();

        if (itemList.size() != 0) {
            adapter = new BookEditionAdapter(SelectedBookActivity.this, itemList, queryOfBook, keyOfBook);
            recyclerViewBookEdition.setAdapter(adapter);
        }

        imageFetcher.loadImage(APIAsyncTask.serverAddress + APIAsyncTask.apiPath + path, imageViewBookMedium);
    }

    @Override
    protected void onPause() {
        super.onPause();

        imageFetcher.setPauseWork(false);
        imageFetcher.setExitTasksEarly(true);
        imageFetcher.flushCache();
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

        imageFetcher.closeCache();

        retainedSelectedBookFragment.setItemList(itemList);
    }

    @Override
    public void onClick(View v) {

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

        fragmentManager.beginTransaction().remove(retainedSelectedBookFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(BookActivity.EXTRA_QUERY_OF_BOOK, queryOfBook);
        outState.putString(EXTRA_KEY_OF_BOOK, keyOfBook);
        outState.putString("AUTHOR_OF_BOOK", authorOfBook);
        outState.putString("GENRE_OF_BOOK", genreOfBook);
        outState.putString("PATH", path);

        outState.putBoolean("FIRST_LAUNCH", firstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        queryOfBook = savedInstanceState.getString(BookActivity.EXTRA_QUERY_OF_BOOK);
        keyOfBook = savedInstanceState.getString(EXTRA_KEY_OF_BOOK);
        authorOfBook = savedInstanceState.getString("AUTHOR_OF_BOOK");
        genreOfBook = savedInstanceState.getString("GENRE_OF_BOOK");
        path = savedInstanceState.getString("PATH");

        firstLaunch = savedInstanceState.getBoolean("FIRST_LAUNCH");
    }

    private void executeIntentAuthor(String query) {
        authorTaskIsFinished = false;

        Intent intentAuthor = new Intent(this, APIAsyncTask.class)
                .putExtra(BookActivity.EXTRA_QUERY_OF_BOOK, query)
                .putExtra(EXTRA_KEY_OF_BOOK, keyOfBook)
                .setAction(APIAsyncTask.ACTION_GET_AUTHOR);

        if (authorTask != null && authorTask.getStatus() == AsyncTask.Status.RUNNING) {
            authorTask.cancel(false);
        }

        authorTask = new APIAsyncTask(this);
        authorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentAuthor);
    }

    private void executeIntentGenre(String query) {
        genreTaskIsFinished = false;

        Intent intentGenre = new Intent(this, APIAsyncTask.class)
                .putExtra(BookActivity.EXTRA_QUERY_OF_BOOK, query)
                .putExtra(EXTRA_KEY_OF_BOOK, keyOfBook)
                .setAction(APIAsyncTask.ACTION_GET_GENRE);

        if (genreTask != null && genreTask.getStatus() == AsyncTask.Status.RUNNING) {
            genreTask.cancel(false);
        }

        genreTask = new APIAsyncTask(this);
        genreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentGenre);
    }

    private void executeIntentBookEdition() {
        bookEditionTaskIsFinished = false;

        Intent intentBookEdition = new Intent(this, APIAsyncTask.class)
                .putExtra(EXTRA_KEY_OF_BOOK, keyOfBook)
                .setAction(APIAsyncTask.ACTION_GET_BOOK_EDITION);

        if (bookEditionTask != null && bookEditionTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookEditionTask.cancel(false);
        }

        bookEditionTask = new APIAsyncTask(this);
        bookEditionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentBookEdition);
    }

    private void executeIntentBookImageMedium() {
        bookImageMediumTaskIsFinished = false;

        Intent intentBookImageMedium = new Intent(this, APIAsyncTask.class)
                .putExtra(EXTRA_KEY_OF_BOOK, keyOfBook)
                .setAction(APIAsyncTask.ACTION_GET_BOOK_IMAGE_MEDIUM);

        if (bookImageMediumTask != null && bookImageMediumTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookImageMediumTask.cancel(false);
        }

        bookImageMediumTask = new APIAsyncTask(this);
        bookImageMediumTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentBookImageMedium);
    }

    private void killAllTask() {
        if (authorTask != null && authorTask.getStatus() == AsyncTask.Status.RUNNING) {
            authorTask.cancel(false);
        }

        if (genreTask != null && genreTask.getStatus() == AsyncTask.Status.RUNNING) {
            genreTask.cancel(false);
        }

        if (bookEditionTask != null && bookEditionTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookEditionTask.cancel(false);
        }

        if (fillRecyclerView != null && fillRecyclerView.getStatus() == AsyncTask.Status.RUNNING) {
            fillRecyclerView.cancel(false);
        }
    }

    class FillRecyclerView extends AsyncTask<Void, Void, Void> {
        JSONArray responseJsonArray;

        public FillRecyclerView(JSONArray responseJsonArray) {
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
                    String language = (String) responseJsonObject.get("l_name");
                    String publisher = (String) responseJsonObject.get("p_name");

                    itemList.add(new BookEditionItem(language, publisher));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new BookEditionAdapter(SelectedBookActivity.this, itemList, queryOfBook, keyOfBook);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!isCancelled()) {
                recyclerViewBookEdition.setAdapter(adapter);
            }
        }
    }
}
