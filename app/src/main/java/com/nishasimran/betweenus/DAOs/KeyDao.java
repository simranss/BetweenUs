package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.Key;
import com.nishasimran.betweenus.Values.DatabaseValues;

import java.util.List;

@Dao
public interface KeyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Key key);

    @Update
    void update(Key key);

    @Delete
    void delete(Key key);

    @Query("DELETE FROM " + DatabaseValues.TABLE_KEYS)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_KEYS + " ORDER BY " + DatabaseValues.COLUMN_CURR_MILLIS + " ASC")
    LiveData<List<Key>> getAllKeys();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_KEYS + " ORDER BY " + DatabaseValues.COLUMN_CURR_MILLIS + " DESC")
    LiveData<List<Key>> getAllKeysDesc();
}
