package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DAOs.SpecialDayDao;
import com.nishasimran.betweenus.DataClasses.SpecialDay;
import com.nishasimran.betweenus.Database.MemoryRoomDatabase;

import java.util.List;

public class SpecialDayRepository {

    private final SpecialDayDao dao;
    private final LiveData<List<SpecialDay>> allSpecialDays;

    // Note that in order to unit test the SpecialDayRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public SpecialDayRepository(Application application) {
        MemoryRoomDatabase db = MemoryRoomDatabase.getDatabase(application);
        dao = db.specialDayDao();
        allSpecialDays = dao.getAllSpecialDays();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<SpecialDay>> getAllSpecialDays() {
        return allSpecialDays;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(SpecialDay specialDay) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.insert(specialDay));
    }

    public void update(SpecialDay specialDay) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.update(specialDay));
    }

    public void delete(SpecialDay specialDay) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.delete(specialDay));
    }

    public void deleteAll() {
        MemoryRoomDatabase.databaseWriteExecutor.execute(dao::deleteAll);
    }

    public List<SpecialDay> findSpecialDays(String text) {
        return dao.findSpecialDays(text);
    }
}
