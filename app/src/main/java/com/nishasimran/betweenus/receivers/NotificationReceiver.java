package com.nishasimran.betweenus.receivers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.RemoteInput;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Encryption.Encryption;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.FirebaseDataClasses.FMessage;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.ViewModels.KeyViewModel;
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
            Message answer = new Message(UUID.randomUUID().toString(), replyText.toString(), uid, serverUid, CommonValues.MESSAGE_TYPE_TEXT, CommonValues.STATUS_SENDING, System.currentTimeMillis(), null, null, null);
            Log.i("TAG", "onReceive: " + uid);
            Log.i("TAG", "onReceive: " + serverUid);
            Log.i("TAG", "onReceive: " + answer.getMessage());
            ParentService.postNotification(answer, context, (Application) context.getApplicationContext());

            if (Utils.isNetworkAvailable(context)) {
                Log.i("TAG", "onReceive: network avail");
                Map<String, String> map = encryptMessage(answer.getMessage(), context);
                if (map != null)
                    createAndSendMessage(map, answer, context);
            }
        }
    }

    private void createAndSendMessage(Map<String, String> map, Message message, Context context) {
        String serverPublic = map.get(CommonValues.SERVER_KEY);
        String myPublic = map.get(CommonValues.MY_PUBLIC_KEY);
        String myPrivate = map.get(CommonValues.MY_PRIVATE_KEY);
        String iv = map.get(CommonValues.IV);
        FMessage fMessage = new FMessage(message.getId(), map.get(CommonValues.ENCRYPTED_MESSAGE), message.getFrom(), message.getTo(), message.getMessageType(), CommonValues.STATUS_SENT, message.getCurrMillis(), null, null, null, serverPublic, myPublic, iv);
        FirebaseDb.getInstance().sendMessage(fMessage);
        ParentService.postNotification(message, context, (Application) context.getApplicationContext());
        Key key = new Key(UUID.randomUUID().toString(), myPrivate, myPublic, serverPublic, message.getCurrMillis());
        insertKey(key, context);
    }

    private Map<String, String> encryptMessage(String text, Context context) {
        Key key = KeyViewModel.getInstance((ViewModelStoreOwner) context, (Application) context.getApplicationContext()).getLastKeyWithServerPublic(ParentService.keys);
        if (key != null) {
            String serverPublic = key.getServerPublic();
            return Encryption.encryptText(text, serverPublic);
        }
        return null;
    }

    private void insertKey(Key key, Context context) {
        KeyViewModel.getInstance((ViewModelStoreOwner) context, (Application) context.getApplicationContext()).insert(key);
    }


}
