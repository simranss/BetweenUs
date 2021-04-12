package com.nishasimran.betweenus.Firebase;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class FirebaseAuthentication {

    private final String TAG = "FirebaseAuthentication";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    private Activity context;

    public FirebaseAuthentication(Activity context) {
        this.context = context;
    }

    public void signIn(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(context)
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

    private FirebaseUser signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        AtomicReference<FirebaseUser> user = null;
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    user.set(authResult.getUser());
                    Log.d(TAG, "Verification success");
                })
                .addOnFailureListener(e -> Log.d(TAG, "VerificationFailed: " + e.getMessage()));
        return user.get();
    }

    public void signOut() {
        auth.signOut();
    }


}
