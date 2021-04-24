package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Values.DatabaseValues;

@Entity(tableName = DatabaseValues.TABLE_MESSAGES)
public class Message {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = DatabaseValues.COLUMN_ID)
    private final String id;
    @ColumnInfo(name = DatabaseValues.COLUMN_MESSAGE)
    private String message;
    @ColumnInfo(name = DatabaseValues.COLUMN_FROM)
    private String from;
    @ColumnInfo(name = DatabaseValues.COLUMN_TO)
    private String to;
    @ColumnInfo(name = DatabaseValues.COLUMN_M_TYPE)
    private String messageType;
    @ColumnInfo(name = DatabaseValues.COLUMN_STATUS)
    private String status;
    @ColumnInfo(name = DatabaseValues.COLUMN_CURR_MILLIS)
    private long currMillis;
    @ColumnInfo(name = DatabaseValues.COLUMN_SENT_CURR_MILLIS)
    private long sentCurrMillis;
    @ColumnInfo(name = DatabaseValues.COLUMN_DELIVERED_CURR_MILLIS)
    private long deliveredCurrMillis;
    @ColumnInfo(name = DatabaseValues.COLUMN_READ_CURR_MILLIS)
    private long readCurrMillis;




    public Message(@NonNull String id, String message, String from, String to, String messageType, String status, long currMillis, long sentCurrMillis, long deliveredCurrMillis, long readCurrMillis) {
        this.id = id;
        this.message = message;
        this.from = from;
        this.to = to;
        this.messageType = messageType;
        this.status = status;
        this.currMillis = currMillis;
        this.sentCurrMillis = sentCurrMillis;
        this.deliveredCurrMillis = deliveredCurrMillis;
        this.readCurrMillis = readCurrMillis;
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
    public long getSentCurrMillis() {
        return sentCurrMillis;
    }
    public long getDeliveredCurrMillis() {
        return deliveredCurrMillis;
    }
    public long getReadCurrMillis() {
        return readCurrMillis;
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
    public void setSentCurrMillis(long sentCurrMillis) {
        this.sentCurrMillis = sentCurrMillis;
    }
    public void setDeliveredCurrMillis(long deliveredCurrMillis) {
        this.deliveredCurrMillis = deliveredCurrMillis;
    }
    public void setReadCurrMillis(long readCurrMillis) {
        this.readCurrMillis = readCurrMillis;
    }
}
