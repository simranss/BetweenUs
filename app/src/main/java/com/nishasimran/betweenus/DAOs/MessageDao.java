package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.Message;
import com.nishasimran.betweenus.Values.DatabaseValues;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    @Update
    void update(Message message);

    @Delete
    void delete(Message message);

    @Query("DELETE FROM " + DatabaseValues.TABLE_MESSAGES)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_MESSAGES + " ORDER BY " + DatabaseValues.COLUMN_CURR_MILLIS + " ASC")
    LiveData<List<Message>> getAllMessages();

    // Query with parameter that returns a specific word or words.
    @Query("SELECT * FROM " + DatabaseValues.TABLE_MESSAGES + " WHERE " + DatabaseValues.COLUMN_ID + " = :id ")
    LiveData<Message> findMessage(String id);
}
