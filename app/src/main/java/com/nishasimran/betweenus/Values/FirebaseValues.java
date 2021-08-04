package com.nishasimran.betweenus.Values;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nishasimran.betweenus.Firebase.FirebaseDb;

public class FirebaseValues {

    public final static String USERS = "users";
    public final static String LAST_SEEN = "last_seen";
    public final static String DISCONNECT = "disconnect";
    public final static String PUBLIC_KEY = "P";
    public final static String NETWORK_STATE_PATH = ".info/connected";
    public final static String KEYS = "keys";
    public final static String CURR_MILLIS = "c_m";
    public final static String MESSAGES = "messages";
    public final static String ID = "id";
    public final static String ENCRYPTED_MESSAGE = "e_m";
    public final static String MESSAGE_FROM = "f";
    public final static String MESSAGE_TO = "t";
    public final static String MESSAGE_TYPE = "m_t";
    public final static String MESSAGE_STATUS = "st";
    public final static String SENT_CURR_MILLIS = "s_c_m";
    public final static String DELIVERED_CURR_MILLIS = "d_c_m";
    public final static String READ_CURR_MILLIS = "r_c_m";
    public final static String SERVER_PUBLIC = "s_p";
    public final static String MY_PUBLIC = "p";
    public final static String IV = "iv";

    public final static String USER_NULL = "user is null";

    private final static FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private final static DatabaseReference ROOT = FirebaseDb.root;
    public final static DatabaseReference USER_REF = ROOT.child(USERS);
    public final static DatabaseReference KEY_REF = ROOT.child(KEYS);
    public final static DatabaseReference MESSAGE_REF = ROOT.child(MESSAGES);
    public final static DatabaseReference CONNECTED_REF = DATABASE.getReference(NETWORK_STATE_PATH);
}
