package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Values.DatabaseValues;

import java.util.Objects;

@Entity(tableName = DatabaseValues.TABLE_MESSAGES)
public class Message {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DatabaseValues.COLUMN_ID)
    private String id;
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
    private Long currMillis;
    @ColumnInfo(name = DatabaseValues.COLUMN_SENT_CURR_MILLIS)
    private Long sentCurrMillis;
    @ColumnInfo(name = DatabaseValues.COLUMN_DELIVERED_CURR_MILLIS)
    private Long deliveredCurrMillis;
    @ColumnInfo(name = DatabaseValues.COLUMN_READ_CURR_MILLIS)
    private Long readCurrMillis;




    @Ignore
    public Message() {
        // required no argument constructor
    }




    public Message(@NonNull String id, String message, String from, String to, String messageType, String status, Long currMillis, Long sentCurrMillis, Long deliveredCurrMillis, Long readCurrMillis) {
        this.id = id;
        this.message = message;
        this.from = from;
        this.to = to;
        this.messageType = messageType;
        this.status = status;
        if (currMillis != null)
            this.currMillis = currMillis;
        if (sentCurrMillis != null)
            this.sentCurrMillis = sentCurrMillis;
        if (deliveredCurrMillis != null)
            this.deliveredCurrMillis = deliveredCurrMillis;
        if (readCurrMillis != null)
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
    public Long getCurrMillis() {
        return currMillis;
    }
    public Long getSentCurrMillis() {
        return sentCurrMillis;
    }
    public Long getDeliveredCurrMillis() {
        return deliveredCurrMillis;
    }
    public Long getReadCurrMillis() {
        return readCurrMillis;
    }

    // setters
    public void setId(@NonNull String id) {
        this.id = id;
    }
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
    public void setCurrMillis(Long currMillis) {
        this.currMillis = currMillis;
    }
    public void setSentCurrMillis(Long sentCurrMillis) {
        this.sentCurrMillis = sentCurrMillis;
    }
    public void setDeliveredCurrMillis(Long deliveredCurrMillis) {
        this.deliveredCurrMillis = deliveredCurrMillis;
    }
    public void setReadCurrMillis(Long readCurrMillis) {
        this.readCurrMillis = readCurrMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return getId().equals(message1.getId()) &&
                Objects.equals(getMessage(), message1.getMessage()) &&
                Objects.equals(getFrom(), message1.getFrom()) &&
                Objects.equals(getTo(), message1.getTo()) &&
                Objects.equals(getMessageType(), message1.getMessageType()) &&
                Objects.equals(getStatus(), message1.getStatus()) &&
                Objects.equals(getCurrMillis(), message1.getCurrMillis()) &&
                Objects.equals(getSentCurrMillis(), message1.getSentCurrMillis()) &&
                Objects.equals(getDeliveredCurrMillis(), message1.getDeliveredCurrMillis()) &&
                Objects.equals(getReadCurrMillis(), message1.getReadCurrMillis());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMessage(), getFrom(), getTo(), getMessageType(), getStatus(), getCurrMillis(), getSentCurrMillis(), getDeliveredCurrMillis(), getReadCurrMillis());
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", messageType='" + messageType + '\'' +
                ", status='" + status + '\'' +
                ", currMillis=" + currMillis +
                ", sentCurrMillis=" + sentCurrMillis +
                ", deliveredCurrMillis=" + deliveredCurrMillis +
                ", readCurrMillis=" + readCurrMillis +
                '}';
    }
}
