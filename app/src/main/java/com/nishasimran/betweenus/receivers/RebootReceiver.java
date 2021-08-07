package com.nishasimran.betweenus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nishasimran.betweenus.services.MessageService;

public class RebootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, MessageService.class));
        context.startService(new Intent(context, MessageService.class));
    }
}
