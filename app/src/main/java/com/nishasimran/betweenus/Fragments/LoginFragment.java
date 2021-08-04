package com.nishasimran.betweenus.Fragments;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseValues;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class LoginFragment extends Fragment {

    private final String TAG = "LoginFrag";

    private String verificationId;
    private boolean codeSent = false;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final MainActivity activity;

    private EditText phoneEditText, codeEditText;
    private ProgressBar progressBar;
    private Button submitButton;

    public LoginFragment(MainActivity activity) {
        this.activity = activity;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initViews(view);
        setDefaults();

        return view;
    }

    private void initViews(@NotNull View parent) {
        phoneEditText = parent.findViewById(R.id.frag_log_phone);
        codeEditText = parent.findViewById(R.id.frag_log_code);
        progressBar = parent.findViewById(R.id.frag_log_progress);
        submitButton = parent.findViewById(R.id.frag_log_submit);
    }

    private void setDefaults() {
        hideView(codeEditText);
        hideView(progressBar);
        submitButton.setText(R.string.submit);
        submitButton.setOnClickListener(v -> {
            if (codeSent) {
                submitCode();
            } else {
                startPhoneAuth();
            }
        });
    }

    private void startPhoneAuth() {
        String phone = validateEditText(phoneEditText, CommonValues.PHONE_LENGTH);
        if (phone != null) {
            phone = CommonValues.COUNTRY_CODE + phone;
            hideView(codeEditText);
            disableView(phoneEditText);
            showView(progressBar);
            disableView(submitButton);
            signIn(activity, phone, generateCallbacks());
        } else {
            phoneEditText.setError(CommonValues.ERROR_PHONE);
        }
    }

    private void submitCode() {
        String code = validateEditText(codeEditText, CommonValues.OTP_LENGTH);
        if (code != null) {
            showView(progressBar);
            disableView(codeEditText);
            disableView(phoneEditText);
            disableView(submitButton);
            signInWithCode(activity.getApplication(), code, verificationId);
        } else {
            codeEditText.setError(CommonValues.ERROR_OTP);
        }
    }

    private @Nullable String validateEditText(@NotNull EditText editText, int length) {
        if (editText.getText().toString().isEmpty() || (editText.getText().length() == 0)) {
            return null;
        } else if (editText.getText().toString().length() == length) {
            return editText.getText().toString();
        } else {
            return null;
        }
    }

    private PhoneAuthProvider.@NotNull OnVerificationStateChangedCallbacks generateCallbacks() {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "Verification success");
                disableView(phoneEditText);
                hideView(codeEditText);
                hideView(progressBar);
                disableView(submitButton);
                signInWithPhoneAuthCredential(activity.getApplication(), phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "verificationFailed: " + e.getMessage());
                if (e instanceof FirebaseNetworkException)
                    Toast.makeText(activity, "Check your internet connection", Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "Failure: " + e.getMessage(), Toast.LENGTH_LONG).show();
                hideView(codeEditText);
                hideView(progressBar);
                enableView(phoneEditText);
                enableView(submitButton);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                codeSent = true;
                disableView(phoneEditText);
                showView(codeEditText);
                codeEditText.requestFocus();
                Log.d(TAG, "otp editText hasFocus(): " + codeEditText.hasFocus());
                hideView(progressBar);
                enableView(submitButton);
                LoginFragment.this.verificationId = verificationId;
            }
        };
    }

    private void updateState(String state) {
        if (activity != null) {
            activity.updateState(state);
        }
    }

    public void signIn(Activity activity, String number, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(callbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signInWithPhoneAuthCredential(Application application, PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "Verification success");
                    hideView(progressBar);
                    disableView(codeEditText);
                    disableView(phoneEditText);
                    disableView(submitButton);
                    if (authResult.getUser() != null) {
                        activity.removeListenerForUserAndKeyData();
                        activity.addListenerForUserAndKeyData();
                        FirebaseDb.getInstance().removeConnectionChangeListener();
                        String uid = authResult.getUser().getUid();
                        Utils.writeToSharedPreference(application, CommonValues.SHARED_PREFERENCE_UID, uid);
                        activity.setUid(uid);
                        FirebaseDb.getInstance().listenersForConnectionChanges(uid, application);
                        updateState(CommonValues.STATE_LOGGED_IN_NO_REG);
                    } else {
                        Log.w(TAG, "signInWithPhoneAuthCredential", new NullPointerException(FirebaseValues.USER_NULL));
                    }

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "VerificationFailed: " + e.getMessage());
                    enableView(submitButton);
                    disableView(phoneEditText);
                    enableView(codeEditText);
                    hideView(progressBar);
                });
    }

    public void signInWithCode(Application application, String code, String verificationId) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(application, credential);
    }

    private void showView(@NotNull View view) {
        view.setVisibility(View.VISIBLE);
    }

    private void hideView(@NotNull View view) {
        view.setVisibility(View.GONE);
    }

    private void enableView(@NotNull View view) {
        if (view instanceof EditText) {
            view.setFocusableInTouchMode(true);
        } else if (view instanceof Button) {
            view.setEnabled(true);
            view.setClickable(true);
        } else {
            view.setEnabled(true);
        }
    }

    private void disableView(@NotNull View view) {
        if (view instanceof EditText) {
            view.setFocusable(false);
        } else if (view instanceof Button) {
            view.setEnabled(false);
            view.setClickable(false);
        } else {
            view.setEnabled(false);
        }
    }
}