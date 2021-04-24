package com.nishasimran.betweenus.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.Fragments.LoginFragment;
import com.nishasimran.betweenus.Fragments.MainFragment;
import com.nishasimran.betweenus.Fragments.RegistrationFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.ViewModels.KeyViewModel;
import com.nishasimran.betweenus.ViewModels.StateViewModel;
import com.nishasimran.betweenus.ViewModels.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private boolean isInternetAvail = false;
    private int fragmentIndex = 0;

    private Fragment loginFragment, registrationFragment, mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginFragment = new LoginFragment(this);
        registrationFragment = new RegistrationFragment(this);
        mainFragment = new MainFragment();

        // initialising the view model
        StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().observe(this, aBoolean -> {
            isInternetAvail = aBoolean;
            Log.d(TAG, "connected: " + isInternetAvail);
        });
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
                    fragmentIndex = 1;
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, registrationFragment);
                    break;
                case CommonValues.STATE_LOGGED_IN_WITH_REG:
                    fragmentIndex = 2;
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, mainFragment);
                    break;
            }
        });
    }

    public boolean isInternetAvail() {
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

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseDb.getInstance().goOffline();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}