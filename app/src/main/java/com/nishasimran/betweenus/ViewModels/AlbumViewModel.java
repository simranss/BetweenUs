package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.nishasimran.betweenus.DataClasses.Album;
import com.nishasimran.betweenus.Repositories.AlbumRepository;

import java.util.List;

public class AlbumViewModel extends AndroidViewModel {

    private final String TAG = "AlbumVM";
    private static AlbumViewModel INSTANCE = null;

    private final AlbumRepository repository;

    private final LiveData<List<Album>> allAlbums;

    public AlbumViewModel (Application application) {
        super(application);
        repository = new AlbumRepository(application);
        allAlbums = repository.getAllAlbums();
    }

    public static AlbumViewModel getInstance(@NonNull ViewModelStoreOwner owner, @NonNull Application application) {
        if (INSTANCE == null) {
            ViewModelProvider.AndroidViewModelFactory factory = new ViewModelProvider.AndroidViewModelFactory(application);
            INSTANCE = new ViewModelProvider(owner, factory).get(AlbumViewModel.class);
        }
        return INSTANCE;
    }

    LiveData<List<Album>> getAllAlbums() { return allAlbums; }

    public void insert(Album album) { repository.insert(album); }

    public void update(Album album) { repository.update(album); }

    public void delete(Album album) { repository.delete(album); }

    public void deleteAll() { repository.deleteAll(); }

    public Album findAlbum(String albumId, List<Album> albums) {
        return repository.findAlbum(albumId, albums);
    }
}
