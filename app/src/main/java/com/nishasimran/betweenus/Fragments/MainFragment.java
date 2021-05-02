package com.nishasimran.betweenus.Fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.R;

public class MainFragment extends Fragment {

    private final String TAG = "MainFrag";

    private final Activity activity;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public MainFragment(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        FirebaseDb.getInstance().goOnline();
        if (isInternetAvailable()) {
            restartConnectionChangeListener();
        }

        return view;
    }

    private boolean isInternetAvailable() {
        if (activity instanceof MainActivity) {
            return ((MainActivity) activity).isInternetAvail();
        }
        return false;
    }

    private void restartConnectionChangeListener() {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).restartListenerForConnectionChange();
        }
    }

    private void updateState(String state) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).updateState(state);
        }
    }

    private void insertUser(User user) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).insertUser(user);
        }
    }

    private void insertKey(Key key) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).insertKey(key);
        }
    }
}