package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.User;
import com.nishasimran.betweenus.Values.DatabaseValues;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM " + DatabaseValues.TABLE_USERS)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_USERS + " ORDER BY " + DatabaseValues.COLUMN_ID + " ASC")
    LiveData<List<User>> getAlphabetizedUsers();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_USERS + " WHERE " + DatabaseValues.COLUMN_ID + "=:uid")
    LiveData<User> getCurrentUser(String uid);
}
