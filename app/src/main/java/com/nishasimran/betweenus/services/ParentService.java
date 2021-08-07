package com.nishasimran.betweenus.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleService;

import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.receivers.ConnectionReceiver;

public class ParentService extends LifecycleService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        CharSequence name = "Foreground";
        String description = "Foreground notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("sticky", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(false);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, "sticky")
                .setContentTitle("Sticky notification")
                .setContentText("Just some sticky notification")
                .setSmallIcon(R.drawable.notif_icon)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSilent(true)
                .setAutoCancel(false)
                .setOngoing(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                .build();
        startForeground(2, notification);

        return START_NOT_STICKY;

    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (isNetworkAvailable(getApplicationContext()))
            startService(new Intent(getApplicationContext(), MessageService.class));

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(new ConnectionReceiver(), intentFilter);

    }

    @Override
    public void onDestroy() {

        stopService(new Intent(getApplicationContext(), MessageService.class));
        this.unregisterReceiver(new ConnectionReceiver());
        super.onDestroy();
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
