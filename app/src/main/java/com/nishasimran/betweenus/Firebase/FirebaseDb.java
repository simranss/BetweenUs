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
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseStrings;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class FirebaseDb {

    private final String TAG = "FirebaseDatabase";

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference root = database.getReference();

    final MutableLiveData<String> lastSeen = new MutableLiveData<>();
    MutableLiveData<Boolean> connected = new MutableLiveData<>();
    final DatabaseReference connectedRef = database.getReference(FirebaseStrings.NETWORK_STATE_PATH);

    private static FirebaseDb INSTANCE = null;

    public static FirebaseDb getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseDb();

        return INSTANCE;
    }

    public LiveData<String> addListenerForServerLastSeen(String serverUid) {
        lastSeen.setValue(null);
        root.child(FirebaseStrings.USERS).child(serverUid).child(FirebaseStrings.LAST_SEEN).addValueEventListener(new ValueEventListener() {
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

    public LiveData<Boolean> listenersForConnectionChanges(String uid, Application application) {
        connected.setValue(false);
        connectedRef.addValueEventListener(new ValueEventListener() {
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
                                final DatabaseReference lastOnlineRef = root.child(FirebaseStrings.LAST_SEEN).child(uid);
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
        });

        return connected;
    }

    public void goOnline() {
        DatabaseReference.goOnline();
    }

    public void goOffline() {
        DatabaseReference.goOffline();
    }

}
