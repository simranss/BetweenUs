package com.nishasimran.betweenus.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.nishasimran.betweenus.DataClasses.FirebaseKey;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.Fragments.LoginFragment;
import com.nishasimran.betweenus.Fragments.MainFragment;
import com.nishasimran.betweenus.Fragments.RegistrationFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.FirebaseValues;
import com.nishasimran.betweenus.ViewModels.KeyViewModel;
import com.nishasimran.betweenus.State.StateViewModel;
import com.nishasimran.betweenus.ViewModels.UserViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    DatabaseReference userRef = FirebaseValues.USER_REF, keyRef = FirebaseValues.KEY_REF;
    ChildEventListener userChildEventListener, keyChildEventListener;

    Observer<Boolean> connectionObserver;

    private List<Key> keys;

    private boolean isInternetAvail = false;
    private int fragmentIndex = 0;
    private String uid;

    private Fragment loginFragment, registrationFragment;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);

        loginFragment = new LoginFragment(this);
        registrationFragment = new RegistrationFragment(this);
        mainFragment = new MainFragment(this);

        // adding a listener for new key and new user data
        initChildEventListeners();
        addListenerForUserAndKeyData();

        // initialising the view model
        initConnectionObserver();
        restartListenerForConnectionChange();
        StateViewModel.getInstance(this, getApplication()).getState().observe(this, s -> {
            Log.d(TAG, "state: " + s);
            switch(s) {
                case CommonValues.NULL:
                    updateState(CommonValues.STATE_NOT_LOGGED_IN);
                    break;
                case CommonValues.STATE_NOT_LOGGED_IN:
                    fragmentIndex = 0;
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, loginFragment);
                    break;
                case CommonValues.STATE_LOGGED_IN_NO_REG:
                    this.uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);
                    fragmentIndex = 1;
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, registrationFragment);
                    break;
                case CommonValues.STATE_LOGGED_IN_WITH_REG:
                    this.uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);
                    fragmentIndex = 2;
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, mainFragment);
                    break;
            }
        });

        KeyViewModel.getInstance(this, getApplication()).getAllKeys().observe(this, keys -> {
            this.keys = keys;
            Log.d(TAG, "Keys updated: " + keys);
        });
    }

    public boolean isInternetAvail() {
        Log.d(TAG, "isInternetAvail: " + isInternetAvail);
        if (!isInternetAvail) {
            removeListenerForUserAndKeyData();
            addListenerForUserAndKeyData();
        }
        return isInternetAvail;
    }

    public void updateState(String state) {
        StateViewModel.getInstance(this, getApplication()).updateState(state);
    }

    public void insertUser(User user) {
        UserViewModel.getInstance(this, getApplication()).insert(user);
    }

    public void insertKey(Key key) {
        KeyViewModel.getInstance(this, getApplication()).insert(key);
    }

    private void initChildEventListeners() {
        userChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        Log.d(TAG, "User: " + user);
                        if (uid.equals(CommonValues.NULL)) {
                            updateState(CommonValues.STATE_NOT_LOGGED_IN);
                        } else {
                            Log.d(TAG, "User: " + user);
                            if (!uid.equals(user.getId())) {
                                insertUser(user);
                                snapshot.getRef().removeValue();
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
            public void onCancelled(@NonNull DatabaseError error) { }
        };

        keyChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    FirebaseKey fKey = snapshot.getValue(FirebaseKey.class);
                    if (fKey != null) {
                        Log.d(TAG, "Key: " + fKey);
                        Key tmpKey = KeyViewModel.getInstance(MainActivity.this, getApplication()).findKey(fKey.getId(), keys);
                        if (tmpKey != null) {
                            Log.d(TAG, "database key: " + tmpKey);
                            if (!tmpKey.getMyPublic().equals(fKey.getMyPublic())) {
                                Key key = new Key(fKey.getId(), null, null, fKey.getMyPublic(), fKey.getCurrMillis());
                                insertKey(key);
                                snapshot.getRef().removeValue();
                            }
                        } else {
                            Log.d(TAG, "database key: null");
                            Key key = new Key(fKey.getId(), null, null, fKey.getMyPublic(), fKey.getCurrMillis());
                            insertKey(key);
                            snapshot.getRef().removeValue();
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
            public void onCancelled(@NonNull DatabaseError error) { }
        };
    }

    private void initConnectionObserver() {
        connectionObserver = aBoolean -> {
            isInternetAvail = aBoolean;
            Log.d(TAG, "connected: " + isInternetAvail);
        };
    }

    public void removeListenerForUserAndKeyData() {
        userRef.removeEventListener(userChildEventListener);
        keyRef.removeEventListener(keyChildEventListener);
    }

    public void addListenerForUserAndKeyData() {
        userRef.addChildEventListener(userChildEventListener);
        keyRef.addChildEventListener(keyChildEventListener);
    }

    public void restartListenerForConnectionChange() {
        StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().removeObserver(connectionObserver);
        StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().observe(this, connectionObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseDb.getInstance().goOffline();
    }

    @Override
    public void onBackPressed() {
        finish();

        if (mainFragment.isDrawerOpen()) {
            mainFragment.closeDrawer();

        } else if (!mainFragment.isHomeFragment()) {
            mainFragment.loadFragment(0);

        } else {
            super.onBackPressed();
        }
    }
}