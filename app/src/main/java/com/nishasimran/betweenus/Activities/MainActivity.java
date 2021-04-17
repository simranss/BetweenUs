package com.nishasimran.betweenus.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.R;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseDb.getInstance().goOffline();
    }
}