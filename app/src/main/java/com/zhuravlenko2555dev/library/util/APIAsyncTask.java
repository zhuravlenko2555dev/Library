package com.zhuravlenko2555dev.library.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.activity.BookActivity;
import com.zhuravlenko2555dev.library.activity.FullAddressActivity;
import com.zhuravlenko2555dev.library.activity.RegisterActivity;
import com.zhuravlenko2555dev.library.activity.SelectedBookActivity;
import com.zhuravlenko2555dev.library.activity.SelectedBookEditionActivity;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class APIAsyncTask extends AsyncTask<Intent, Void, Void> {
    public static String serverAddress = "";
    /*public static String serverAddress = "http://192.168.0.101/";*/
    /*public static String serverAddress = "http://192.168.43.145/";*/
    public static String apiPath = "library/";

    private HashMap<String, String> postDataParams;
    private Context context;

    public static boolean isRegistered = false;
    private boolean isReceived = false;

    /** Actions **/
    public static final String ACTION_ACCOUNT_AUTHENTICATE = "action_account_authenticate";
    public static final String ACTION_ACCOUNT_REGISTER = "action_account_register";
    public static final String ACTION_ACCOUNT_CHECK_ROLE = "action_account_check_role";

    public static final String ACTION_GET_READERS_IN_LIBRARY = "action_get_readers_in_library";
    public static final String ACTION_GET_READERS_IN_LIBRARY_LOG = "action_get_readers_in_library_log";

    public static final String ACTION_GET_GENDER = "action_get_gender";
    public static final String ACTION_GET_COUNTRY = "action_get_country";
    public static final String ACTION_GET_CITY = "action_get_city";
    public static final String ACTION_GET_ADDRESS = "action_get_address";

    public static final String ACTION_GET_BOOK = "action_get_book";
    public static final String ACTION_GET_BOOK_COUNT = "action_get_book_count";
    public static final String ACTION_GET_AUTHOR = "action_get_author";
    public static final String ACTION_GET_GENRE = "action_get_genre";
    public static final String ACTION_GET_BOOK_EDITION = "action_get_book_edition";
    public static final String ACTION_GET_SELECTED_BOOK_EDITION = "action_get_selected_book_edition";
    public static final String ACTION_GET_BOOK_IMAGE_MEDIUM = "action_get_book_image_medium";

    public static final String ACTION_GET_FAVORITE = "action_get_favorite";
    public static final String ACTION_SET_FAVORITE = "action_set_favorite";
    public static final String ACTION_DELETE_FAVORITE = "action_delete_favorite";

    public static final String ACTION_GET_RATING = "action_get_rating";
    public static final String ACTION_SET_RATING = "action_set_rating";
    public static final String ACTION_DELETE_RATING = "action_delete_rating";

    public static final String ACTION_TAKE_A_BOOK = "action_take_a_book";
    public static final String ACTION_RETURN_A_BOOK = "action_return_a_book";

    public static final String RESPONSE_OK = "response_ok";
    public static final String RESPONSE_ERROR = "response_error";
    public static final String START_REQUEST = "start_request";

    /** Dialogs **/
    public static final int ALERT_DIALOG_NETWORK_CONNECTION_ERROR = 0;
    public static final int PROGRESS_DIALOG_PROCESSING = 1;

    /** Extras **/
    public static final String EXTRA_REQUEST_ACTION = "request_action";
    public static final String EXTRA_RESPONSE_OK = "extra_response_ok";
    public static final String EXTRA_RESPONSE_ERROR = "extra_response_error";

    public APIAsyncTask(Context context) {
        this.context = context;

        serverAddress = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.serverAddress), "");
    }

    @Override
    protected Void doInBackground(Intent... intents) {
        postDataParams = new HashMap<>();
        String apiLastString = "";
        String requestAction = "";

        postDataParams.put("HTTP_ACCEPT", "application/json");

        Intent intent = intents[0];

        switch (intent.getAction()) {
            case ACTION_ACCOUNT_AUTHENTICATE:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD));
                apiLastString = "account/authenticate/";
                requestAction = ACTION_ACCOUNT_AUTHENTICATE;
                break;
            case ACTION_ACCOUNT_REGISTER:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD));
                postDataParams.put(RegisterActivity.EXTRA_NAME, intent.getStringExtra(RegisterActivity.EXTRA_NAME));
                postDataParams.put(RegisterActivity.EXTRA_SURNAME, intent.getStringExtra(RegisterActivity.EXTRA_SURNAME));
                postDataParams.put(RegisterActivity.EXTRA_MIDDLE_NAME, intent.getStringExtra(RegisterActivity.EXTRA_MIDDLE_NAME));
                postDataParams.put(RegisterActivity.EXTRA_DATE_OF_BIRTH, intent.getStringExtra(RegisterActivity.EXTRA_DATE_OF_BIRTH));
                postDataParams.put(RegisterActivity.EXTRA_NUMBER, intent.getStringExtra(RegisterActivity.EXTRA_NUMBER));
                postDataParams.put(RegisterActivity.EXTRA_EMAIL, intent.getStringExtra(RegisterActivity.EXTRA_EMAIL));
                postDataParams.put(RegisterActivity.EXTRA_COUNTRY, intent.getStringExtra(RegisterActivity.EXTRA_COUNTRY));
                postDataParams.put(RegisterActivity.EXTRA_CITY, intent.getStringExtra(RegisterActivity.EXTRA_CITY));
                postDataParams.put(RegisterActivity.EXTRA_ADDRESS, intent.getStringExtra(RegisterActivity.EXTRA_ADDRESS));
                postDataParams.put(RegisterActivity.EXTRA_UNIVERSITY, intent.getStringExtra(RegisterActivity.EXTRA_UNIVERSITY));
                postDataParams.put(RegisterActivity.EXTRA_FACULTY, intent.getStringExtra(RegisterActivity.EXTRA_FACULTY));
                postDataParams.put(RegisterActivity.EXTRA_GROUP, intent.getStringExtra(RegisterActivity.EXTRA_GROUP));
                postDataParams.put(RegisterActivity.EXTRA_GENDER, intent.getStringExtra(RegisterActivity.EXTRA_GENDER));
                postDataParams.put(RegisterActivity.EXTRA_COURSE, intent.getStringExtra(RegisterActivity.EXTRA_COURSE));
                apiLastString = "account/register/";
                requestAction = ACTION_ACCOUNT_REGISTER;
                break;
            case ACTION_ACCOUNT_CHECK_ROLE:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD));
                apiLastString = "account/check/";
                requestAction = ACTION_ACCOUNT_CHECK_ROLE;
                break;

            case ACTION_GET_READERS_IN_LIBRARY:
                apiLastString = "administration/readers/";
                requestAction = ACTION_GET_READERS_IN_LIBRARY;
                break;
            case ACTION_GET_READERS_IN_LIBRARY_LOG:
                apiLastString = "administration/readers/log/";
                requestAction = ACTION_GET_READERS_IN_LIBRARY_LOG;
                break;

            case ACTION_GET_GENDER:
                apiLastString = "gender/";
                requestAction = ACTION_GET_GENDER;

                break;
            case ACTION_GET_COUNTRY:
                postDataParams.put(FullAddressActivity.EXTRA_COUNTRY, intent.getStringExtra(FullAddressActivity.EXTRA_COUNTRY));
                apiLastString = "country/";
                requestAction = ACTION_GET_COUNTRY;

                break;
            case ACTION_GET_CITY:
                postDataParams.put(FullAddressActivity.EXTRA_COUNTRY, intent.getStringExtra(FullAddressActivity.EXTRA_COUNTRY));
                postDataParams.put(FullAddressActivity.EXTRA_CITY, intent.getStringExtra(FullAddressActivity.EXTRA_CITY));
                apiLastString = "city/";
                requestAction = ACTION_GET_CITY;

                break;
            case ACTION_GET_ADDRESS:
                postDataParams.put(FullAddressActivity.EXTRA_COUNTRY, intent.getStringExtra(FullAddressActivity.EXTRA_COUNTRY));
                postDataParams.put(FullAddressActivity.EXTRA_CITY, intent.getStringExtra(FullAddressActivity.EXTRA_CITY));
                postDataParams.put(FullAddressActivity.EXTRA_ADDRESS, intent.getStringExtra(FullAddressActivity.EXTRA_ADDRESS));
                apiLastString = "address/";
                requestAction = ACTION_GET_ADDRESS;

                break;
            case ACTION_GET_BOOK:
                postDataParams.put(BookActivity.EXTRA_QUERY_OF_BOOK, intent.getStringExtra(BookActivity.EXTRA_QUERY_OF_BOOK));
                postDataParams.put(BookActivity.EXTRA_NUMBER_OF_BOOK_LIST_PAGE, Integer.toString(intent.getIntExtra(BookActivity.EXTRA_NUMBER_OF_BOOK_LIST_PAGE, 0)));
                postDataParams.put(BookActivity.EXTRA_SIZE_OF_BOOK_LIST, Integer.toString(intent.getIntExtra(BookActivity.EXTRA_SIZE_OF_BOOK_LIST, 0)));
                apiLastString = "book/";
                requestAction = ACTION_GET_BOOK;

                break;
            case ACTION_GET_BOOK_COUNT:
                postDataParams.put(BookActivity.EXTRA_QUERY_OF_BOOK, intent.getStringExtra(BookActivity.EXTRA_QUERY_OF_BOOK));
                apiLastString = "book/count/";
                requestAction = ACTION_GET_BOOK_COUNT;

                break;
            case ACTION_GET_AUTHOR:
                postDataParams.put(BookActivity.EXTRA_QUERY_OF_BOOK, intent.getStringExtra(BookActivity.EXTRA_QUERY_OF_BOOK));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                apiLastString = "author/";
                requestAction = ACTION_GET_AUTHOR;

                break;
            case ACTION_GET_GENRE:
                postDataParams.put(BookActivity.EXTRA_QUERY_OF_BOOK, intent.getStringExtra(BookActivity.EXTRA_QUERY_OF_BOOK));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                apiLastString = "genre/";
                requestAction = ACTION_GET_GENRE;

                break;
            case ACTION_GET_BOOK_EDITION:
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                apiLastString = "book/edition/";
                requestAction = ACTION_GET_BOOK_EDITION;

                break;
            case ACTION_GET_SELECTED_BOOK_EDITION:
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "book/edition/selected/";
                requestAction = ACTION_GET_SELECTED_BOOK_EDITION;

                break;

            case ACTION_GET_BOOK_IMAGE_MEDIUM:
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                apiLastString = "book/image/medium/";
                requestAction = ACTION_GET_BOOK_IMAGE_MEDIUM;

                break;

            case ACTION_GET_FAVORITE:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "favorite/";
                requestAction = ACTION_GET_FAVORITE;
                break;
            case ACTION_SET_FAVORITE:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "favorite/set/";
                requestAction = ACTION_SET_FAVORITE;
                break;
            case ACTION_DELETE_FAVORITE:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "favorite/delete/";
                requestAction = ACTION_DELETE_FAVORITE;
                break;

            case ACTION_GET_RATING:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "rating/";
                requestAction = ACTION_GET_RATING;
                break;
            case ACTION_SET_RATING:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_MY_VALUE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_MY_VALUE));
                apiLastString = "rating/set/";
                requestAction = ACTION_SET_RATING;
                break;
            case ACTION_DELETE_RATING:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "rating/delete/";
                requestAction = ACTION_DELETE_RATING;
                break;

            case ACTION_TAKE_A_BOOK:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "book/edition/selected/take/";
                requestAction = ACTION_TAKE_A_BOOK;
                break;
            case ACTION_RETURN_A_BOOK:
                postDataParams.put(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, intent.getStringExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN));
                postDataParams.put(SelectedBookActivity.EXTRA_KEY_OF_BOOK, intent.getStringExtra(SelectedBookActivity.EXTRA_KEY_OF_BOOK));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_LANGUAGE, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_LANGUAGE));
                postDataParams.put(SelectedBookEditionActivity.EXTRA_PUBLISHER, intent.getStringExtra(SelectedBookEditionActivity.EXTRA_PUBLISHER));
                apiLastString = "book/edition/selected/return/";
                requestAction = ACTION_RETURN_A_BOOK;
                break;
        }

        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(START_REQUEST).putExtra(EXTRA_REQUEST_ACTION, requestAction));

        Log.d(START_REQUEST, START_REQUEST);

        if (!PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.timer), "").equals("no")) {
            try {
                Thread.sleep(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getResources().getString(R.string.timer), "")) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        HttpHelper httpHelper = new HttpHelper();
        String responseJson = httpHelper.sendRequest(serverAddress + apiPath + apiLastString, postDataParams);
        Log.d("responseJson", responseJson);

        parseJSON(responseJson, requestAction);

        return null;
    }

    private void parseJSON(String json, String requestAction) {
        JSONObject jsonObjectResponse = null;
        Intent broadcast = null;

        try {
            jsonObjectResponse = new JSONObject(json);

            String responseError = null;
            String responseOk = null;

            try {
                JSONObject jsonObjectOutput = jsonObjectResponse.getJSONObject("output");

                try {
                    responseError = jsonObjectOutput.getString("error");

                    System.out.println(responseError + " --- " + requestAction);

                    broadcast = new Intent(RESPONSE_ERROR)
                            .putExtra(EXTRA_REQUEST_ACTION, requestAction)
                            .putExtra(EXTRA_RESPONSE_ERROR, responseError);
                } catch (JSONException e) {
                    e.printStackTrace();

                    responseOk = jsonObjectOutput.getString("ok");

                    System.out.println(responseOk + " --- " + requestAction);

                    broadcast = new Intent(RESPONSE_OK)
                            .putExtra(EXTRA_REQUEST_ACTION, requestAction)
                            .putExtra(EXTRA_RESPONSE_OK, responseOk);
                }
            } catch (JSONException e) {
                e.printStackTrace();

                try {
                    JSONArray jsonArrayOutput = jsonObjectResponse.getJSONArray("output");
                    responseOk = jsonArrayOutput.toString();

                    System.out.println(responseOk + " --- " + requestAction);

                    broadcast = new Intent(RESPONSE_OK)
                            .putExtra(EXTRA_REQUEST_ACTION, requestAction)
                            .putExtra(EXTRA_RESPONSE_OK, responseOk);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            Log.d("isRegistered", Boolean.toString(isRegistered));

            while (!isReceived && !isCancelled()) {
                if (isRegistered) {
                    isReceived = LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
                    Log.d("isReceived", Boolean.toString(isReceived));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*class ServiceRunnable implements Runnable {
        private String apiLastString = null;
        private String requestAction = null;

        HashMap<String, String> postDataParams;

        *//*public ServiceRunnable(String apiLastString, String requestAction) {
            this.apiLastString = apiLastString;
            this.requestAction = requestAction;
        }*//*

        public ServiceRunnable(String apiLastString, String requestAction, HashMap<String, String> postDataParams) {
            this.apiLastString = apiLastString;
            this.requestAction = requestAction;
            this.postDataParams = postDataParams;
        }

        @Override
        public void run() {

        }
    }*/

    public static IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(RESPONSE_OK);
        filter.addAction(RESPONSE_ERROR);
        /*filter.addAction(NETWORK_CONNECTION_ERROR);*/
        filter.addAction(START_REQUEST);

        return filter;
    }
}
