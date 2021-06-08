package com.nishasimran.betweenus.Firebase;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.nishasimran.betweenus.FirebaseDataClasses.FMessage;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseValues;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class FirebaseDb {

    private final String TAG = "FirebaseDatabase";

    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference root = database.getReference();

    private final DatabaseReference messagesRef = FirebaseValues.MESSAGE_REF;

    final MutableLiveData<String> lastSeen = new MutableLiveData<>();
    MutableLiveData<Boolean> connected = new MutableLiveData<>();
    final DatabaseReference connectedRef = FirebaseValues.CONNECTED_REF;
    ValueEventListener connectionChangeListener = null;

    private static FirebaseDb INSTANCE = null;

    public static FirebaseDb getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseDb();

        return INSTANCE;
    }

    public LiveData<String> addListenerForServerLastSeen(String serverUid) {
        lastSeen.setValue(null);
        root.child(FirebaseValues.LAST_SEEN).child(serverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object object = snapshot.getValue();
                    if (object != null) {
                        if (object instanceof String) {
                            lastSeen.setValue((String) object);
                        } else if (object instanceof Long) {
                            lastSeen.setValue(String.valueOf((long) object));
                        }
                    } else {
                        lastSeen.setValue(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                lastSeen.setValue(null);
            }
        });
        return lastSeen;
    }

    private void initListenerForConnectionChange(String uid, Application application) {
        connectionChangeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object object = snapshot.getValue();
                    Log.d(TAG, "connected: " + object);
                    if (object instanceof Boolean) {
                        Log.d(TAG, "connected: " + (boolean) object);
                        if ((boolean) object) {
                            // When I disconnect, update the last time I was seen online
                            if (uid != null && !uid.equals(CommonValues.NULL)) {
                                final DatabaseReference lastOnlineRef = root.child(FirebaseValues.LAST_SEEN).child(uid);
                                lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

                                lastOnlineRef.setValue(CommonValues.STATUS_ONLINE);
                            }

                            Utils.writeToSharedPreference(application, CommonValues.SHARED_PREFERENCE_CONNECTION, CommonValues.CONNECTION_CONNECTED);
                            connected.setValue(true);

                        } else {
                            Utils.writeToSharedPreference(application, CommonValues.SHARED_PREFERENCE_CONNECTION, CommonValues.CONNECTION_NOT_CONNECTED);
                            connected.setValue(false);
                        }
                    }
                } else {
                    connected.setValue(false);
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled at .info/connected");
                connected.setValue(false);
            }
        };
    }

    public LiveData<Boolean> listenersForConnectionChanges(String uid, Application application) {
        connected.setValue(false);
        if (connectionChangeListener != null) {
            removeConnectionChangeListener();
            connectedRef.addValueEventListener(connectionChangeListener);
        } else {
            initListenerForConnectionChange(uid, application);
            connected.setValue(listenersForConnectionChanges(uid, application).getValue());
        }

        return connected;
    }

    public void removeConnectionChangeListener() {
        connectedRef.removeEventListener(connectionChangeListener);
    }

    public void goOnline() {
        DatabaseReference.goOnline();
    }

    public void goOffline() {
        DatabaseReference.goOffline();
    }

    public void sendMessage(FMessage message) {
        messagesRef.child(message.getId()).setValue(message.getMap()).addOnSuccessListener(unused -> messagesRef.child(message.getId()).child(FirebaseValues.SENT_CURR_MILLIS).setValue(ServerValue.TIMESTAMP));
    }

    public void updateMessageStatus(String id, String status, long currMillis) {
        FMessage message = new FMessage(id, null, null, null, null, status, null, null, null, null, null, null, null);
        switch (status) {
            case CommonValues.STATUS_SENDING:
                message.setCurrMillis(String.valueOf(currMillis));
                break;
            case CommonValues.STATUS_SENT:
                message.setSentCurrMillis(String.valueOf(currMillis));
                break;
            case CommonValues.STATUS_DELIVERED:
                message.setDeliveredCurrMillis(String.valueOf(currMillis));
                break;
            case CommonValues.STATUS_SEEN:
                message.setReadCurrMillis(String.valueOf(currMillis));
                break;
        }

        messagesRef.child(id).setValue(message.getMap());
    }

}
