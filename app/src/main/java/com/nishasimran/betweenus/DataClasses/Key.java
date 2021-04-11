package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Strings.DatabaseStrings;

@Entity(tableName = DatabaseStrings.TABLE_KEYS)
public class Key {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DatabaseStrings.COLUMN_ID)
    private final String id;
    @ColumnInfo(name = DatabaseStrings.COLUMN_MY_PRIVATE)
    private String myPrivate;
    @ColumnInfo(name = DatabaseStrings.COLUMN_MY_PUBLIC)
    private String myPublic;
    @ColumnInfo(name = DatabaseStrings.COLUMN_SERVER_PUBLIC)
    private String serverPublic;
    @ColumnInfo(name = DatabaseStrings.COLUMN_CURR_MILLIS)
    private long currMillis;



    public Key(@NonNull String id, String myPrivate, String myPublic, String serverPublic, long currMillis) {
        this.id = id;
        this.myPrivate = myPrivate;
        this.myPublic = myPublic;
        this.serverPublic = serverPublic;
        this.currMillis = currMillis;
    }



    // getters
    @NonNull
    public String getId() {
        return id;
    }
    public String getMyPrivate() {
        return myPrivate;
    }
    public long getCurrMillis() {
        return currMillis;
    }
    public String getMyPublic() {
        return myPublic;
    }
    public String getServerPublic() {
        return serverPublic;
    }

    // setters
    public void setMyPrivate(String myPrivate) {
        this.myPrivate = myPrivate;
    }
    public void setCurrMillis(long currMillis) {
        this.currMillis = currMillis;
    }
    public void setMyPublic(String myPublic) {
        this.myPublic = myPublic;
    }
    public void setServerPublic(String serverPublic) {
        this.serverPublic = serverPublic;
    }

}
