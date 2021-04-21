package com.nishasimran.betweenus.Utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nishasimran.betweenus.Strings.CommonStrings;

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


    public static void showFragment(@NonNull FragmentManager fragManager, @IdRes int id, Fragment fragment) {
        FragmentTransaction transaction = fragManager.beginTransaction();

        transaction.replace(id, fragment);
        transaction.commit();
    }


    public static void writeToSharedPreference(@NonNull Application application, String key, String value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static void writeToSharedPreference(@NonNull Application application, String key, boolean value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static void writeToSharedPreference(@NonNull Application application, String key, int value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public static void writeToSharedPreference(@NonNull Application application, String key, long value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }


    public static String getStringFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(key, CommonStrings.NULL);
    }


    public static boolean getBooleanFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }


    public static int getIntFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }


    public static long getLongFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonStrings.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getLong(key, 0);
    }

    @NonNull
    public static byte [] stringByteArrayToByteArray(@NonNull String stringByteArray) {
        String[] byteValues = stringByteArray.substring(1, stringByteArray.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        return bytes;
    }
}
