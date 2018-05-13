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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.fragment.RetainedBookFragment;
import com.zhuravlenko2555dev.library.provider.BookAdapter;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;
import com.zhuravlenko2555dev.library.provider.BookItem;
import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.util.ImageCache;
import com.zhuravlenko2555dev.library.util.ImageFetcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements View.OnClickListener {
    public static String EXTRA_QUERY_OF_BOOK = "extra_query_of_book";
    public static String EXTRA_NUMBER_OF_BOOK_LIST_PAGE = "extra_number_of_book_list_page";
    public static String EXTRA_SIZE_OF_BOOK_LIST = "extra_size_of_book_list";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listViewBook;
    private ImageButton imageButtonArrowLeft, imageButtonArrowRight;
    private TextView textViewNumberOfPage;

    private FragmentManager fragmentManager;
    private RetainedBookFragment retainedBookFragment;

    private String queryOfBook = "";
    private int numberOfBookListPage = 1;
    private int sizeOfBookList = 100;
    private int countOfBook = 0;
    private int countOfBookListPage = 0;

    private ArrayList<BookItem> itemList;
    private BookAdapter adapter;

    private boolean firstLaunch = true;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask bookTask = null;
    private APIAsyncTask bookCountTask = null;
    private FillListView fillListViewTask = null;

    boolean bookTaskIsFinished = true;
    boolean bookCountTaskIsFinished = true;

    private ActivityManager activityManager = null;

    private static final String IMAGE_CACHE_DIR = "image_book_small";

    private int imageViewBookSmallWidth;
    private int imageViewBookSmallHeight;
    public ImageFetcher imageFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        imageViewBookSmallWidth = getResources().getDimensionPixelSize(R.dimen.image_view_book_small_width);
        imageViewBookSmallHeight = getResources().getDimensionPixelSize(R.dimen.image_view_book_small_height);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        imageFetcher = new ImageFetcher(this, imageViewBookSmallWidth, imageViewBookSmallHeight);
        imageFetcher.setLoadingImage(R.drawable.placeholder_book);
        imageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        fragmentManager = getSupportFragmentManager();
        retainedBookFragment = (RetainedBookFragment) fragmentManager.findFragmentByTag(RetainedBookFragment.FRAGMENT_TAG);

        if (retainedBookFragment == null) {
            retainedBookFragment  = new RetainedBookFragment();
            fragmentManager.beginTransaction().add(retainedBookFragment, RetainedBookFragment.FRAGMENT_TAG).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        listViewBook = findViewById(R.id.listViewBook);
        imageButtonArrowLeft = findViewById(R.id.imageButtonArrowLeft);
        imageButtonArrowRight = findViewById(R.id.imageButtonArrowRight);
        textViewNumberOfPage = findViewById(R.id.textViewNumberOfPage);

        imageButtonArrowLeft.setOnClickListener(this);
        imageButtonArrowRight.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeIntentBook(queryOfBook);
                executeIntentBookCount(queryOfBook);
            }
        });

        swipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                if (listViewBook != null)
                    return listViewBook.canScrollVertically(-1);

                return false;
            }
        });

        listViewBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                killAllTask();

                Intent intentSelectedBook = new Intent(BookActivity.this, SelectedBookActivity.class);
                intentSelectedBook.putExtra(BookActivity.EXTRA_QUERY_OF_BOOK, itemList.get(position).getName());
                intentSelectedBook.putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, itemList.get(position).getKey());
                startActivity(intentSelectedBook);
            }
        });

        listViewBook.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                } else {
                    imageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
                            case APIAsyncTask.ACTION_GET_BOOK:
                                bookTaskIsFinished = true;

                                fillListViewTask = new FillListView(responseJsonArray);
                                fillListViewTask.execute();

                                break;
                            case APIAsyncTask.ACTION_GET_BOOK_COUNT:
                                bookCountTaskIsFinished = true;

                                int rOK = Integer.parseInt(responseOk);

                                countOfBook = rOK;
                                countOfBookListPage = rOK / sizeOfBookList;

                                if (rOK % sizeOfBookList > 0) {
                                    countOfBookListPage++;
                                }

                                textViewNumberOfPage.setText(Integer.toString(numberOfBookListPage) + "/" + Integer.toString(countOfBookListPage) + " (" + Integer.toString(countOfBook) + ")");

                                break;
                        }

                        if (bookTaskIsFinished && bookCountTaskIsFinished && swipeRefreshLayout.isRefreshing()) {
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

        imageFetcher.setExitTasksEarly(false);

        if (firstLaunch) {
            firstLaunch = false;

            executeIntentBook(queryOfBook);
            executeIntentBookCount(queryOfBook);
        }

        if (retainedBookFragment.getItemList().size() != 0) {
            itemList = retainedBookFragment.getItemList();
            adapter = new BookAdapter(this, R.layout.item_book, itemList);
            listViewBook.setAdapter(adapter);
        }

        textViewNumberOfPage.setText(Integer.toString(numberOfBookListPage) + "/" + Integer.toString(countOfBookListPage) + " (" + Integer.toString(countOfBook) + ")");
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

        retainedBookFragment.setItemList(itemList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonArrowLeft:
                if (numberOfBookListPage != 1) {
                    numberOfBookListPage--;

                    textViewNumberOfPage.setText(Integer.toString(numberOfBookListPage) + "/" + Integer.toString(countOfBookListPage) + " (" + Integer.toString(countOfBook) + ")");

                    executeIntentBook(queryOfBook);
                }

                break;
            case R.id.imageButtonArrowRight:
                if (numberOfBookListPage != countOfBookListPage) {
                    numberOfBookListPage++;

                    textViewNumberOfPage.setText(Integer.toString(numberOfBookListPage) + "/" + Integer.toString(countOfBookListPage) + " (" + Integer.toString(countOfBook) + ")");

                    executeIntentBook(queryOfBook);
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_book).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);

        menu.add(0, 3, 3, getResources().getString(R.string.settings))
                .setIcon(R.drawable.settings)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                .setIntent(new Intent(this, PrefActivity.class));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!queryOfBook.equals(query)) {
                    queryOfBook = query;
                    numberOfBookListPage = 1;
                    sizeOfBookList = 100;

                    executeIntentBook(query);
                    executeIntentBookCount(query);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!queryOfBook.equals(newText)) {
                    queryOfBook = newText;
                    numberOfBookListPage = 1;
                    sizeOfBookList = 100;

                    executeIntentBook(newText);
                    executeIntentBookCount(newText);
                }

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                break;
            /*case R.id.sort_book:
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        killAllTask();

        fragmentManager.beginTransaction().remove(retainedBookFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_QUERY_OF_BOOK, queryOfBook);
        outState.putInt(EXTRA_NUMBER_OF_BOOK_LIST_PAGE, numberOfBookListPage);
        outState.putInt(EXTRA_SIZE_OF_BOOK_LIST, sizeOfBookList);
        outState.putInt("COUNT_OF_BOOK", countOfBook);
        outState.putInt("COUNT_OF_BOOK_LIST_PAGE", countOfBookListPage);

        outState.putBoolean("BOOK_TASK_IS_FINISHED", bookTaskIsFinished);
        outState.putBoolean("BOOK_COUNT_TASK_IS_FINISHED", bookCountTaskIsFinished);

        outState.putBoolean("SWIPE_REFRESH_LAYOUT", swipeRefreshLayout.isRefreshing());

        outState.putBoolean("FIRST_LAUNCH", firstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        queryOfBook = savedInstanceState.getString(EXTRA_QUERY_OF_BOOK);
        numberOfBookListPage = savedInstanceState.getInt(EXTRA_NUMBER_OF_BOOK_LIST_PAGE);
        sizeOfBookList = savedInstanceState.getInt(EXTRA_SIZE_OF_BOOK_LIST);
        countOfBook = savedInstanceState.getInt("COUNT_OF_BOOK");
        countOfBookListPage = savedInstanceState.getInt("COUNT_OF_BOOK_LIST_PAGE");

        bookTaskIsFinished = savedInstanceState.getBoolean("BOOK_TASK_IS_FINISHED");
        bookCountTaskIsFinished = savedInstanceState.getBoolean("BOOK_COUNT_TASK_IS_FINISHED");

        swipeRefreshLayout.setRefreshing(savedInstanceState.getBoolean("SWIPE_REFRESH_LAYOUT"));

        firstLaunch = savedInstanceState.getBoolean("FIRST_LAUNCH");
    }

    private void executeIntentBook(String query) {
        bookTaskIsFinished = false;

        Intent intentBook = new Intent(BookActivity.this, APIAsyncTask.class)
                .putExtra(EXTRA_QUERY_OF_BOOK, query)
                .putExtra(EXTRA_NUMBER_OF_BOOK_LIST_PAGE, numberOfBookListPage)
                .putExtra(EXTRA_SIZE_OF_BOOK_LIST, sizeOfBookList)
                .setAction(APIAsyncTask.ACTION_GET_BOOK);

        if (bookTask != null && bookTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookTask.cancel(false);
        }

        if (fillListViewTask != null && fillListViewTask.getStatus() == AsyncTask.Status.RUNNING) {
            fillListViewTask.cancel(false);
        }

        bookTask = new APIAsyncTask(this);
        bookTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentBook);
    }

    private void executeIntentBookCount(String query) {
        bookCountTaskIsFinished = false;

        Intent intentBookCount = new Intent(BookActivity.this, APIAsyncTask.class)
                .putExtra(EXTRA_QUERY_OF_BOOK, query)
                .setAction(APIAsyncTask.ACTION_GET_BOOK_COUNT);

        if (bookCountTask != null && bookCountTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookCountTask.cancel(false);
        }

        bookCountTask = new APIAsyncTask(this);
        bookCountTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentBookCount);
    }

    private void killAllTask() {
        if (bookTask != null && bookTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookTask.cancel(false);
        }

        if (bookCountTask != null && bookCountTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookCountTask.cancel(false);
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
                    String key = (String) responseJsonObject.get("key");
                    String name = (String) responseJsonObject.get("name");
                    String ipv4 = !responseJsonObject.isNull("ipv4") ? (String) responseJsonObject.get("ipv4") : "";
                    String imageSmall = !responseJsonObject.isNull("path") ? (String) responseJsonObject.get("path") : "";

                    itemList.add(new BookItem(key, name, ipv4, imageSmall));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new BookAdapter(BookActivity.this, R.layout.item_book, itemList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!isCancelled()) {
                listViewBook.setAdapter(adapter);
            }
        }
    }

}
