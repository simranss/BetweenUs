package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Database.UserDao;
import com.nishasimran.betweenus.Database.UserRoomDatabase;

import java.util.List;

public class UserRepository {
    
    private final UserDao userDao;
    private LiveData<List<User>> allUsers;

    // Note that in order to unit test the UserRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public UserRepository(Application application) {
        UserRoomDatabase db = UserRoomDatabase.getDatabase(application);
        userDao = db.userDao();
        allUsers = userDao.getAlphabetizedUsers();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(User user) {
        UserRoomDatabase.databaseWriteExecutor.execute(() -> userDao.insert(user));
    }

    public void update(User user) {
        UserRoomDatabase.databaseWriteExecutor.execute(() -> userDao.update(user));
    }

    public void delete(User user) {
        UserRoomDatabase.databaseWriteExecutor.execute(() -> userDao.delete(user));
    }

    public void deleteAll() {
        UserRoomDatabase.databaseWriteExecutor.execute(userDao::deleteAll);
    }
}
