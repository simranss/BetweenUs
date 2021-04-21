package com.nishasimran.betweenus.Fragments;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nishasimran.betweenus.Activities.MainActivity;
import com.nishasimran.betweenus.R;
import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.FirebaseStrings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class LoginFragment extends Fragment {

    private final String TAG = "LoginFrag";

    private String verificationId;
    private boolean codeSent = false;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final AppCompatActivity activity;

    private EditText phoneEditText, codeEditText;
    private Button submitButton;

    public LoginFragment(AppCompatActivity activity) {
        this.activity = activity;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initViews(view);
        setDefaults();

        return view;
    }

    private void initViews(@NotNull View parent) {
        phoneEditText = parent.findViewById(R.id.frag_log_phone);
        codeEditText = parent.findViewById(R.id.frag_log_code);
        submitButton = parent.findViewById(R.id.frag_log_submit);
    }

    private void setDefaults() {
        hideView(codeEditText);
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
            signIn(activity, phone, generateCallbacks());
        } else {
            phoneEditText.setError(getString(R.string.phone_error));
        }
    }

    private void submitCode() {
        String code = validateEditText(codeEditText, CommonValues.OTP_LENGTH);
        if (code != null) {
            signInWithCode(activity.getApplication(), code, verificationId);
        } else {
            codeEditText.setError(getString(R.string.otp_error));
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
                signInWithPhoneAuthCredential(activity.getApplication(), phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "verificationFailed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);

                codeSent = true;
                showView(codeEditText);
                LoginFragment.this.verificationId = verificationId;
            }
        };
    }

    private void updateState(String state) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).updateState(state);
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
                    updateState(CommonValues.STATE_LOGGED_IN_NO_REG);
                    if (authResult.getUser() != null) {
                        Utils.writeToSharedPreference(application, CommonValues.SHARED_PREFERENCE_UID, authResult.getUser().getUid());
                    } else {
                        Log.w(TAG, "signInWithPhoneAuthCredential", new NullPointerException(FirebaseStrings.USER_NULL));
                    }

                })
                .addOnFailureListener(e -> Log.d(TAG, "VerificationFailed: " + e.getMessage()));
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
}