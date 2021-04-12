package com.nishasimran.betweenus.Firebase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class FirebaseAuthentication {

    private final String TAG = "FirebaseAuthentication";

    FirebaseAuth auth = FirebaseAuth.getInstance();

    private static FirebaseAuthentication INSTANCE = null;

    public void signIn(Activity activity, String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d(TAG, "Verification success");
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.d(TAG, "verificationFailed: " + e.getMessage());
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> Log.d(TAG, "Verification success"))
                .addOnFailureListener(e -> Log.d(TAG, "VerificationFailed: " + e.getMessage()));
    }

    public void signOut() {
        auth.signOut();
    }

    public static FirebaseAuthentication getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FirebaseAuthentication();

        return INSTANCE;
    }

}
