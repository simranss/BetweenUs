package com.nishasimran.betweenus.services;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ShortcutManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.lifecycle.LifecycleService;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nishasimran.betweenus.Activities.BubbleChatActivity;
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
import com.nishasimran.betweenus.receivers.ConnectionReceiver;
import com.nishasimran.betweenus.receivers.NotificationReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ParentService extends LifecycleService {

    private static final String TAG = "MessageService";

    private static ChildEventListener handler;
    private static DatabaseReference messagesRef;
    private static MessageViewModel messageViewModel;
    private static KeyViewModel keyViewModel;
    public static List<Key> keys;
    private static  List<Message> messages;

    private static List<Message> unreadMessages;

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        CharSequence name = "Foreground";
        String description = "Foreground notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel("sticky", name, importance);
        channel.setDescription(description);
        channel.setShowBadge(false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.createNotificationChannel(channel);
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

        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(new ConnectionReceiver(), intentFilter);

    }

    public static void startWork(Context context) {
        Application application = ((Application)context.getApplicationContext());

        Log.d(TAG, "init firebaseApp");
        FirebaseApp.initializeApp(context);
        Log.d(TAG, "init view models");
        messageViewModel = new MessageViewModel(application);
        keyViewModel = new KeyViewModel(application);
        Log.d(TAG, "init messages and key listener");
        unreadMessages = new ArrayList<>();
        initMessagesListener();
        initKeyListener();
        String uid = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_UID);
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
                                Log.d(TAG, "onChildAdded: keys: " + keys);
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
                                    postNotification(message, context, application);
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
    private static void initMessagesListener() {
        messageViewModel.getHundredMessages(0).observeForever( messages1 -> messages = messages1);
    }

    private static String decryptMessage(String serverPublic, String myPrivateKey, String iv, String encryptedMessage) {
        return Encryption.decryptText(serverPublic, myPrivateKey, iv, encryptedMessage);
    }

    private static void insertMessage(Message message) {
        messageViewModel.insert(message);
    }

    private static void updateMessage(Message message) {
        messageViewModel.update(message);
    }

    private static void initKeyListener() {
        Log.d(TAG, "initKeyListener: keyVM: " + keyViewModel);
        Log.d(TAG, "initKeyListener: livekeys: " + keyViewModel.getAllKeys());
        keyViewModel.getAllKeys().observeForever(keys1 -> {
            keys = keys1;
            Log.d(TAG, "initKeyListener: keys: " + keys1);
        });
    }

    public static void postNotification(Message message, Context context, Application application) {
        Intent notificationIntent = new Intent(context, BubbleChatActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 102, notificationIntent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);

        String serverName = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_SERVER_NAME);
        String serverUid = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_SERVER_UID);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        Intent replyIntent = new Intent(context, NotificationReceiver.class);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(context, 0, replyIntent, 0);

        RemoteInput remoteInput = new RemoteInput.Builder("key_reply")
                .setLabel("Reply")
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.send, "Reply", replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            CharSequence name = "Messages";
            String description = "Message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("message", name, importance);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);

            NotificationCompat.BubbleMetadata bubbleMetadata = new NotificationCompat.BubbleMetadata.Builder("short_id").build();
            Person person = new Person.Builder()
                    .setName(serverName)
                    .setImportant(true)
                    .build();
            Person you = new Person.Builder()
                    .setName("You")
                    .setImportant(true)
                    .build();
            ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(context, "short_id")
                    .setLongLived(true)
                    .setIntent(new Intent(context, BubbleChatActivity.class).setAction(Intent.ACTION_VIEW))
                    .setShortLabel(serverName)
                    .setPerson(person)
                    .build();
            ((ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE)).pushDynamicShortcut(shortcut.toShortcutInfo());
            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle((person));
            if (unreadMessages != null)
                for (Message message1 : unreadMessages) {
                    if (message1.getFrom().equals(serverUid))
                        messagingStyle.addMessage(new NotificationCompat.MessagingStyle.Message(message1.getMessage(), message1.getCurrMillis(), person));
                    else
                        messagingStyle.addMessage(new NotificationCompat.MessagingStyle.Message(message1.getMessage(), message1.getCurrMillis(), you));
                }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "message")
                    .setSmallIcon(R.drawable.notif_icon)
                    .setStyle(messagingStyle)
                    .setShortcutId("short_id")
                    .setOnlyAlertOnce(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .addAction(replyAction)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setBubbleMetadata(bubbleMetadata)
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(101, builder.build());
        } else {

            CharSequence name = "Messages";
            String description = "Message notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel("message", name, importance);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            channel.setDescription(description);

            notificationManager.createNotificationChannel(channel);

            String contentInfo = "";

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            List<Message> list1 = new ArrayList<>(unreadMessages);
            List<Message> list2 = new ArrayList<>();

            Collections.reverse(list1);
            int i = 0;
            for (Message message1 : list1) {
                i++;
                if (i == 6)
                    break;
                list2.add(message1);
            }

            Collections.reverse(list2);
            for (Message message1 : list2) {
                inboxStyle.addLine(message1.getMessage());
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "message")
                    .setSmallIcon(R.drawable.notif_icon)
                    .setContentIntent(contentIntent)
                    .setNumber(unreadMessages.size())
                    .setContentTitle(serverName)
                    .setContentText(message.getMessage())
                    .addAction(replyAction)
                    .setOnlyAlertOnce(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setStyle(inboxStyle)
                    .setContentText(contentInfo)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(101, builder.build());
        }
    }

    public static void stopWork() {
        if (messagesRef != null && handler != null)
            messagesRef.removeEventListener(handler);
    }

    public static List<Message> getUnreadMessages() {
        return unreadMessages;
    }

    public static void addUnreadMessages(Message message) {
        unreadMessages.add(message);
    }

    public static void createAndSendMessage(Map<String, String> map, Message message, Context context) {
        String serverPublic = map.get(CommonValues.SERVER_KEY);
        String myPublic = map.get(CommonValues.MY_PUBLIC_KEY);
        String myPrivate = map.get(CommonValues.MY_PRIVATE_KEY);
        String iv = map.get(CommonValues.IV);
        FMessage fMessage = new FMessage(message.getId(), map.get(CommonValues.ENCRYPTED_MESSAGE), message.getFrom(), message.getTo(), message.getMessageType(), CommonValues.STATUS_SENT, message.getCurrMillis(), null, null, null, serverPublic, myPublic, iv);
        FirebaseDb.getInstance().sendMessage(fMessage);
        ParentService.postNotification(message, context, (Application) context.getApplicationContext());
        Key key = new Key(UUID.randomUUID().toString(), myPrivate, myPublic, serverPublic, message.getCurrMillis());
        insertKey(key, context);
        insertMessage(message);
    }

    public static Map<String, String> encryptMessage(String text, Context context) {

        Key key = new KeyViewModel((Application) context.getApplicationContext()).getLastKeyWithServerPublic(ParentService.keys);
        if (key != null) {
            String serverPublic = key.getServerPublic();
            return Encryption.encryptText(text, serverPublic);
        }
        return null;
    }

    public static void insertKey(Key key, Context context) {
        new KeyViewModel((Application) context.getApplicationContext()).insert(key);
    }
}
