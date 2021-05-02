package com.nishasimran.betweenus.State;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class StateViewModel extends AndroidViewModel {

    private final String TAG = "StateVM";
    private final StateRepository repository;
    private static StateViewModel INSTANCE = null;


    public StateViewModel(@NonNull Application application) {
        super(application);
        repository = new StateRepository(application);
    }

    public static StateViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(StateViewModel.class);
        }
        return INSTANCE;
    }

    public LiveData<Boolean> addConnectionChangeListener() {
        return repository.addListenerForConnectionChanges();
    }

    public LiveData<String> addServerLastSeenChangeListener() {
        LiveData<String> lastSeen =  repository.addListenerForServerLastSeen();
        Log.d(TAG, "addServerLastSeenChangeListener: " + lastSeen.getValue());
        return lastSeen;
    }

    public LiveData<String> getState() {
        Log.d(TAG, "getState: " + repository.getState().getValue());
        return repository.getState();
    }

    public void updateState(String state) {
        Log.d(TAG, "updateState: " + state);
        repository.updateState(state);
    }

    public String getServerUid() {
        Log.d(TAG, "getServerUid: " + repository.getServerUid());
        return repository.getServerUid();
    }

    public String getUid() {
        Log.d(TAG, "getUid: " + repository.getUid());
        return repository.getUid();
    }

}
