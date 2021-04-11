package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Strings.DatabaseStrings;

@Entity(tableName = DatabaseStrings.TABLE_MESSAGES)
public class Message {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = DatabaseStrings.COLUMN_ID)
    private final String id;
    @ColumnInfo(name = DatabaseStrings.COLUMN_MESSAGE)
    private String message;
    @ColumnInfo(name = DatabaseStrings.COLUMN_FROM)
    private String from;
    @ColumnInfo(name = DatabaseStrings.COLUMN_TO)
    private String to;
    @ColumnInfo(name = DatabaseStrings.COLUMN_M_TYPE)
    private String messageType;
    @ColumnInfo(name = DatabaseStrings.COLUMN_STATUS)
    private String status;
    @ColumnInfo(name = DatabaseStrings.COLUMN_CURR_MILLIS)
    private long currMillis;




    public Message(@NonNull String id, String message, String from, String to, String messageType, String status, long currMillis) {
        this.id = id;
        this.message = message;
        this.from = from;
        this.to = to;
        this.messageType = messageType;
        this.status = status;
        this.currMillis = currMillis;
    }



    // getters
    @NonNull
    public String getId() {
        return id;
    }
    public String getMessage() {
        return message;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getMessageType() {
        return messageType;
    }
    public String getStatus() {
        return status;
    }
    public long getCurrMillis() {
        return currMillis;
    }

    // setters
    public void setMessage(String message) {
        this.message = message;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setCurrMillis(long currMillis) {
        this.currMillis = currMillis;
    }
}
