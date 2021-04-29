package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Values.DatabaseValues;

@Entity(tableName = DatabaseValues.TABLE_ALBUMS)
public class Album {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DatabaseValues.COLUMN_ID)
    private String id;
    @ColumnInfo(name = DatabaseValues.COLUMN_ALBUM_NAME)
    private String albumName;
    @ColumnInfo(name = DatabaseValues.COLUMN_CURR_MILLIS)
    private long currMillis;
    @ColumnInfo(name = DatabaseValues.COLUMN_DESC)
    private String desc;



    @Ignore
    public Album() {
        // required no argument constructor
    }



    public Album(@NonNull String id, String albumName, long currMillis, String desc) {
        this.id = id;
        this.albumName = albumName;
        this.currMillis = currMillis;
        this.desc = desc;
    }



    // getters
    @NonNull
    public String getId() {
        return id;
    }
    public String getAlbumName() {
        return albumName;
    }
    public long getCurrMillis() {
        return currMillis;
    }
    public String getDesc() {
        return desc;
    }

    // setters
    public void setId(@NonNull String id) {
        this.id = id;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public void setCurrMillis(long currMillis) {
        this.currMillis = currMillis;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
