package com.nishasimran.betweenus.Firebase;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.nishasimran.betweenus.Strings.CommonStrings;
import com.nishasimran.betweenus.Strings.FirebaseStrings;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class FirebaseDb {

    private final String TAG = "FirebaseDatabase";

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference root = database.getReference();

    private String lastSeen = null;

    private static FirebaseDb INSTANCE = null;

    public static FirebaseDb getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseDb();

        return INSTANCE;
    }

    public String getServerLastSeen(String uid) {
        lastSeen = null;
        root.child(FirebaseStrings.USERS).child(uid).child(FirebaseStrings.LAST_SEEN).get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        lastSeen = dataSnapshot.getValue(String.class);
                    }
                })
                .addOnFailureListener(e -> lastSeen = null);
        return lastSeen;
    }

    public void listenersForConnectionChanges(String uid, Application application) {
        final DatabaseReference connectedRef = database.getReference(FirebaseStrings.NETWORK_STATE_PATH);
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot snapshot) {
                Object connected = snapshot.getValue();
                if (connected instanceof Boolean) {
                    if ((boolean) connected) {
                        // When I disconnect, update the last time I was seen online
                        final DatabaseReference lastOnlineRef = root.child(FirebaseStrings.USERS).child(uid);
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

                        lastOnlineRef.setValue(CommonStrings.STATUS_ONLINE);

                        Utils.writeToSharedPreference(application, CommonStrings.SHARED_PREFERENCE_CONNECTION, CommonStrings.CONNECTION_CONNECTED);

                    } else {
                        Utils.writeToSharedPreference(application, CommonStrings.SHARED_PREFERENCE_CONNECTION, CommonStrings.CONNECTION_NOT_CONNECTED);
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled at .info/connected");
            }
        });
    }

    public void goOnline() {
        DatabaseReference.goOnline();
    }

    public void goOffline() {
        DatabaseReference.goOffline();
    }

}
