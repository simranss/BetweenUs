package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DAOs.MemoryDao;
import com.nishasimran.betweenus.DataClasses.Memory;
import com.nishasimran.betweenus.Database.MemoryRoomDatabase;

import java.util.List;

public class MemoryRepository {

    private final MemoryDao dao;
    private final LiveData<List<Memory>> allMemories;

    // Note that in order to unit test the MemoryRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public MemoryRepository(Application application) {
        MemoryRoomDatabase db = MemoryRoomDatabase.getDatabase(application);
        dao = db.memoryDao();
        allMemories = dao.getAllMemories();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Memory>> getAllMemories() {
        return allMemories;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Memory memory) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.insert(memory));
    }

    public void update(Memory memory) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.update(memory));
    }

    public void delete(Memory memory) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.delete(memory));
    }

    public void deleteAll() {
        MemoryRoomDatabase.databaseWriteExecutor.execute(dao::deleteAll);
    }

    public List<Memory> findMemories(String text) {
        return dao.findMemories(text);
    }
}
