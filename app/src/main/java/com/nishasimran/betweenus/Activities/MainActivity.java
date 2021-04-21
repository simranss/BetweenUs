package com.nishasimran.betweenus.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.Fragments.LoginFragment;
import com.nishasimran.betweenus.Fragments.MainFragment;
import com.nishasimran.betweenus.Fragments.RegistrationFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Strings.CommonStrings;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.ViewModels.StateViewModel;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    boolean isInternetAvail = false;

    Fragment loginFragment = new LoginFragment();
    Fragment registrationFragment = new RegistrationFragment();
    Fragment mainFragment = new MainFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialising the view model
        StateViewModel.getInstance(this, getApplication()).addConnectionChangeListener().observe(this, aBoolean -> {
            isInternetAvail = aBoolean;
            Log.d(TAG, "connected: " + isInternetAvail);
        });
        StateViewModel.getInstance(this, getApplication()).getState().observe(this, s -> {
            Log.d(TAG, "state: " + s);
            switch(s) {
                case CommonStrings.NULL:
                    StateViewModel.getInstance(this, getApplication()).updateState(CommonStrings.STATE_NOT_LOGGED_IN);
                    break;
                case CommonStrings.STATE_NOT_LOGGED_IN:
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, loginFragment);
                    break;
                case CommonStrings.STATE_LOGGED_IN_NO_REG:
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, registrationFragment);
                    break;
                case CommonStrings.STATE_LOGGED_IN_WITH_REG:
                    Utils.showFragment(getSupportFragmentManager(), R.id.fragment_container, mainFragment);
                    break;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseDb.getInstance().goOffline();
    }
}