package com.nishasimran.betweenus.Utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseValues;

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

    public static String getFormattedTime(long milliSec) {
        Date date = new Date(milliSec);

        // date
        SimpleDateFormat df = new SimpleDateFormat("hh:mm a", Locale.getDefault());

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
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void writeToSharedPreference(@NonNull Application application, String key, boolean value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void writeToSharedPreference(@NonNull Application application, String key, int value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void writeToSharedPreference(@NonNull Application application, String key, long value) {
        SharedPreferences.Editor editor = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.apply();
    }


    public static String getStringFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getString(key, CommonValues.NULL);
    }

    public static boolean getBooleanFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static int getIntFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        return preferences.getInt(key, 0);
    }

    public static long getLongFromSharedPreference(@NonNull Application application, String key) {
        SharedPreferences preferences = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE);
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


    @DrawableRes
    public static int getBackgroundId(@NonNull Application application) {
        int index = getIntFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_BACKGROUND);
        @DrawableRes int DEFAULT = R.drawable.background_13_img;
        switch (index) {
            case 1:
                return R.drawable.background_01_img;
            case 2:
                return R.drawable.background_02_img;
            case 3:
                return R.drawable.background_03_img;
            case 4:
                return R.drawable.background_04_img;
            case 5:
                return R.drawable.background_05_img;
            case 6:
                return R.drawable.background_06_img;
            case 7:
                return R.drawable.background_07_img;
            case 8:
                return R.drawable.background_08_img;
            case 9:
                return R.drawable.background_09_img;
            case 10:
                return R.drawable.background_10_img;
            case 11:
                return R.drawable.background_11_img;
            case 12:
                return R.drawable.background_12_img;
            case 13:
                return R.drawable.background_13_img;
            case 14:
                return R.drawable.background_14_img;
            case 15:
                return R.drawable.background_15_img;
            case 16:
                return R.drawable.background_16_img;
            case 17:
                return R.drawable.background_17_img;
            default:
                return DEFAULT;
        }
    }

    public static void setBackgroundInt(@NonNull Application application, int value) {
        writeToSharedPreference(application, CommonValues.SHARED_PREFERENCE_BACKGROUND, value);
    }


    public static String getUniqueMessageId() {
        return FirebaseValues.MESSAGE_REF.push().getKey();
    }


    @SuppressLint("ApplySharedPref")
    public static void resetSharedPref(@NonNull Application application) {
        SharedPreferences preferences = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
}
