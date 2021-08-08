package com.nishasimran.betweenus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import com.nishasimran.betweenus.services.ParentService;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ParentService parentService = new ParentService();
        if (isNetworkAvailable(context)) {
            parentService.startWork(context.getApplicationContext());
        } else {
            parentService.stopWork();
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // For 29 api or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities == null)
                return false;
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
        }
        // For below 29 api
        else {
            return connectivityManager.getActiveNetworkInfo() != null && (connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI || connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE ||connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_ETHERNET);
        }
    }
}
