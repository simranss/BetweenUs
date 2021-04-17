package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.nishasimran.betweenus.Strings.CommonStrings;
import com.nishasimran.betweenus.Utils.Utils;

import org.jetbrains.annotations.NotNull;

public class StateRepository {

    private final Application application;
    private final String uid, serverUid;
    private MutableLiveData<String> state;

    public StateRepository(@NotNull Application application) {
        this.application = application;
        uid = Utils.getStringFromSharedPreference(application, CommonStrings.SHARED_PREFERENCE_UID);
        serverUid = Utils.getStringFromSharedPreference(application, CommonStrings.SHARED_PREFERENCE_SERVER_UID);
        if (state == null) {
            state = new MutableLiveData<>();
        }
        state.setValue(Utils.getStringFromSharedPreference(application, CommonStrings.SHARED_PREFERENCE_STATE));
    }

    public String getUid() {
        return uid;
    }
    public String getServerUid() {
        return serverUid;
    }
    public MutableLiveData<String> getState() {
        return state;
    }

    public void updateState(String state) {
        Utils.writeToSharedPreference(application, CommonStrings.SHARED_PREFERENCE_STATE, state);
        this.state.setValue(state);
    }

}
