package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.Memory;
import com.nishasimran.betweenus.Values.DatabaseValues;

import java.util.List;

@Dao
public interface MemoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Memory memory);

    @Update
    void update(Memory memory);

    @Delete
    void delete(Memory memory);

    @Query("DELETE FROM " + DatabaseValues.TABLE_MEMORIES)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_MEMORIES + " ORDER BY " + DatabaseValues.COLUMN_CURR_MILLIS + " ASC")
    LiveData<List<Memory>> getAllMemories();

    // Query with parameter that returns a specific word or words.
    @Query("SELECT * FROM " + DatabaseValues.TABLE_MEMORIES + " WHERE `" + DatabaseValues.COLUMN_DESC + "` LIKE :text ")
    List<Memory> findMemories(String text);
}
