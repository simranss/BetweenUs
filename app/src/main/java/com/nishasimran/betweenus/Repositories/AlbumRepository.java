package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DAOs.AlbumDao;
import com.nishasimran.betweenus.DataClasses.Album;
import com.nishasimran.betweenus.Database.MemoryRoomDatabase;

import java.util.List;

public class AlbumRepository {

    private final AlbumDao dao;
    private LiveData<List<Album>> allAlbums;

    // Note that in order to unit test the AlbumRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public AlbumRepository(Application application) {
        MemoryRoomDatabase db = MemoryRoomDatabase.getDatabase(application);
        dao = db.albumDao();
        allAlbums = dao.getAllAlbums();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Album>> getAllAlbums() {
        return allAlbums;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Album album) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.insert(album));
    }

    public void update(Album album) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.update(album));
    }

    public void delete(Album album) {
        MemoryRoomDatabase.databaseWriteExecutor.execute(() -> dao.delete(album));
    }

    public void deleteAll() {
        MemoryRoomDatabase.databaseWriteExecutor.execute(dao::deleteAll);
    }

    public List<Album> findAlbums(String text) {
        return dao.findAlbums(text);
    }
}
