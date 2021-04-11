package com.nishasimran.betweenus.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("DELETE FROM users")
    void deleteAll();

    @Query("SELECT * FROM users ORDER BY id ASC")
    LiveData<List<User>> getAlphabetizedUsers();
}
