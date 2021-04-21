package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DAOs.KeyDao;
import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.Database.UserRoomDatabase;

import java.util.Arrays;
import java.util.List;

public class KeyRepository {

    private final KeyDao keyDao;
    private final LiveData<List<Key>> allKeys;

    // Note that in order to unit test the KeyRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public KeyRepository(Application application) {
        UserRoomDatabase db = UserRoomDatabase.getDatabase(application);
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
        UserRoomDatabase.databaseWriteExecutor.execute(() -> keyDao.insert(key));
    }

    public void update(Key key) {
        UserRoomDatabase.databaseWriteExecutor.execute(() -> keyDao.update(key));
    }

    public void delete(Key key) {
        UserRoomDatabase.databaseWriteExecutor.execute(() -> keyDao.delete(key));
    }

    public void deleteAll() {
        UserRoomDatabase.databaseWriteExecutor.execute(keyDao::deleteAll);
    }

    public Key findKeys(long currMillis, byte[] server_public) {
        List<Key> keys = keyDao.findKeys(currMillis);
        if (keys.isEmpty()) {
            return null;
        } else if (keys.size() == 1) {
            return keys.get(0);
        } else {
            for (Key key : keys) {
                if (key.getServerPublic().equals(Arrays.toString(server_public))) {
                    return key;
                }
            }
            return keys.get(0);
        }
    }

    public Key findKeys(long currMillis, String server_public) {
        List<Key> keys = keyDao.findKeys(currMillis);
        if (keys.isEmpty()) {
            return null;
        } else if (keys.size() == 1) {
            return keys.get(0);
        } else {
            for (Key key : keys) {
                if (key.getServerPublic().equals(server_public)) {
                    return key;
                }
            }
            return keys.get(0);
        }
    }
}
