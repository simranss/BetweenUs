package com.nishasimran.betweenus.Repositories;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.DAOs.UserDao;
import com.nishasimran.betweenus.Database.CommonDatabase;
import com.nishasimran.betweenus.Values.CommonValues;

import java.util.List;

public class UserRepository {

    private final UserDao userDao;
    private final LiveData<List<User>> allUsers;
    private final String uid;

    // Note that in order to unit test the UserRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public UserRepository(Application application) {
        CommonDatabase db = CommonDatabase.getDatabase(application);
        userDao = db.userDao();
        allUsers = userDao.getAlphabetizedUsers();

        SharedPreferences prefs = application.getSharedPreferences(CommonValues.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        uid = prefs.getString(CommonValues.SHARED_PREFERENCE_UID, CommonValues.NULL);
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(User user) {
        CommonDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        CommonDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        CommonDatabase.databaseWriteExecutor.execute(() -> userDao.delete(user));
    }

    public void deleteAll() {
        CommonDatabase.databaseWriteExecutor.execute(userDao::deleteAll);
    }

    public User getCurrentUser(List<User> users) {
        if (users != null) {
            for (User user : users) {
                if (uid.equals(user.getId())) {
                    return user;
                }
            }
        }
        return null;
    }
}
