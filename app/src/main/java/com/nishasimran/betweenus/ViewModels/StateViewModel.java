package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.Repositories.StateRepository;

public class StateViewModel extends AndroidViewModel {
    private final StateRepository repository;


    public StateViewModel(@NonNull Application application) {
        super(application);
        repository = new StateRepository(application);
    }

    public LiveData<Boolean> addConnectionChangeListener() {
        return repository.addListenerForConnectionChanges();
    }

    public LiveData<String> addServerLastSeenChangeListener() {
        return repository.addListenerForServerLastSeen();
    }

    public LiveData<String> getState() {
        return repository.getState();
    }

    public void updateState(String state) {
        repository.updateState(state);
    }

    public String getServerUid() {
        return repository.getServerUid();
    }

    public String getUid() {
        return repository.getUid();
    }

}
