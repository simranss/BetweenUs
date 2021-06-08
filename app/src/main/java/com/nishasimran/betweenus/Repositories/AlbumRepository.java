package com.nishasimran.betweenus.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DAOs.AlbumDao;
import com.nishasimran.betweenus.DataClasses.Album;
import com.nishasimran.betweenus.Database.CommonDatabase;

import java.util.List;

public class AlbumRepository {

    private final AlbumDao dao;
    private final LiveData<List<Album>> allAlbums;

    // Note that in order to unit test the AlbumRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public AlbumRepository(Application application) {
        CommonDatabase db = CommonDatabase.getDatabase(application);
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
        CommonDatabase.databaseWriteExecutor.execute(() -> dao.insert(album));
    }

    public void update(Album album) {
        CommonDatabase.databaseWriteExecutor.execute(() -> dao.update(album));
    }

    public void delete(Album album) {
        CommonDatabase.databaseWriteExecutor.execute(() -> dao.delete(album));
    }

    public void deleteAll() {
        CommonDatabase.databaseWriteExecutor.execute(dao::deleteAll);
    }

    public Album findAlbum(String id, List<Album> albums) {
        if (albums != null) {
            for (Album album : albums) {
                if (id.equals(album.getId())) {
                    return album;
                }
            }
        }
        return null;
    }
}
