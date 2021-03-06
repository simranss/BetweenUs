package com.nishasimran.betweenus.Fragments;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Encryption.Encryption;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.FirebaseDataClasses.FirebaseKey;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseValues;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

public class RegistrationFragment extends Fragment {

    private final String TAG = "RegFrag";

    private final MainActivity activity;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private EditText nameEditText, dobEditText, emailEditText;
    private Button saveButton;
    private ProgressBar progressBar;
    private DatePickerDialog datePicker;

    private String id, phone, name, email;
    private long dob;

    private String privateKey, publicKey, keyId;
    private long keyCurrMillis;

    public RegistrationFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        FirebaseDb.getInstance().goOnline();
        isInternetAvailable();

        // adding a listener for new key and new user data
        activity.initChildEventListeners();
        activity.addListenerForUserAndKeyData();

        FirebaseDb.getInstance().goOnline();

        // initialising the view model
        activity.initConnectionObserver();
        activity.restartListenerForConnectionChange();

        initViews(view);
        setDefaults();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = auth.getCurrentUser();
    }

    private void initViews(@NotNull View parent) {
        nameEditText = parent.findViewById(R.id.frag_reg_name);
        dobEditText = parent.findViewById(R.id.frag_reg_dob);
        emailEditText = parent.findViewById(R.id.frag_reg_email);
        saveButton = parent.findViewById(R.id.frag_reg_save);
        progressBar = parent.findViewById(R.id.frag_reg_progress);
    }

    private void setDefaults() {
        progressBar.setVisibility(View.GONE);
        dobEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            // date picker dialog
            datePicker = new DatePickerDialog(getContext(), (view, year1, monthOfYear, dayOfMonth) -> {

                Log.d(TAG, "day: " + dayOfMonth + ", month: " + (monthOfYear + 1) + ", year: " + year1);
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year1);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Log.d(TAG, "selected date millis: " + myCalendar.getTimeInMillis());
                if (myCalendar.getTimeInMillis() < CommonValues.MIN_DOB) {
                    dob = myCalendar.getTimeInMillis();
                    dobEditText.setText(Utils.getFormattedDate(myCalendar.getTimeInMillis()));
                }

            }, year, month, day);
            datePicker.show();
        });

        saveButton.setOnClickListener(v -> {
            if (isInternetAvailable()) {
                progressBar.setVisibility(View.VISIBLE);
                if (validateInputs()) {

                    if (currentUser != null) {
                        id = currentUser.getUid();
                        phone = currentUser.getPhoneNumber();
                        name = nameEditText.getText().toString().trim();
                        email = emailEditText.getText().toString().trim();

                        User user = new User(id, name, dob, email, phone, null);

                        keyCurrMillis = System.currentTimeMillis();
                        keyId = UUID.randomUUID().toString();
                        byte[] private_key = Encryption.generatePrivateKey(new SecureRandom());
                        privateKey = Arrays.toString(private_key);
                        publicKey = Arrays.toString(Encryption.generatePublicKey(private_key));

                        Key key = new Key(keyId, privateKey, publicKey, null, keyCurrMillis);

                        insertUser(user);
                        insertKey(key);

                        database.getReference()
                                .child(FirebaseValues.USERS).child(id).setValue(user)
                                .addOnSuccessListener(aVoid -> {

                                    FirebaseKey fKey = new FirebaseKey(keyId, key.getMyPublic(), keyCurrMillis);

                                    database.getReference()
                                            .child(FirebaseValues.KEYS).child(keyId).setValue(fKey)
                                            .addOnSuccessListener(aVoid1 -> {
                                                updateState(CommonValues.STATE_LOGGED_IN_WITH_REG);
                                                progressBar.setVisibility(View.GONE);
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.GONE);
                                                Log.w(TAG, "setDefaults: uploading key", e);
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Log.w(TAG, "setDefaults: uploading user data", e);
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        updateState(CommonValues.STATE_NOT_LOGGED_IN);
                    }

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(activity, "Please enter all the details", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(activity, "No internet available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            nameEditText.setError(CommonValues.ERROR_REQUIRED);
            return false;
        }
        if (dobEditText.getText().toString().trim().isEmpty()) {
            dobEditText.setError(CommonValues.ERROR_REQUIRED);
            return false;
        }
        if (dob == 0) {
            dobEditText.setError(CommonValues.ERROR_REQUIRED);
            return false;
        }
        if (emailEditText.getText().toString().trim().isEmpty()) {
            emailEditText.setError(CommonValues.ERROR_REQUIRED);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString().trim()).matches()) {
            emailEditText.setError(CommonValues.ERROR_EMAIL);
            return false;
        }
        return true;
    }

    private boolean isInternetAvailable() {
        if (activity != null) {
            return activity.isInternetAvail();
        }
        return false;
    }

    private void updateState(String state) {
        if (activity != null) {
            activity.updateState(state);
        }
    }

    private void insertUser(User user) {
        if (activity != null) {
            activity.insertUser(user);
        }
    }

    private void insertKey(Key key) {
        if (activity != null) {
            activity.insertKey(key);
        }
    }
}