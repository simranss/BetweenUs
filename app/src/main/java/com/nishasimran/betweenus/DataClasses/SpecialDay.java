package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Values.DatabaseValues;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = DatabaseValues.TABLE_SPECIAL_DAY)
public class SpecialDay {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DatabaseValues.COLUMN_ID)
    private final String id;
    @ColumnInfo(name = DatabaseValues.COLUMN_DAY)
    private String day;
    @ColumnInfo(name = DatabaseValues.COLUMN_MONTH)
    private String month;
    @ColumnInfo(name = DatabaseValues.COLUMN_YEAR)
    private String year;
    @ColumnInfo(name = DatabaseValues.COLUMN_TITLE)
    private String title;
    @ColumnInfo(name = DatabaseValues.COLUMN_DESC)
    private String desc;
    @ColumnInfo(name = DatabaseValues.COLUMN_CURR_MILLIS)
    private long currMillis;



    public SpecialDay(@NotNull String id, String day, String month, String year, String title, String desc, long currMillis) {
        this.id = id;
        this.day = day;
        this.month = month;
        this.year = year;
        this.title = title;
        this.desc = desc;
        this.currMillis = currMillis;
    }



    // getters
    @NonNull
    public String getId() {
        return id;
    }
    public String getDay() {
        return day;
    }
    public String getMonth() {
        return month;
    }
    public String getYear() {
        return year;
    }
    public String getTitle() {
        return title;
    }
    public String getDesc() {
        return desc;
    }
    public long getCurrMillis() {
        return currMillis;
    }

    // setters
    public void setDay(String day) {
        this.day = day;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public void setCurrMillis(long currMillis) {
        this.currMillis = currMillis;
    }

}
