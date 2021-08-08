package com.nishasimran.betweenus.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nishasimran.betweenus.Fragments.ChatFragment;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;

public class BubbleChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble_chat);

        ChatFragment chatFragment = new ChatFragment(this);
        Utils.showFragment(getSupportFragmentManager(), R.id.bubble_chat_fragment, chatFragment);
    }
}