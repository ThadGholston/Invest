package com.investmobile.invest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Authentication {

    private final static String APP_AUTH_SHARED_PREFS = "auth_preferences";
    private String token = null;
    private Context context;

    public Authentication(Context context) {
        this.context = context;
    }

    public void onCreate() {
        baseTest();
    }

    public void onResume() {
        baseTest();
    }

    public void onRestart() {
        baseTest();

    }

    public void baseTest() {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        boolean skippedLogin = sharedPrefs.getBoolean("userSkippedLoggedInState", false);
        if (!isUserLoggedIn && !skippedLogin) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }

    public boolean isActiveUser() {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        boolean isUserLoggedIn = sharedPrefs.getBoolean("userLoggedInState", false);
        return isUserLoggedIn;
    }

    public String getToken() {
        return token;
    }


    public void logout() {
        SharedPreferences sharedPrefs = context.getApplicationContext().getSharedPreferences(APP_AUTH_SHARED_PREFS, Context.MODE_PRIVATE);
        sharedPrefs.edit().putBoolean("userSkippedLoggedInState", false).apply();
        sharedPrefs.edit().putBoolean("userLoggedInState", false).apply();
        token = null;
    }


}
