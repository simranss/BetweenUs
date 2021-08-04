package com.nishasimran.betweenus.State;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nishasimran.betweenus.Firebase.FirebaseDb;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class StateRepository {

    private final Application application;
    private MutableLiveData<String> state;

    public StateRepository(@NotNull Application application) {
        this.application = application;
        if (state == null) {
            state = new MutableLiveData<>();
        }
        String stateValue = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_STATE);
        if (stateValue == null) {
            Utils.writeToSharedPreference(application, CommonValues.SHARED_PREFERENCE_STATE, CommonValues.STATE_NOT_LOGGED_IN);
            stateValue = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_STATE);
        }
        state.setValue(stateValue);
    }

    public LiveData<String> getState() {
        return state;
    }

    public void updateState(String state) {
        Utils.writeToSharedPreference(application, CommonValues.SHARED_PREFERENCE_STATE, state);
        this.state.setValue(state);
    }

    public LiveData<Boolean> addListenerForConnectionChanges() {
        String uid = Utils.getStringFromSharedPreference(application, CommonValues.SHARED_PREFERENCE_UID);
        return FirebaseDb.getInstance().listenersForConnectionChanges(uid, application);
    }

}
