package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Values.DatabaseValues;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = DatabaseValues.TABLE_USERS)
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DatabaseValues.COLUMN_ID)
    private String id;
    @ColumnInfo(name = DatabaseValues.COLUMN_NAME)
    private String name;
    @ColumnInfo(name = DatabaseValues.COLUMN_DOB)
    private long dob;
    @ColumnInfo(name = DatabaseValues.COLUMN_EMAIL)
    private String email;
    @ColumnInfo(name = DatabaseValues.COLUMN_PHONE)
    private String phone;
    @ColumnInfo(name = DatabaseValues.COLUMN_PHOTO)
    private String photo;



    @Ignore
    public User() {
        // required no argument constructor
    }



    public User(@NonNull String id, String name, long dob, String email, String phone, String photo) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
    }



    // getters
    public @NotNull String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public long getDob() {
        return dob;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public String getPhoto() {
        return photo;
    }

    // setters

    public void setId(@NotNull String id) {
        this.id = id;
    }
    public void setDob(long dob) {
        this.dob = dob;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    @NotNull
    public String toString() {
        return "User{ " +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dob=" + dob +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                " }";
    }
}
