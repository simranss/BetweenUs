package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.SpecialDay;
import com.nishasimran.betweenus.Values.DatabaseValues;

import java.util.List;

@Dao
public interface SpecialDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SpecialDay specialDay);

    @Update
    void update(SpecialDay specialDay);

    @Delete
    void delete(SpecialDay specialDay);

    @Query("DELETE FROM " + DatabaseValues.TABLE_SPECIAL_DAY)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseValues.TABLE_SPECIAL_DAY + " ORDER BY " + DatabaseValues.COLUMN_CURR_MILLIS + " ASC")
    LiveData<List<SpecialDay>> getAllSpecialDays();

    // Query with parameter that returns a specific word or words.
    @Query("SELECT * FROM " + DatabaseValues.TABLE_SPECIAL_DAY + " WHERE " + DatabaseValues.COLUMN_ID + " = :id ")
    LiveData<SpecialDay> findSpecialDay(String id);
}
