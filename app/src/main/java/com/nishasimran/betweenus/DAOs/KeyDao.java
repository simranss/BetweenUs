package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.Strings.DatabaseStrings;

import java.util.List;

@Dao
public interface KeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Key key);

    @Update
    void update(Key key);

    @Delete
    void delete(Key key);

    @Query("DELETE FROM " + DatabaseStrings.TABLE_KEYS)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseStrings.TABLE_KEYS + " ORDER BY " + DatabaseStrings.COLUMN_CURR_MILLIS + " ASC")
    LiveData<List<Key>> getAllKeys();

    @Query("SELECT * FROM " + DatabaseStrings.TABLE_KEYS + " WHERE " + DatabaseStrings.COLUMN_CURR_MILLIS + " LIKE :currMillis ")
    List<Key> findKeys(long currMillis);
}
