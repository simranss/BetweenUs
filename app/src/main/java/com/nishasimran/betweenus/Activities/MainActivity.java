package com.nishasimran.betweenus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.FirebaseDataClasses.FirebaseKey;
import com.nishasimran.betweenus.Fragments.BlankFragment;
import com.nishasimran.betweenus.Fragments.LoginFragment;
import com.nishasimran.betweenus.Fragments.MainFragment;
import com.nishasimran.betweenus.Fragments.RegistrationFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.State.StateViewModel;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseValues;
import com.nishasimran.betweenus.ViewModels.KeyViewModel;
import com.nishasimran.betweenus.ViewModels.UserViewModel;
import com.nishasimran.betweenus.services.ParentService;

import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    DatabaseReference userRef = FirebaseValues.USER_REF, keyRef = FirebaseValues.KEY_REF;
    ChildEventListener userChildEventListener, keyChildEventListener;

    Observer<Boolean> connectionObserver;

    private List<Key> keys;

    private boolean isInternetAvail = false;
    private String uid;
    private String state;

    private Fragment loginFragment, registrationFragment, blankFragment;
    private MainFragment mainFragment;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopService(new Intent(getApplicationContext(), ParentService.class));

        this.uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);

        loginFragment = new LoginFragment(this);
        registrationFragment = new RegistrationFragment(this);
        mainFragment = new MainFragment(this);
        blankFragment = new BlankFragment();

        StateViewModel.getInstance(this, getApplication()).getState().observe(this, s -> {
            Log.d(TAG, "state: " + s);
            state = s;
            switch(s) {
                case CommonValues.NULL:
                    updateState(CommonValues.STATE_NOT_LOGGED_IN);
                    break;
                case CommonValues.STATE_NOT_LOGGED_IN:
                    Utils.showFragment(getSupportFragmentManager(), R.id.root_fragment_container, loginFragment);
                    break;
                case CommonValues.STATE_LOGGED_IN_NO_REG:
                    this.uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);
                    Utils.showFragment(getSupportFragmentManager(), R.id.root_fragment_container, registrationFragment);
                    break;
                case CommonValues.STATE_LOGGED_IN_WITH_REG:
                    Utils.showFragment(getSupportFragmentManager(), R.id.root_fragment_container, blankFragment);
                    initBiometric();
                    checkForBiometric();
                    this.uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);
                    break;
            }

            if (state != null && !state.equals(CommonValues.NULL) && !state.equals(CommonValues.STATE_NOT_LOGGED_IN)) {

                // adding a listener for new key and new user data
                initChildEventListeners();
                addListenerForUserAndKeyData();

                FirebaseDb.getInstance().goOnline();

                // initialising the view model
                initConnectionObserver();
                restartListenerForConnectionChange();
            }

        });

        KeyViewModel.getInstance(this, getApplication()).getAllKeys().observe(this, keys -> {
            this.keys = keys;
            Log.d(TAG, "Keys updated: " + keys);
        });
    }

    private void initBiometric() {

        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode != 12) {
                    Toast.makeText(getApplicationContext(), "Error: " + errorCode, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Utils.showFragment(getSupportFragmentManager(), R.id.root_fragment_container, mainFragment);
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Exit")
                .build();
    }

    private void checkForBiometric() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d(TAG, "App can authenticate using biometrics.");
                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.d(TAG, "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.d(TAG, "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Log.d(TAG, "BIOMETRIC_ERROR_NONE_ENROLLED");
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                Log.d(TAG, "BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED");
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                Log.d(TAG, "BIOMETRIC_ERROR_UNSUPPORTED");
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                Log.d(TAG, "BIOMETRIC_STATUS_UNKNOWN");
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        stopService(new Intent(getApplicationContext(), ParentService.class));

        StateViewModel.getInstance(this, getApplication()).getState().observe(this, s -> {
            Log.d(TAG, "state: " + s);
            state = s;
            switch(s) {
                case CommonValues.NULL:
                    updateState(CommonValues.STATE_NOT_LOGGED_IN);
                    break;
                case CommonValues.STATE_LOGGED_IN_WITH_REG:
                    Utils.showFragment(getSupportFragmentManager(), R.id.root_fragment_container, blankFragment);
                    initBiometric();
                    checkForBiometric();
                    this.uid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_UID);
                    break;
            }

            if (state != null && !state.equals(CommonValues.NULL) && !state.equals(CommonValues.STATE_NOT_LOGGED_IN)) {

                // adding a listener for new key and new user data
                initChildEventListeners();
                addListenerForUserAndKeyData();

                FirebaseDb.getInstance().goOnline();

                // initialising the view model
                initConnectionObserver();
                restartListenerForConnectionChange();
            }

        });
    }

    public boolean isInternetAvail() {
        Log.d(TAG, "isInternetAvail: " + isInternetAvail);
        if (!isInternetAvail) {
            FirebaseDb.getInstance().goOnline();
            removeListenerForUserAndKeyData();
            addListenerForUserAndKeyData();
        }
        return isInternetAvail;
    }

    public void updateState(String state) {
        StateViewModel.getInstance(this, getApplication()).updateState(state);
    }

    public void insertUser(User user) {
        new Thread(() -> UserViewModel.getInstance(this, getApplication()).insert(user)).start();
    }

    public void insertKey(Key key) {
        new Thread(() -> KeyViewModel.getInstance(this, getApplication()).insert(key)).start();
    }

    public void initChildEventListeners() {
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
                                String serverUid = Utils.getStringFromSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_UID);
                                if (serverUid == null || serverUid.equals(CommonValues.NULL)) {
                                    Utils.writeToSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_NAME, user.getName());
                                    Utils.writeToSharedPreference(getApplication(), CommonValues.SHARED_PREFERENCE_SERVER_UID, user.getId());
                                    insertUser(user);
                                }
                                insertUser(user);
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

    public void initConnectionObserver() {
        connectionObserver = aBoolean -> {
            isInternetAvail = aBoolean;
            Log.d(TAG, "connected: " + isInternetAvail);
        };
    }

    public void removeListenerForUserAndKeyData() {
        if (userChildEventListener != null && keyChildEventListener != null) {
            userRef.removeEventListener(userChildEventListener);
            keyRef.removeEventListener(keyChildEventListener);
        }
    }

    public void addListenerForUserAndKeyData() {
        if (userChildEventListener != null && keyChildEventListener != null) {
            userRef.addChildEventListener(userChildEventListener);
            keyRef.addChildEventListener(keyChildEventListener);
        }
    }

    public void restartListenerForConnectionChange() {
        StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().removeObserver(connectionObserver);
        StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().observe(this, connectionObserver);
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    @Override
    protected void onStop() {
        StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().removeObserver(connectionObserver);
        FirebaseDb.getInstance().removeConnectionChangeListener();
        if (mainFragment.chatFragment != null)
            mainFragment.chatFragment.removeMessageListener();
        FirebaseDb.getInstance().userOffline(uid);
        startService(new Intent(getApplicationContext(), ParentService.class));

        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isInternetAvail) {
            FirebaseDb.getInstance().userOnline(uid);
        }
    }

    @Override
    public void onBackPressed() {

        if (state.equals(CommonValues.STATE_LOGGED_IN_WITH_REG)) {

            if (mainFragment.chatFragment.fragment != null) {
                if (mainFragment.chatFragment.fragment.isVisible()) {
                    Utils.showFragment(getSupportFragmentManager(), R.id.root_fragment_container, mainFragment);
                }

            } else if (mainFragment.isDrawerOpen()) {
                mainFragment.closeDrawer();

            } else if (!mainFragment.isHomeFragment()) {
                mainFragment.loadFragment(0);

            } else if (!mainFragment.chatFragment.isVisible()) {
                mainFragment.loadFragment(0);
            } else {
                StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().removeObserver(connectionObserver);
                FirebaseDb.getInstance().removeConnectionChangeListener();
                mainFragment.chatFragment.removeMessageListener();
                FirebaseDb.getInstance().userOffline(uid);
                finish();
            }
        } else {
            StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().removeObserver(connectionObserver);
            FirebaseDb.getInstance().removeConnectionChangeListener();
            FirebaseDb.getInstance().userOffline(uid);
            finish();
        }
    }
}