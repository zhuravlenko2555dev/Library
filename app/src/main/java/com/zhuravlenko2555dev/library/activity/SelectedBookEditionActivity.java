package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SelectedBookEditionActivity extends AppCompatActivity implements View.OnClickListener {
    public static String EXTRA_LANGUAGE = "extra_language";
    public static String EXTRA_PUBLISHER = "extra_publisher";
    public static String EXTRA_MY_VALUE = "extra_my_value";

    enum CURRENT_STATUS_READ {
        READ, NOT_READ
    }
    enum CURRENT_STATUS_FAVORITE {
        FAVORITE, NOT_FAVORITE
    }

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewBookName, textViewBookAuthor, textViewBookGenre, textViewBookOLID, textViewBookEditionLanguage, textViewBookEditionPublishDate, textViewBookEditionPublisher, textViewBookEditionNumberOfPages, textViewBookEditionDescription, textViewBookEditionCount, textViewBookEditionDateOfAddition, textViewCountFive, textViewCountFour, textViewCountThree, textViewCountTwo, textViewCountOne, textViewAVGValue;
    private ImageView imageViewBookMedium, imageViewBookEditionFavorite;
    private Button buttonTakeOrReturn;
    private RatingBar ratingBarMyValue;

    private Dialog dialog, alertDialogNetworkConnectionError;
    private AlertDialog.Builder builderNetworkConnectionError;

    private String queryOfBook = "";
    private String keyOfBook = "";
    private String authorOfBook = "";
    private String genreOfBook = "";
    private String language = "";
    private String publishDate = "";
    private String publisher = "";
    private int numberOfPages = 0;
    private String description = "";
    private int count = 0;
    private String dateOfAddition = "";
    private int myValue = 0;
    private double AVGValue = 0;
    private int countFive = 0;
    private int countFour = 0;
    private int countThree = 0;
    private int countTwo = 0;
    private int countOne = 0;
    private CURRENT_STATUS_FAVORITE currentStatusFavorite;
    private CURRENT_STATUS_READ currentStatusRead;

    private boolean firstLaunch = true;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask authorTask = null;
    private APIAsyncTask genreTask = null;
    private APIAsyncTask selectedBookEditionTask = null;
    private APIAsyncTask favoriteTask = null;
    private APIAsyncTask takeABookTask = null;
    private APIAsyncTask returnABookTask = null;
    private APIAsyncTask setFavoriteTask = null;
    private APIAsyncTask deleteFavoriteTask = null;
    private APIAsyncTask ratingTask = null;
    private APIAsyncTask setRatingTask = null;
    private APIAsyncTask deleteRatingTask = null;

    private boolean authorTaskIsFinished = true;
    private boolean genreTaskIsFinished = true;
    private boolean selectedBookEditionTaskIsFinished = true;
    private boolean favoriteTaskIsFinished = true;
    private boolean ratingTaskIsFinished = true;

    private ActivityManager activityManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_book_edition);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        textViewBookName = findViewById(R.id.textViewBookName);
        textViewBookAuthor = findViewById(R.id.textViewBookAuthor);
        textViewBookGenre = findViewById(R.id.textViewBookGenre);
        textViewBookOLID = findViewById(R.id.textViewBookOLID);
        imageViewBookMedium = findViewById(R.id.imageViewBookMedium);
        textViewBookEditionLanguage = findViewById(R.id.textViewBookEditionLanguage);
        textViewBookEditionPublishDate = findViewById(R.id.textViewBookEditionPublishDate);
        textViewBookEditionPublisher = findViewById(R.id.textViewBookEditionPublisher);
        textViewBookEditionNumberOfPages = findViewById(R.id.textViewBookEditionNumberOfPages);
        textViewBookEditionDescription = findViewById(R.id.textViewBookEditionDescription);
        textViewBookEditionCount = findViewById(R.id.textViewBookEditionCount);
        textViewBookEditionDateOfAddition = findViewById(R.id.textViewBookEditionDateOfAddition);
        imageViewBookEditionFavorite = findViewById(R.id.imageViewBookEditionFavorite);
        ratingBarMyValue = findViewById(R.id.ratingBarMyValue);
        textViewAVGValue = findViewById(R.id.textViewAVGValue);
        textViewCountFive = findViewById(R.id.textViewCountFive);
        textViewCountFour = findViewById(R.id.textViewCountFour);
        textViewCountThree = findViewById(R.id.textViewCountThree);
        textViewCountTwo = findViewById(R.id.textViewCountTwo);
        textViewCountOne = findViewById(R.id.textViewCountOne);
        buttonTakeOrReturn = findViewById(R.id.buttonTakeOrReturn);

        imageViewBookEditionFavorite.setOnClickListener(this);
        buttonTakeOrReturn.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeIntentAuthor("");
                executeIntentGenre("");
                executeIntentSelectedBookEdition();
                executeIntentFavorite();
                executeIntentRatingTask();
            }
        });

        ratingBarMyValue.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    if (rating != 0.0) {
                        Intent intentSetRatingTask = new Intent(SelectedBookEditionActivity.this, APIAsyncTask.class)
                                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                                .putExtra(EXTRA_LANGUAGE, language)
                                .putExtra(EXTRA_PUBLISHER, publisher)
                                .putExtra(EXTRA_MY_VALUE, Integer.toString((int) rating))
                                .setAction(APIAsyncTask.ACTION_SET_RATING);

                        if (setRatingTask != null && setRatingTask.getStatus() == AsyncTask.Status.RUNNING) {
                            setRatingTask.cancel(false);
                        }

                        setRatingTask = new APIAsyncTask(SelectedBookEditionActivity.this);
                        setRatingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentSetRatingTask);
                    } else {
                        Intent intentDeleteRatingTask = new Intent(SelectedBookEditionActivity.this, APIAsyncTask.class)
                                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                                .putExtra(EXTRA_LANGUAGE, language)
                                .putExtra(EXTRA_PUBLISHER, publisher)
                                .setAction(APIAsyncTask.ACTION_DELETE_RATING);

                        if (deleteRatingTask != null && deleteRatingTask.getStatus() == AsyncTask.Status.RUNNING) {
                            deleteRatingTask.cancel(false);
                        }

                        deleteRatingTask = new APIAsyncTask(SelectedBookEditionActivity.this);
                        deleteRatingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentDeleteRatingTask);
                    }
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

                        StringBuilder result;
                        boolean first;

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
                            case APIAsyncTask.ACTION_GET_SELECTED_BOOK_EDITION:
                                selectedBookEditionTaskIsFinished = true;

                                try {
                                    JSONObject responseJsonObject = responseJsonArray.getJSONObject(0);
                                    language = (String) responseJsonObject.get("l_name");
                                    publishDate = !responseJsonObject.isNull("publish_date") ? (String) responseJsonObject.get("publish_date") : "";
                                    publisher = (String) responseJsonObject.get("p_name");
                                    numberOfPages = (Integer) responseJsonObject.get("number_of_pages");
                                    description = !responseJsonObject.isNull("description") ? (String) responseJsonObject.get("description") : "";
                                    count = (Integer) responseJsonObject.get("count");
                                    dateOfAddition = (String) responseJsonObject.get("date_of_addition");

                                    textViewBookEditionLanguage.setText(language);
                                    textViewBookEditionPublishDate.setText(publishDate);
                                    textViewBookEditionPublisher.setText(publisher);
                                    textViewBookEditionNumberOfPages.setText(Integer.toString(numberOfPages));
                                    textViewBookEditionDescription.setText(description);
                                    textViewBookEditionCount.setText(Integer.toString(count));
                                    textViewBookEditionDateOfAddition.setText(dateOfAddition);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;

                            case APIAsyncTask.ACTION_GET_FAVORITE:
                                favoriteTaskIsFinished = true;

                                currentStatusFavorite = CURRENT_STATUS_FAVORITE.FAVORITE;
                                imageViewBookEditionFavorite.setEnabled(true);
                                imageViewBookEditionFavorite.setAlpha(1.0f);
                                break;
                            case APIAsyncTask.ACTION_SET_FAVORITE:
                                currentStatusFavorite = CURRENT_STATUS_FAVORITE.FAVORITE;
                                imageViewBookEditionFavorite.setEnabled(true);
                                imageViewBookEditionFavorite.setAlpha(1.0f);
                                break;
                            case APIAsyncTask.ACTION_DELETE_FAVORITE:
                                currentStatusFavorite = CURRENT_STATUS_FAVORITE.NOT_FAVORITE;
                                imageViewBookEditionFavorite.setEnabled(true);
                                imageViewBookEditionFavorite.setAlpha(0.5f);
                                break;

                            case APIAsyncTask.ACTION_GET_RATING:
                                ratingTaskIsFinished = true;

                                try {
                                    Log.d("ACTION_GET_RATING", "ACTION_GET_RATING");

                                    JSONObject responseJsonObject = responseJsonArray.getJSONObject(0);
                                    myValue = !responseJsonObject.isNull("my_value") ? Integer.parseInt((String) responseJsonObject.get("my_value")) : 0;
                                    AVGValue = !responseJsonObject.isNull("avg_value") ? (double) ((int) responseJsonObject.get("avg_value")) : 0.0;
                                    countFive = (Integer) responseJsonObject.get("count_five");
                                    countFour = (Integer) responseJsonObject.get("count_four");
                                    countThree = (Integer) responseJsonObject.get("count_three");
                                    countTwo = (Integer) responseJsonObject.get("count_two");
                                    countOne = (Integer) responseJsonObject.get("count_one");

                                    ratingBarMyValue.setRating(myValue);
                                    textViewAVGValue.setText(Double.toString(AVGValue));
                                    textViewCountFive.setText(Integer.toString(countFive));
                                    textViewCountFour.setText(Integer.toString(countFour));
                                    textViewCountThree.setText(Integer.toString(countThree));
                                    textViewCountTwo.setText(Integer.toString(countTwo));
                                    textViewCountOne.setText(Integer.toString(countOne));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                break;
                            case APIAsyncTask.ACTION_SET_RATING:
                                break;
                            case APIAsyncTask.ACTION_DELETE_RATING:
                                break;

                            case APIAsyncTask.ACTION_TAKE_A_BOOK:
                                currentStatusRead = CURRENT_STATUS_READ.READ;
                                buttonTakeOrReturn.setEnabled(true);
                                buttonTakeOrReturn.setText("Return a book");
                                break;
                            case APIAsyncTask.ACTION_RETURN_A_BOOK:
                                currentStatusRead = CURRENT_STATUS_READ.NOT_READ;
                                buttonTakeOrReturn.setEnabled(true);
                                buttonTakeOrReturn.setText("Take a book");
                                break;
                        }

                        if (authorTaskIsFinished && genreTaskIsFinished && selectedBookEditionTaskIsFinished && favoriteTaskIsFinished && ratingTaskIsFinished && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        break;
                    case APIAsyncTask.RESPONSE_ERROR:
                        String responseError = intent.getStringExtra(APIAsyncTask.EXTRA_RESPONSE_ERROR);

                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_TAKE_A_BOOK:
                                currentStatusRead = CURRENT_STATUS_READ.NOT_READ;
                                buttonTakeOrReturn.setEnabled(true);
                                buttonTakeOrReturn.setText("Take a book");

                                Toast toast = Toast.makeText(SelectedBookEditionActivity.this, responseError, Toast.LENGTH_LONG);
                                toast.show();

                                break;
                            case APIAsyncTask.ACTION_GET_AUTHOR:
                                authorTaskIsFinished = true;

                                break;
                            case APIAsyncTask.ACTION_GET_GENRE:
                                genreTaskIsFinished = true;

                                break;
                            case APIAsyncTask.ACTION_GET_SELECTED_BOOK_EDITION:
                                selectedBookEditionTaskIsFinished = true;

                                break;
                            case APIAsyncTask.ACTION_GET_FAVORITE:
                                favoriteTaskIsFinished = true;

                                break;
                            case APIAsyncTask.ACTION_GET_RATING:
                                ratingTaskIsFinished = true;

                                break;
                        }

                        if (authorTaskIsFinished && genreTaskIsFinished && selectedBookEditionTaskIsFinished && favoriteTaskIsFinished && ratingTaskIsFinished && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        break;
                }
            }
        };

        if (savedInstanceState == null) {
            currentStatusFavorite = CURRENT_STATUS_FAVORITE.NOT_FAVORITE;
            currentStatusRead = CURRENT_STATUS_READ.NOT_READ;
            buttonTakeOrReturn.setText("Take a book");
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case APIAsyncTask.ALERT_DIALOG_NETWORK_CONNECTION_ERROR:
                builderNetworkConnectionError = new AlertDialog.Builder(this);
                builderNetworkConnectionError.setTitle("Error");
                builderNetworkConnectionError.setMessage("Network Connection Error");
                builderNetworkConnectionError.setIcon(getResources().getDrawable(R.drawable.network_connection_error));

                alertDialogNetworkConnectionError = builderNetworkConnectionError.create();
                alertDialogNetworkConnectionError.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_background));

                dialog = alertDialogNetworkConnectionError;

                break;
        }

        return dialog;
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
            language = intentSelectedBook.getStringExtra(EXTRA_LANGUAGE);
            publisher = intentSelectedBook.getStringExtra(EXTRA_PUBLISHER);

            textViewBookName.setText(queryOfBook);
            textViewBookOLID.setText(keyOfBook);

            executeIntentAuthor("");
            executeIntentGenre("");
            executeIntentSelectedBookEdition();
            executeIntentFavorite();
            executeIntentRatingTask();
        }

        getSupportActionBar().setTitle(queryOfBook + "(language: " + language + "), (publisher: " + publisher + ")");

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

        if (!language.isEmpty()) {
            textViewBookEditionLanguage.setText(language);
        }

        if (!publishDate.isEmpty()) {
            textViewBookEditionPublishDate.setText(publishDate);
        }

        if (!publisher.isEmpty()) {
            textViewBookEditionPublisher.setText(publisher);
        }

        if (numberOfPages != 0) {
            textViewBookEditionNumberOfPages.setText(Integer.toString(numberOfPages));
        }

        if (!description.isEmpty()) {
            textViewBookEditionDescription.setText(description);
        }

        if (count != 0) {
            textViewBookEditionCount.setText(Integer.toString(count));
        }

        if (!dateOfAddition.isEmpty()) {
            textViewBookEditionDateOfAddition.setText(dateOfAddition);
        }

        if (currentStatusFavorite == CURRENT_STATUS_FAVORITE.FAVORITE) {
            imageViewBookEditionFavorite.setEnabled(true);
            imageViewBookEditionFavorite.setAlpha(1.0f);
        }

        ratingBarMyValue.setRating(myValue);
        textViewAVGValue.setText(Double.toString(AVGValue));
        textViewCountFive.setText(Integer.toString(countFive));
        textViewCountFour.setText(Integer.toString(countFour));
        textViewCountThree.setText(Integer.toString(countThree));
        textViewCountTwo.setText(Integer.toString(countTwo));
        textViewCountOne.setText(Integer.toString(countOne));

        switch (currentStatusRead) {
            case NOT_READ:
                buttonTakeOrReturn.setText("Take a book");

                break;
            case READ:
                buttonTakeOrReturn.setText("Return a book");

                break;
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
            case R.id.imageViewBookEditionFavorite:
                switch (currentStatusFavorite) {
                    case NOT_FAVORITE:
                        imageViewBookEditionFavorite.setEnabled(false);

                        Intent intentSetFavorite = new Intent(this, APIAsyncTask.class)
                                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                                .putExtra(EXTRA_LANGUAGE, language)
                                .putExtra(EXTRA_PUBLISHER, publisher)
                                .setAction(APIAsyncTask.ACTION_SET_FAVORITE);
                        setFavoriteTask = new APIAsyncTask(this);
                        setFavoriteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentSetFavorite);

                        break;
                    case FAVORITE:
                        imageViewBookEditionFavorite.setEnabled(false);

                        Intent intentDeleteFavorite = new Intent(this, APIAsyncTask.class)
                                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                                .putExtra(EXTRA_LANGUAGE, language)
                                .putExtra(EXTRA_PUBLISHER, publisher)
                                .setAction(APIAsyncTask.ACTION_DELETE_FAVORITE);
                        deleteFavoriteTask = new APIAsyncTask(this);
                        deleteFavoriteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentDeleteFavorite);

                        break;
                }

                break;
            case R.id.buttonTakeOrReturn:
                switch (currentStatusRead) {
                    case NOT_READ:
                        buttonTakeOrReturn.setEnabled(false);

                        Intent intentTakeABook = new Intent(this, APIAsyncTask.class)
                                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                                .putExtra(EXTRA_LANGUAGE, language)
                                .putExtra(EXTRA_PUBLISHER, publisher)
                                .setAction(APIAsyncTask.ACTION_TAKE_A_BOOK);
                        takeABookTask = new APIAsyncTask(this);
                        takeABookTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentTakeABook);

                        break;
                    case READ:
                        buttonTakeOrReturn.setEnabled(false);

                        Intent intentReturnABook = new Intent(this, APIAsyncTask.class)
                                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                                .putExtra(EXTRA_LANGUAGE, language)
                                .putExtra(EXTRA_PUBLISHER, publisher)
                                .setAction(APIAsyncTask.ACTION_RETURN_A_BOOK);
                        returnABookTask = new APIAsyncTask(this);
                        returnABookTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentReturnABook);

                        break;
                }

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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(BookActivity.EXTRA_QUERY_OF_BOOK, queryOfBook);
        outState.putString(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook);
        outState.putString("AUTHOR_OF_BOOK", authorOfBook);
        outState.putString("GENRE_OF_BOOK", genreOfBook);
        outState.putString(EXTRA_LANGUAGE, language);
        outState.putString("PUBLISH_DATE", publishDate);
        outState.putString(EXTRA_PUBLISHER, publisher);
        outState.putInt("NUMBER_OF_PAGES", numberOfPages);
        outState.putString("DESCRIPTION", description);
        outState.putInt("COUNT", count);
        outState.putString("DATE_OF_ADDITION", dateOfAddition);
        outState.putInt(EXTRA_MY_VALUE, myValue);
        outState.putDouble("AVG_VALUE", AVGValue);
        outState.putInt("COUNT_FIVE", countFive);
        outState.putInt("COUNT_FOUR", countFour);
        outState.putInt("COUNT_THREE", countThree);
        outState.putInt("COUNT_TWO", countTwo);
        outState.putInt("COUNT_ONE", countOne);
        outState.putString("CURRENT_STATUS_FAVORITE", currentStatusFavorite.toString());
        outState.putString("CURRENT_STATUS_READ", currentStatusRead.toString());

        outState.putBoolean("AUTHOR_TASK_IS_FINISHED", authorTaskIsFinished);
        outState.putBoolean("GENRE_TASK_IS_FINISHED", genreTaskIsFinished);
        outState.putBoolean("SELECTED_BOOK_EDITION_TASK_IS_FINISHED", selectedBookEditionTaskIsFinished);
        outState.putBoolean("FAVORITE_TASK_IS_FINISHED", favoriteTaskIsFinished);
        outState.putBoolean("RATING_TASK_IS_FINISHED", ratingTaskIsFinished);

        outState.putBoolean("SWIPE_REFRESH_LAYOUT", swipeRefreshLayout.isRefreshing());

        outState.putBoolean("FIRST_LAUNCH", firstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        queryOfBook = savedInstanceState.getString(BookActivity.EXTRA_QUERY_OF_BOOK);
        keyOfBook = savedInstanceState.getString(SelectedBookActivity.EXTRA_KEY_OF_BOOK);
        authorOfBook = savedInstanceState.getString("AUTHOR_OF_BOOK");
        genreOfBook = savedInstanceState.getString("GENRE_OF_BOOK");
        language = savedInstanceState.getString(EXTRA_LANGUAGE);
        publishDate = savedInstanceState.getString("PUBLISH_DATE");
        publisher = savedInstanceState.getString(EXTRA_PUBLISHER);
        numberOfPages = savedInstanceState.getInt("NUMBER_OF_PAGES");
        description = savedInstanceState.getString("DESCRIPTION");
        count = savedInstanceState.getInt("COUNT");
        dateOfAddition = savedInstanceState.getString("DATE_OF_ADDITION");
        myValue = savedInstanceState.getInt(EXTRA_MY_VALUE);
        AVGValue = savedInstanceState.getDouble("AVG_VALUE");
        countFive = savedInstanceState.getInt("COUNT_FIVE");
        countFour = savedInstanceState.getInt("COUNT_FOUR");
        countThree = savedInstanceState.getInt("COUNT_THREE");
        countTwo = savedInstanceState.getInt("COUNT_TWO");
        countOne = savedInstanceState.getInt("COUNT_ONE");
        currentStatusFavorite = CURRENT_STATUS_FAVORITE.valueOf(savedInstanceState.getString("CURRENT_STATUS_FAVORITE"));
        currentStatusRead = CURRENT_STATUS_READ.valueOf(savedInstanceState.getString("CURRENT_STATUS_READ"));

        authorTaskIsFinished = savedInstanceState.getBoolean("AUTHOR_TASK_IS_FINISHED");
        genreTaskIsFinished = savedInstanceState.getBoolean("GENRE_TASK_IS_FINISHED");
        selectedBookEditionTaskIsFinished = savedInstanceState.getBoolean("SELECTED_BOOK_EDITION_TASK_IS_FINISHED");
        favoriteTaskIsFinished = savedInstanceState.getBoolean("FAVORITE_TASK_IS_FINISHED");
        ratingTaskIsFinished = savedInstanceState.getBoolean("RATING_TASK_IS_FINISHED");

        swipeRefreshLayout.setRefreshing(savedInstanceState.getBoolean("SWIPE_REFRESH_LAYOUT"));

        firstLaunch = savedInstanceState.getBoolean("FIRST_LAUNCH");
    }

    private void executeIntentAuthor(String query) {
        authorTaskIsFinished = false;

        Intent intentAuthor = new Intent(this, APIAsyncTask.class)
                .putExtra(BookActivity.EXTRA_QUERY_OF_BOOK, query)
                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                .setAction(APIAsyncTask.ACTION_GET_AUTHOR);
        authorTask = new APIAsyncTask(this);
        authorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentAuthor);
    }

    private void executeIntentGenre(String query) {
        genreTaskIsFinished = false;

        Intent intentGenre = new Intent(this, APIAsyncTask.class)
                .putExtra(BookActivity.EXTRA_QUERY_OF_BOOK, query)
                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                .setAction(APIAsyncTask.ACTION_GET_GENRE);
        genreTask = new APIAsyncTask(this);
        genreTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentGenre);
    }

    private void executeIntentSelectedBookEdition() {
        selectedBookEditionTaskIsFinished = false;

        Intent intentSelectedBookEdition = new Intent(this, APIAsyncTask.class)
                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                .putExtra(EXTRA_LANGUAGE, language)
                .putExtra(EXTRA_PUBLISHER, publisher)
                .setAction(APIAsyncTask.ACTION_GET_SELECTED_BOOK_EDITION);
        selectedBookEditionTask = new APIAsyncTask(this);
        selectedBookEditionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentSelectedBookEdition);
    }

    private void executeIntentFavorite() {
        favoriteTaskIsFinished = false;

        Intent intentFavorite = new Intent(this, APIAsyncTask.class)
                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                .putExtra(EXTRA_LANGUAGE, language)
                .putExtra(EXTRA_PUBLISHER, publisher)
                .setAction(APIAsyncTask.ACTION_GET_FAVORITE);
        favoriteTask = new APIAsyncTask(this);
        favoriteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentFavorite);
    }

    private void executeIntentRatingTask() {
        ratingTaskIsFinished = false;

        Intent intentRatingTask = new Intent(this, APIAsyncTask.class)
                .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, AccountAuthLocalStore.getAccountAuth().getLogin())
                .putExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK, keyOfBook)
                .putExtra(EXTRA_LANGUAGE, language)
                .putExtra(EXTRA_PUBLISHER, publisher)
                .setAction(APIAsyncTask.ACTION_GET_RATING);
        ratingTask = new APIAsyncTask(this);
        ratingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, intentRatingTask);
    }

    private void killAllTask() {
        if (authorTask != null && authorTask.getStatus() == AsyncTask.Status.RUNNING) {
            authorTask.cancel(false);
        }

        if (genreTask != null && genreTask.getStatus() == AsyncTask.Status.RUNNING) {
            genreTask.cancel(false);
        }

        if (selectedBookEditionTask != null && selectedBookEditionTask.getStatus() == AsyncTask.Status.RUNNING) {
            selectedBookEditionTask.cancel(false);
        }

        if (favoriteTask != null && favoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            favoriteTask.cancel(false);
        }

        if (takeABookTask != null && takeABookTask.getStatus() == AsyncTask.Status.RUNNING) {
            takeABookTask.cancel(false);
        }

        if (returnABookTask != null && returnABookTask.getStatus() == AsyncTask.Status.RUNNING) {
            returnABookTask.cancel(false);
        }

        if (setFavoriteTask != null && setFavoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            setFavoriteTask.cancel(false);
        }

        if (deleteFavoriteTask != null && deleteFavoriteTask.getStatus() == AsyncTask.Status.RUNNING) {
            deleteFavoriteTask.cancel(false);
        }

        if (ratingTask != null && ratingTask.getStatus() == AsyncTask.Status.RUNNING) {
            ratingTask.cancel(false);
        }

        if (setRatingTask != null && setRatingTask.getStatus() == AsyncTask.Status.RUNNING) {
            setRatingTask.cancel(false);
        }

        if (deleteRatingTask != null && deleteRatingTask.getStatus() == AsyncTask.Status.RUNNING) {
            deleteRatingTask.cancel(false);
        }
    }
}
