package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Strings.DatabaseStrings;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM " + DatabaseStrings.TABLE_USERS)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseStrings.TABLE_USERS + " ORDER BY " + DatabaseStrings.COLUMN_ID + " ASC")
    LiveData<List<User>> getAlphabetizedUsers();
}
