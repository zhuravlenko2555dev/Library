package com.zhuravlenko2555dev.library.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zhuravlenko2555dev.library.R;
import com.zhuravlenko2555dev.library.provider.AccountAuthLocalStore;

import java.util.List;

public class PrefActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private ActivityManager activityManager = null;
    private boolean preferenceLogOutEnabled = true;
    private String baseActivity = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
        baseActivity = taskInfo.get(0).baseActivity.getClassName();

        Log.d("baseActivity", baseActivity);

        if (baseActivity.equals("com.zhuravlenko2555dev.library.activity.LoginActivity") || baseActivity.equals("com.zhuravlenko2555dev.library.activity.RegisterActivity") || baseActivity.equals("com.zhuravlenko2555dev.library.activity.FullAddressActivity")) {
            preferenceLogOutEnabled = false;
            findPreference("logout").setEnabled(false);
        } else {
            findPreference("logout").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AccountAuthLocalStore.clearAccountAuth();
                    AccountAuthLocalStore.setAccountLoggedIn(false);

                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    finish();

                    return false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
            Preference preference = getPreferenceScreen().getPreference(i);
            updatePreference(preference);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key));
    }

    private void updatePreference (Preference preference) {
        switch (preference.getKey()) {
            case "serverAddress":
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(editTextPreference.getText());

                break;
            case "logout":
                if (preferenceLogOutEnabled) {
                    preference.setSummary("Current login: " + AccountAuthLocalStore.getAccountAuth().getLogin());
                }

                break;
            case "timer":
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getValue());

                break;
        }
    }
}
