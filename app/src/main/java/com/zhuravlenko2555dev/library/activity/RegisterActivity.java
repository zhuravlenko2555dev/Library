package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.zhuravlenko2555dev.library.fragment.RetainedRegisterFragment;
import com.zhuravlenko2555dev.library.util.APIAsyncTask;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;
import com.zhuravlenko2555dev.library.provider.GenderAdapter;
import com.zhuravlenko2555dev.library.provider.GenderItem;
import com.zhuravlenko2555dev.library.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    public static final int DATE_PICKER_DIALOG_SELECT_DATE = 2;

    public static final int REQUEST_CODE_SELECT_FULL_ADDRESS = 0;
    public static final int REQUEST_CODE_SELECT_FULL_GROUP = 1;

    public static String EXTRA_NAME = "extra_name";
    public static String EXTRA_SURNAME = "extra_surname";
    public static String EXTRA_MIDDLE_NAME = "extra_middle_name";
    public static String EXTRA_DATE_OF_BIRTH = "extra_date_of_birth";
    public static String EXTRA_NUMBER = "extra_number";
    public static String EXTRA_EMAIL = "extra_email";
    public static String EXTRA_COUNTRY = "extra_country";
    public static String EXTRA_CITY = "extra_city";
    public static String EXTRA_ADDRESS = "extra_address";
    public static String EXTRA_UNIVERSITY = "extra_university";
    public static String EXTRA_FACULTY = "extra_faculty";
    public static String EXTRA_GROUP = "extra_group";
    public static String EXTRA_GENDER = "extra_gender";
    public static String EXTRA_COURSE = "extra_course";

    private TextInputLayout textInputLayoutLogin, textInputLayoutPassword, textInputLayoutNumber, textInputLayoutEmail, textInputLayoutName, textInputLayoutSurname, textInputLayoutMiddleName;
    private EditText editTextLogin, editTextPassword, editTextNumber, editTextEmail, editTextName, editTextSurname, editTextMiddleName;
    private TextView textViewDateOfBirth;
    private Spinner spinnerGender;
    private ProgressBar progressBarGender;
    private TextView textViewCountry, textViewCity, textViewAddress;
    private LinearLayout linearLayoutFullAddress;

    private FragmentManager fragmentManager;
    private RetainedRegisterFragment retainedRegisterFragment;

    private Dialog dialog, alertDialogProcessing, datePickerDialogSelectDate;
    private AlertDialog.Builder builderProcessing;

    private String login = "";
    private String password = "";
    private String name = "";
    private String surname = "";
    private String middleName = "";
    private String dateOfBirth = "";
    private String number = "";
    private String email = "";
    private String country = "";
    private String city = "";
    private String address = "";
    private String university = "";
    private String faculty = "";
    private String group = "";
    private String gender = "";
    private String course = "";

    private final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;

    private ArrayList<GenderItem> genderItemList;
    private GenderAdapter genderAdapter;

    private boolean firstLaunch = true;

    private BroadcastReceiver broadcastReceiver;

    private APIAsyncTask genderTask = null;
    private APIAsyncTask registerTask = null;

    private ActivityManager activityManager = null;

    private AnimationDrawable animationDrawableDialogProcessing = null;
    private AssetManager assetManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        fragmentManager = getSupportFragmentManager();
        retainedRegisterFragment = (RetainedRegisterFragment) fragmentManager.findFragmentByTag(RetainedRegisterFragment.FRAGMENT_TAG);

        if (retainedRegisterFragment == null) {
            retainedRegisterFragment  = new RetainedRegisterFragment();
            fragmentManager.beginTransaction().add(retainedRegisterFragment, RetainedRegisterFragment.FRAGMENT_TAG).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInputLayoutLogin = findViewById(R.id.textInputLayoutLogin);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutNumber = findViewById(R.id.textInputLayoutNumber);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutSurname = findViewById(R.id.textInputLayoutSurname);
        textInputLayoutMiddleName = findViewById(R.id.textInputLayoutMiddleName);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextMiddleName = findViewById(R.id.editTextMiddleName);
        textViewDateOfBirth = findViewById(R.id.textViewDateOfBirth);
        spinnerGender = findViewById(R.id.spinnerGender);
        progressBarGender = findViewById(R.id.progressBarGender);
        textViewCountry = findViewById(R.id.textViewCountry);
        textViewCity = findViewById(R.id.textViewCity);
        textViewAddress = findViewById(R.id.textViewAddress);
        linearLayoutFullAddress = findViewById(R.id.linearLayoutFullAddress);

        textViewDateOfBirth.setOnClickListener(this);
        linearLayoutFullAddress.setOnTouchListener(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case APIAsyncTask.START_REQUEST:
                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_ACCOUNT_REGISTER:
                                showDialog(APIAsyncTask.PROGRESS_DIALOG_PROCESSING);

                                break;
                            case APIAsyncTask.ACTION_GET_GENDER:
                                progressBarGender.setVisibility(View.VISIBLE);
                                Log.d("progressBarGender", "progressBarGender");

                                break;
                        }

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
                            case APIAsyncTask.ACTION_ACCOUNT_REGISTER:
                                killAllTask();

                                dismissDialog(APIAsyncTask.PROGRESS_DIALOG_PROCESSING);
                                animationDrawableDialogProcessing.stop();

                                Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentLogin);

                                finish();

                                break;
                            case APIAsyncTask.ACTION_GET_GENDER:
                                genderItemList = new ArrayList<>();

                                for (int i = 0; i < responseJsonArray.length(); i++) {
                                    try {
                                        JSONObject responseJsonObject = responseJsonArray.getJSONObject(i);
                                        String genderName = (String) responseJsonObject.get("name");

                                        switch (genderName) {
                                            case "male":
                                                genderItemList.add(new GenderItem(genderName, R.drawable.male));

                                                break;
                                            case "female":
                                                genderItemList.add(new GenderItem(genderName, R.drawable.female));

                                                break;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                genderAdapter = new GenderAdapter(RegisterActivity.this, R.layout.item_gender, genderItemList);
                                spinnerGender.setAdapter(genderAdapter);

                                spinnerGender.setEnabled(true);
                                progressBarGender.setVisibility(View.GONE);

                                break;
                        }

                        break;
                    case APIAsyncTask.RESPONSE_ERROR:
                        String responseError = intent.getStringExtra(APIAsyncTask.EXTRA_RESPONSE_ERROR);

                        switch (intent.getStringExtra(APIAsyncTask.EXTRA_REQUEST_ACTION)) {
                            case APIAsyncTask.ACTION_ACCOUNT_REGISTER:
                                dismissDialog(APIAsyncTask.PROGRESS_DIALOG_PROCESSING);
                                animationDrawableDialogProcessing.stop();

                                Log.d("register", responseError);

                                break;
                        }

                        break;
                }
            }
        };

        if (savedInstanceState == null) {
            spinnerGender.setEnabled(false);
        }
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
            case DATE_PICKER_DIALOG_SELECT_DATE:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                datePickerDialogSelectDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateOfBirth = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
                        textViewDateOfBirth.setText(dateOfBirth);
                    }
                }, year, month, day);
                datePickerDialogSelectDate.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_background));

                dialog = datePickerDialogSelectDate;

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
    protected void onResume() {
        super.onResume();

        if (firstLaunch) {
            firstLaunch = false;

            Intent intentGender = new Intent(this, APIAsyncTask.class)
                    .setAction(APIAsyncTask.ACTION_GET_GENDER);
            genderTask = new APIAsyncTask(this);
            genderTask.execute(intentGender);
        }

        textViewDateOfBirth.setText(dateOfBirth);
        textViewCountry.setText(country);
        textViewCity.setText(city);
        textViewAddress.setText(address);

        genderItemList = retainedRegisterFragment.getGenderItemList();

        if (genderItemList.size() != 0) {
            genderAdapter = new GenderAdapter(RegisterActivity.this, R.layout.item_gender, genderItemList);
            spinnerGender.setAdapter(genderAdapter);
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

        retainedRegisterFragment.setGenderItemList(genderItemList);

        Log.d("spinnerGender", Integer.toString(genderItemList.size()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textViewDateOfBirth:
                showDialog(DATE_PICKER_DIALOG_SELECT_DATE);

                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();

                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                if(Math.abs(deltaX) > MIN_DISTANCE){
                    if (deltaX < 0) {
                        return true;
                    }

                    if (deltaX > 0) {
                        switch (v.getId()) {
                            case R.id.linearLayoutFullAddress:
                                killAllTask();

                                Intent intentSelectFullAddress = new Intent(this, FullAddressActivity.class);
                                startActivityForResult(intentSelectFullAddress, REQUEST_CODE_SELECT_FULL_ADDRESS);
                                overridePendingTransition( R.anim.left_in, R.anim.left_out);

                                break;
                        }

                        return true;
                    }
                }
                else {
                }

                if(Math.abs(deltaY) > MIN_DISTANCE){
                    if (deltaY < 0) {
                        return true;
                    }

                    if (deltaY > 0) {
                        return true;
                    }
                }
                else {
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FULL_ADDRESS:
                    country = data.getStringExtra(FullAddressActivity.EXTRA_COUNTRY);
                    city = data.getStringExtra(FullAddressActivity.EXTRA_CITY);
                    address = data.getStringExtra(FullAddressActivity.EXTRA_ADDRESS);

                    textViewCountry.setText(country);
                    textViewCity.setText(city);
                    textViewAddress.setText(address);

                    break;
                case REQUEST_CODE_SELECT_FULL_GROUP:
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);

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
            case R.id.ok:
                login = editTextLogin.getText().toString();
                password = editTextPassword.getText().toString();
                name = editTextName.getText().toString();
                surname = editTextSurname.getText().toString();
                middleName = editTextMiddleName.getText().toString();
                dateOfBirth = textViewDateOfBirth.getText().toString();
                number = editTextNumber.getText().toString();
                email = editTextEmail.getText().toString();

                if (spinnerGender.getSelectedView() != null) {
                    gender = ((TextView) spinnerGender.getSelectedView().findViewById(R.id.textViewGenderName)).getText().toString();
                }

                Intent intentRegister = new Intent(RegisterActivity.this, APIAsyncTask.class)
                        .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, login)
                        .putExtra(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, password)
                        .putExtra(EXTRA_NAME, name)
                        .putExtra(EXTRA_SURNAME, surname)
                        .putExtra(EXTRA_MIDDLE_NAME, middleName)
                        .putExtra(EXTRA_DATE_OF_BIRTH, dateOfBirth)
                        .putExtra(EXTRA_NUMBER, number)
                        .putExtra(EXTRA_EMAIL, email)
                        .putExtra(EXTRA_COUNTRY, country)
                        .putExtra(EXTRA_CITY, city)
                        .putExtra(EXTRA_ADDRESS, address)
                        .putExtra(EXTRA_UNIVERSITY, university)
                        .putExtra(EXTRA_FACULTY, faculty)
                        .putExtra(EXTRA_GROUP, group)
                        .putExtra(EXTRA_GENDER, gender)
                        .putExtra(EXTRA_COURSE, course)
                        .setAction(APIAsyncTask.ACTION_ACCOUNT_REGISTER);
                registerTask = new APIAsyncTask(this);
                registerTask.execute(intentRegister);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        killAllTask();

        fragmentManager.beginTransaction().remove(retainedRegisterFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN, login);
        outState.putString(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD, password);
        outState.putString(EXTRA_NAME, name);
        outState.putString(EXTRA_SURNAME, surname);
        outState.putString(EXTRA_MIDDLE_NAME, middleName);
        outState.putString(EXTRA_DATE_OF_BIRTH, dateOfBirth);
        outState.putString(EXTRA_NUMBER, number);
        outState.putString(EXTRA_EMAIL, email);
        outState.putString(EXTRA_COUNTRY, country);
        outState.putString(EXTRA_CITY, city);
        outState.putString(EXTRA_ADDRESS, address);
        outState.putString(EXTRA_UNIVERSITY, university);
        outState.putString(EXTRA_FACULTY, faculty);
        outState.putString(EXTRA_GROUP, group);
        outState.putString(EXTRA_GENDER, gender);
        outState.putString(EXTRA_COURSE, course);

        outState.putBoolean("FIRST_LAUNCH", firstLaunch);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        login = savedInstanceState.getString(AccountAuthLocalStore.AccountAuth.EXTRA_LOGIN);
        password = savedInstanceState.getString(AccountAuthLocalStore.AccountAuth.EXTRA_PASSWORD);
        name = savedInstanceState.getString(EXTRA_NAME);
        surname = savedInstanceState.getString(EXTRA_SURNAME);
        middleName = savedInstanceState.getString(EXTRA_MIDDLE_NAME);
        dateOfBirth = savedInstanceState.getString(EXTRA_DATE_OF_BIRTH);
        number = savedInstanceState.getString(EXTRA_NUMBER);
        email = savedInstanceState.getString(EXTRA_EMAIL);
        country = savedInstanceState.getString(EXTRA_COUNTRY);
        city = savedInstanceState.getString(EXTRA_CITY);
        address = savedInstanceState.getString(EXTRA_ADDRESS);
        university = savedInstanceState.getString(EXTRA_UNIVERSITY);
        faculty = savedInstanceState.getString(EXTRA_FACULTY);
        group = savedInstanceState.getString(EXTRA_GROUP);
        gender = savedInstanceState.getString(EXTRA_GENDER);
        course = savedInstanceState.getString(EXTRA_COURSE);

        firstLaunch = savedInstanceState.getBoolean("FIRST_LAUNCH");
    }

    private void killAllTask() {
        if (genderTask != null && genderTask.getStatus() == AsyncTask.Status.RUNNING) {
            genderTask.cancel(false);
        }

        if (registerTask != null && registerTask.getStatus() == AsyncTask.Status.RUNNING) {
            registerTask.cancel(false);
        }
    }
}
