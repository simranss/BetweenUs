package com.nishasimran.betweenus.Values;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseValues {

    public final static String USERS = "users";
    public final static String LAST_SEEN = "last_seen";
    public final static String PUBLIC_KEY = "P";
    public final static String NETWORK_STATE_PATH = ".info/connected";
    public final static String KEYS = "keys";
    public final static String CURR_MILLIS = "curr_millis";

    public final static String USER_NULL = "user is null";

    private final static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private final static DatabaseReference ROOT = DATABASE.getReference();
    public final static DatabaseReference USER_REF = ROOT.child(USERS);
    public final static DatabaseReference KEY_REF = ROOT.child(KEYS);
    public final static DatabaseReference CONNECTED_REF = DATABASE.getReference(NETWORK_STATE_PATH);
}
