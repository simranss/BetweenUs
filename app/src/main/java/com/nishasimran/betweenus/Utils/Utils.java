package com.nishasimran.betweenus.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.nishasimran.betweenus.Strings.CommonStrings;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Locale;

public class Utils {

    private static final String TAG = "Utils";


    public static String getFormattedDate(long milliSec) {
        Date date = new Date(milliSec);

        // date
        String day = new SimpleDateFormat("dd", Locale.getDefault()).format(date);
        SimpleDateFormat df;
        switch (day) {
            case "01":
            case "21":
                df = new SimpleDateFormat("dd'st' MMM yyyy", Locale.getDefault());
                break;
            case "02":
            case "22":
                df = new SimpleDateFormat("dd'nd' MMM yyyy", Locale.getDefault());
                break;
            case "03":
            case "23":
                df = new SimpleDateFormat("dd'rd' MMM yyyy", Locale.getDefault());
                break;
            default:
                df = new SimpleDateFormat("dd'th' MMM yyyy", Locale.getDefault());
                break;
        }

        String formattedDate = df.format(date);
        Log.d(TAG, formattedDate);

        return formattedDate;
    }


    public static void writeToSharedPreference(@NotNull Application application, String key, String value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static void writeToSharedPreference(@NotNull Application application, String key, boolean value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static void writeToSharedPreference(@NotNull Application application, String key, int value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public static void writeToSharedPreference(@NotNull Application application, String key, long value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }


    public static String getStringFromSharedPreference(@NotNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(key, CommonStrings.NULL);
    }


    public static boolean getBooleanFromSharedPreference(@NotNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }


    public static int getIntFromSharedPreference(@NotNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }


    public static long getLongFromSharedPreference(@NotNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }
}
