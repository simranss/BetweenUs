package com.nishasimran.betweenus.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nishasimran.betweenus.DataClasses.SpecialDay;
import com.nishasimran.betweenus.Strings.DatabaseStrings;

import java.util.List;

@Dao
public interface SpecialDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SpecialDay specialDay);

    @Update
    void update(SpecialDay specialDay);

    @Delete
    void delete(SpecialDay specialDay);

    @Query("DELETE FROM " + DatabaseStrings.TABLE_SPECIAL_DAY)
    void deleteAll();

    @Query("SELECT * FROM " + DatabaseStrings.TABLE_SPECIAL_DAY + " ORDER BY " + DatabaseStrings.COLUMN_CURR_MILLIS + " ASC")
    LiveData<List<SpecialDay>> getAllSpecialDays();

    // Query with parameter that returns a specific word or words.
    @Query("SELECT * FROM " + DatabaseStrings.TABLE_SPECIAL_DAY + " WHERE " + DatabaseStrings.COLUMN_TITLE + " LIKE :text ")
    List<SpecialDay> findSpecialDays(String text);
}
