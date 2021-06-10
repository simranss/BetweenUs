package com.nishasimran.betweenus.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.EditText;

import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.ViewModels.UserViewModel;

import java.util.List;

public class UserDetailsActivity extends AppCompatActivity {

    private User user;
    private String userId;
    private List<User> users;

    private EditText editText;
    private ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userId = getIntent().getStringExtra(CommonValues.EXTRA_USER_ID);

        initViews();

        initListeners();
    }

    private void initListeners() {
        initListenerForViews();

        initOtherListeners();
    }

    private void initOtherListeners() {

    }

    private void initListenerForViews() {
        UserViewModel.getInstance(this, getApplication()).getAllUsers().observe(this, users1 -> {
            users = users1;
            if (users != null && !users.isEmpty()) {
                user = UserViewModel.getInstance(this, getApplication()).findUserById(userId, users);
                if (user != null) {
                    // TODO: update the views
                }
            }
        });

        root.setOnClickListener(v -> {
            editText.clearFocus();

        });
    }

    private void initViews() {
        editText = findViewById(R.id.user_det_edittext);
        root = findViewById(R.id.user_det_root);
    }
}