package com.nishasimran.betweenus.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nishasimran.betweenus.DataClasses.Album;
import com.nishasimran.betweenus.Repositories.AlbumRepository;

import java.util.List;

public class AlbumViewModel extends AndroidViewModel {

    private final AlbumRepository repository;

    private final LiveData<List<Album>> allAlbums;

    public AlbumViewModel (Application application) {
        super(application);
        repository = new AlbumRepository(application);
        allAlbums = repository.getAllAlbums();
    }

    LiveData<List<Album>> getAllAlbums() { return allAlbums; }

    void insert(Album album) { repository.insert(album); }

    void update(Album album) { repository.update(album); }

    void delete(Album album) { repository.delete(album); }

    void deleteAll() { repository.deleteAll(); }

    Album findAlbum(String albumId) {
        return repository.findAlbum(albumId);
    }
}
