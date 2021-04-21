package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.Album;
import com.nishasimran.betweenus.Values.DatabaseValues;

import java.util.List;

@Dao
public interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Album album);

    @Update
    void update(Album album);

    @Delete
    void delete(Album album);

    @Query("DELETE FROM " + DatabaseValues.TABLE_ALBUMS)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_ALBUMS + " ORDER BY " + DatabaseValues.COLUMN_CURR_MILLIS + " ASC")
    LiveData<List<Album>> getAllAlbums();

    // Query with parameter that returns a specific word or words.
    @Query("SELECT * FROM " + DatabaseValues.TABLE_ALBUMS + " WHERE " + DatabaseValues.COLUMN_ALBUM_NAME + " LIKE :albumName ")
    List<Album> findAlbums(String albumName);
}
