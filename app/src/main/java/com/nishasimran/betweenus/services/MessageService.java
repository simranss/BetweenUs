package com.nishasimran.betweenus.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.lifecycle.LifecycleService;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Encryption.Encryption;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.FirebaseDataClasses.FMessage;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.ViewModels.KeyViewModel;
import com.nishasimran.betweenus.ViewModels.MessageViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MessageService extends LifecycleService {

    private final String TAG = "MessageService";

    private ChildEventListener handler;
    private DatabaseReference messagesRef;
    private MessageViewModel messageViewModel;
    private KeyViewModel keyViewModel;
    private List<Key> keys;
    private List<Message> messages;

    private List<Message> unreadMessages;

    @Override
    public IBinder onBind(@NonNull Intent arg0) {
        super.onBind(arg0);
        return null;
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
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
        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "init firebaseApp");
        FirebaseApp.initializeApp(getApplicationContext());
        Log.d(TAG, "init view models");
        messageViewModel = new MessageViewModel(getApplication());
        keyViewModel = new KeyViewModel(getApplication());
        Log.d(TAG, "init messages and key listener");
        unreadMessages = new ArrayList<>();
        initMessagesListener();
        initKeyListener();
        String uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);
        Log.d(TAG, "uid: " + uid);

        messagesRef = FirebaseDatabase.getInstance().getReference().child("messages");
        Log.d(TAG, "init messagesRef");
        handler = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "messageService onChildAdded");
                if (snapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map != null) {
                        FMessage fMessage = new FMessage(map);
                        Log.d(TAG, "fMessage: " + fMessage);
                        if (fMessage.getFrom() != null && fMessage.getTo() != null && fMessage.getMessage() != null) {
                            if (uid.trim().equals(fMessage.getTo().trim())) {
                                Key key = keyViewModel.findKeyByMyPublic(fMessage.getServerPublic(), keys);
                                if (key != null) {
                                    snapshot.getRef().removeValue();
                                    long deliveredCurrMillis = System.currentTimeMillis();
                                    Log.d(TAG, "map iv: " + fMessage.getIv());
                                    Log.d(TAG, "map myPublic: " + fMessage.getMyPublic());
                                    Log.d(TAG, "map message: " + fMessage.getMessage());
                                    String messageTxt = decryptMessage(fMessage.getMyPublic(), key.getMyPrivate(), fMessage.getIv(), fMessage.getMessage());
                                    Message message = new Message(fMessage.getId(), messageTxt, fMessage.getFrom(), fMessage.getTo(), fMessage.getMessageType(), CommonValues.STATUS_DELIVERED, deliveredCurrMillis, null, deliveredCurrMillis, null);
                                    message.setUnread(true);
                                    unreadMessages.add(message);
                                    if (fMessage.getSentCurrMillis() != null) {
                                        message.setCurrMillis(fMessage.getSentCurrMillis());
                                        message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                    }
                                    FirebaseDb.getInstance().updateMessageStatus(fMessage.getId(), CommonValues.STATUS_DELIVERED, deliveredCurrMillis);
                                    insertMessage(message);
                                    postNotification(message);
                                    Key key1 = new Key(UUID.randomUUID().toString(), null, fMessage.getServerPublic(), fMessage.getMyPublic(), fMessage.getCurrMillis());
                                    keyViewModel.insert(key1);
                                } else {
                                    Log.d(TAG, "key not found");
                                }
                            } else {
                                Log.d(TAG, "message not sent to you");
                                if (fMessage.getSentCurrMillis() != null) {
                                    String messageId = snapshot.getRef().getKey();
                                    Message message = messageViewModel.findMessage(messageId, messages);
                                    if (message != null) {
                                        if (message.getSentCurrMillis() == null) {
                                            message.setStatus(CommonValues.STATUS_SENT);
                                            message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                            updateMessage(message);
                                        } else {
                                            Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                        }
                                    }
                                }
                            }
                        } else if (fMessage.getSentCurrMillis() != null) {
                            String messageId = snapshot.getRef().getKey();
                            Message message = messageViewModel.findMessage(messageId, messages);
                            if (message != null) {
                                if (message.getSentCurrMillis() == null) {
                                    message.setStatus(CommonValues.STATUS_SENT);
                                    message.setSentCurrMillis(fMessage.getSentCurrMillis());
                                    updateMessage(message);
                                } else {
                                    Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                }

                                if (fMessage.getDeliveredCurrMillis() != null) {
                                    if (message.getDeliveredCurrMillis() == null) {
                                        message.setStatus(CommonValues.STATUS_DELIVERED);
                                        message.setDeliveredCurrMillis(fMessage.getDeliveredCurrMillis());
                                        updateMessage(message);
                                    } else {
                                        Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                    }
                                }
                            }
                        } else if (fMessage.getDeliveredCurrMillis() != null) {
                            String messageId = snapshot.getRef().getKey();
                            Message message = messageViewModel.findMessage(messageId, messages);
                            if (message != null) {
                                if (message.getDeliveredCurrMillis() == null) {
                                    message.setStatus(CommonValues.STATUS_DELIVERED);
                                    message.setDeliveredCurrMillis(fMessage.getDeliveredCurrMillis());
                                    updateMessage(message);
                                } else {
                                    Log.d(TAG, "sentCurrMillis not null: " + message.getSentCurrMillis());
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "error" + error.getMessage());
            }
        };

        Log.d(TAG, "adding the listeners to databaseReference");
        messagesRef.addChildEventListener(handler);
    }

    private void initMessagesListener() {
        messageViewModel.getHundredMessages(0).observe(this, messages1 -> messages = messages1);
    }

    private void createAndSendMessage(Map<String, String> map, Message message) {
        String serverPublic = map.get(CommonValues.SERVER_KEY);
        String myPublic = map.get(CommonValues.MY_PUBLIC_KEY);
        String myPrivate = map.get(CommonValues.MY_PRIVATE_KEY);
        String iv = map.get(CommonValues.IV);
        FMessage fMessage = new FMessage(message.getId(), map.get(CommonValues.ENCRYPTED_MESSAGE), message.getFrom(), message.getTo(), message.getMessageType(), CommonValues.STATUS_SENT, message.getCurrMillis(), null, null, null, serverPublic, myPublic, iv);
        sendMessage(fMessage);
        Key key = new Key(UUID.randomUUID().toString(), myPrivate, myPublic, serverPublic, message.getCurrMillis());
        keyViewModel.insert(key);
    }

    private Map<String, String> encryptMessage(String text) {
        Key key = keyViewModel.getLastKeyWithServerPublic(keys);
        if (key != null) {
            String serverPublic = key.getServerPublic();
            return Encryption.encryptText(text, serverPublic);
        }
        return null;
    }

    public void sendMessage(FMessage message) {
        message.setSentCurrMillis(ServerValue.TIMESTAMP);
        messagesRef.child(message.getId()).setValue(message.getMap());
    }

    private String decryptMessage(String serverPublic, String myPrivateKey, String iv, String encryptedMessage) {
        return Encryption.decryptText(serverPublic, myPrivateKey, iv, encryptedMessage);
    }

    private void insertMessage(Message message) {
        messageViewModel.insert(message);
    }

    private void updateMessage(Message message) {
        messageViewModel.update(message);
    }

    private void initKeyListener() {
        keyViewModel.getAllKeys().observe(this, keys1 -> keys = keys1);
    }

    private void postNotification(Message message) {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 102, notificationIntent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            CharSequence name = "Messages";
            String description = "Message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("message", name, importance);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);

            String serverName = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_NAME);
            String serverUid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_UID);
            String from = (serverUid.equals(message.getFrom().trim())) ? serverName : "Name";

            NotificationCompat.BubbleMetadata bubbleMetadata = new NotificationCompat.BubbleMetadata.Builder("short_id").build();
            Person person = new Person.Builder()
                    .setName(from)
                    .setImportant(true)
                    .build();
            Person you = new Person.Builder()
                    .setName("You")
                    .setImportant(true)
                    .build();
            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(this, "short_id")
                    .setLongLived(true)
                    .setIntent(new Intent(this, MainActivity.class).setAction(Intent.ACTION_VIEW))
                    .setShortLabel(from)
                    .setPerson(person)
                    .build();
            ((ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE)).pushDynamicShortcut(shortcut.toShortcutInfo());
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle((person));
            for (Message message1 : unreadMessages) {
                if (message1.getFrom().equals(serverUid))
                    messagingStyle.addMessage(new NotificationCompat.MessagingStyle.Message(message1.getMessage(), message1.getCurrMillis(), person));
                else
                    messagingStyle.addMessage(new NotificationCompat.MessagingStyle.Message(message1.getMessage(), message1.getCurrMillis(), you));
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "message")
                    .setSmallIcon(R.drawable.notif_icon)
                    .setStyle(messagingStyle)
                    .setShortcutId("short_id")
                    .setBubbleMetadata(bubbleMetadata)
                    .setContentIntent(contentIntent)
                    .setNumber(unreadMessages.size())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // notificationId is a unique int for each notification that you must define
            notificationManager.cancelAll();
            notificationManager.notify(101, builder.build());
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Messages";
                String description = "Message notifications";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel("message", name, importance);
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
                channel.setDescription(description);

                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "message")
                    .setSmallIcon(R.drawable.notif_icon)
                    .setContentIntent(contentIntent)
                    .setNumber(unreadMessages.size())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // notificationId is a unique int for each notification that you must define
            notificationManager.cancelAll();
            notificationManager.notify(101, builder.build());
        }
    }

    @Override
    public void onDestroy() {
        messagesRef.removeEventListener(handler);
        super.onDestroy();
    }
}
