package com.nishasimran.betweenus.receivers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.RemoteInput;

import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.services.ParentService;

import java.util.Map;
import java.util.UUID;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence replyText = remoteInput.getCharSequence("key_reply");
            Log.i("TAG", "onReceive: " + replyText);
            Application application = ((Application)context.getApplicationContext());
            String uid = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_UID);
            String serverUid = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_SERVER_UID);
            long currMillis = System.currentTimeMillis();
            Message answer = new Message(UUID.randomUUID().toString(), replyText.toString(), uid, serverUid, CommonValues.MESSAGE_TYPE_TEXT, CommonValues.STATUS_SENDING, currMillis, null, null, null);
            Log.i("TAG", "onReceive: " + uid);
            Log.i("TAG", "onReceive: " + serverUid);
            Log.i("TAG", "onReceive: " + answer.getMessage());
            if (ParentService.getUnreadMessages() != null) {
                ParentService.addUnreadMessages(answer);
                ParentService.makeUnreadRead();
            }
            ParentService.postNotification(answer, context, (Application) context.getApplicationContext());

            if (Utils.isNetworkAvailable(context)) {
                Log.i("TAG", "onReceive: network avail");
                Map<String, String> map = ParentService.encryptMessage(answer.getMessage(), context, currMillis);
                if (map != null)
                    ParentService.createAndSendMessage(map, answer, context);
            } else {
                Log.d("TAG", "onReceive: no network");
            }
        } else {
            Log.d("TAG", "onReceive: result null");
        }
    }


}
