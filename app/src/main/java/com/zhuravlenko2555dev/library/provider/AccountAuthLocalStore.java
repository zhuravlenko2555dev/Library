package com.zhuravlenko2555dev.library.provider;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhura on 12.03.2018.
 */

public class AccountAuthLocalStore {
    private static final String SP_NAME = "accountData";
    private static SharedPreferences sharedPreferences;

    public static void initialize(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
    }

    public static void setAccountAuth(AccountAuth accountAuth) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("login", accountAuth.getLogin());
        editor.putString("password", accountAuth.getPassword());
        editor.commit();
    }

    public static void setAccountLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("loggedIn", loggedIn);
        editor.commit();
    }

    public static void clearAccountAuth() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static AccountAuth getAccountAuth() {
        if (sharedPreferences.getBoolean("loggedIn", false) == false)
            return null;

        String login = sharedPreferences.getString("login", "");
        String password = sharedPreferences.getString("password", "");

        AccountAuth accountAuth = new AccountAuth(login, password);
        return accountAuth;
    }

    public static class AccountAuth {
        private String login, password;
        public static final String EXTRA_LOGIN = "extra_login";
        public static final String EXTRA_PASSWORD = "extra_password";
        /*private String name, surname, middleName, dateOfBirth, number, email;
        private String gender, course;
        private String address, city, country;
        private String group, faculty, university;*/

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public AccountAuth(String login, String password) {

            this.login = login;
            this.password = password;
        }
    }
}
