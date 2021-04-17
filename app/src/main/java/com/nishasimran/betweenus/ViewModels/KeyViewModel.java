package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.Repositories.KeyRepository;

import java.util.List;

public class KeyViewModel extends AndroidViewModel {

    private final KeyRepository repository;

    private final LiveData<List<Key>> allKeys;

    public KeyViewModel (Application application) {
        super(application);
        repository = new KeyRepository(application);
        allKeys = repository.getAllKeys();
    }

    LiveData<List<Key>> getAllKeys() { return allKeys; }

    public void insert(Key key) { repository.insert(key); }

    public void update(Key key) { repository.update(key); }

    public void delete(Key key) { repository.delete(key); }

    public void deleteAll() { repository.deleteAll(); }

    List<Key> findKeys(long currMillis) {
        return repository.findKeys(currMillis);
    }
}
