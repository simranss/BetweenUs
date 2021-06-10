package com.nishasimran.betweenus.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.Repositories.KeyRepository;

import java.util.List;

public class KeyViewModel extends AndroidViewModel {

    private final String TAG = "KeyVM";
    private static KeyViewModel INSTANCE = null;

    private final KeyRepository repository;

    private final LiveData<List<Key>> allKeys;

    public KeyViewModel (Application application) {
        super(application);
        repository = new KeyRepository(application);
        allKeys = repository.getAllKeys();
    }

    public static KeyViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(KeyViewModel.class);
        }
        return INSTANCE;
    }

    public LiveData<List<Key>> getAllKeys() { return allKeys; }

    public void insert(Key key) { repository.insert(key); }

    public void update(Key key) { repository.update(key); }

    public void delete(Key key) { repository.delete(key); }

    public void deleteAll() { repository.deleteAll(); }

    public Key findKey(String keyId, List<Key> keys) {
        Log.d(TAG, "findKey: inside");
        return repository.findKey(keyId, keys);
    }

    public Key getLastKeyWithServerPublic(List<Key> keys) {
        return repository.getLastKeyWithServerPublic(keys);
    }

    public Key findKeyByMyPublic(String myPublic, List<Key> keys) {
        return repository.findKeyByMyPublic(myPublic, keys);
    }
}
