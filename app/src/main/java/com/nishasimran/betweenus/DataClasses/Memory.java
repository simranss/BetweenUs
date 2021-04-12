package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Strings.DatabaseStrings;

@Entity(tableName = DatabaseStrings.TABLE_MEMORIES)
public class Memory {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DatabaseStrings.COLUMN_ID)
    private final String id;
    @ColumnInfo(name = DatabaseStrings.COLUMN_ALBUM_NAME)
    private String albumName;
    @ColumnInfo(name = DatabaseStrings.COLUMN_CURR_MILLIS)
    private long currMillis;
    @ColumnInfo(name = DatabaseStrings.COLUMN_IMAGE)
    private String image;
    @ColumnInfo(name = DatabaseStrings.COLUMN_DESC)
    private String desc;



    public Memory(@NonNull String id, String albumName, long currMillis, String image, String desc) {
        this.id = id;
        this.albumName = albumName;
        this.currMillis = currMillis;
        this.image = image;
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
    public String getImage() {
        return image;
    }
    public String getDesc() {
        return desc;
    }

    // setters
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public void setCurrMillis(long currMillis) {
        this.currMillis = currMillis;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
