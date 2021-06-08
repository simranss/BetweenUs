package com.nishasimran.betweenus.Repositories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DAOs.KeyDao;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.Database.CommonDatabase;

import java.util.List;

public class KeyRepository {

    private final String TAG = "KeyRepo";

    private final KeyDao keyDao;
    private final LiveData<List<Key>> allKeys;

    // Note that in order to unit test the KeyRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public KeyRepository(Application application) {
        CommonDatabase db = CommonDatabase.getDatabase(application);
        keyDao = db.keyDao();
        allKeys = keyDao.getAllKeys();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Key>> getAllKeys() {
        return allKeys;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Key key) {
        CommonDatabase.databaseWriteExecutor.execute(() -> keyDao.insert(key));
    }

    public void update(Key key) {
        CommonDatabase.databaseWriteExecutor.execute(() -> keyDao.update(key));
    }

    public void delete(Key key) {
        CommonDatabase.databaseWriteExecutor.execute(() -> keyDao.delete(key));
    }

    public void deleteAll() {
        CommonDatabase.databaseWriteExecutor.execute(keyDao::deleteAll);
    }

    public Key findKey(String id, List<Key> keys) {
        Log.d(TAG, "findKey: inside");
        if (keys != null) {
            Log.d(TAG, "findKey: keys not null");
            for (Key key : keys) {
                if (id.equals(key.getId())) {
                    return key;
                }
            }
        }
        return null;
    }
}
