package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.nishasimran.betweenus.Strings.DatabaseStrings;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = DatabaseStrings.TABLE_USERS)
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DatabaseStrings.COLUMN_ID)
    private final String id;
    @ColumnInfo(name = DatabaseStrings.COLUMN_NAME)
    private String name;
    @ColumnInfo(name = DatabaseStrings.COLUMN_DOB)
    private final long dob;
    @ColumnInfo(name = DatabaseStrings.COLUMN_EMAIL)
    private String email;
    @ColumnInfo(name = DatabaseStrings.COLUMN_PHONE)
    private String phone;
    @ColumnInfo(name = DatabaseStrings.COLUMN_ZODIAC)
    private final String zodiac;
    @ColumnInfo(name = DatabaseStrings.COLUMN_PHOTO)
    private String photo;



    public User(@NonNull String id, String name, long dob, String email, String phone, String zodiac, String photo) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
        this.zodiac = zodiac;
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
    public String getZodiac() {
        return zodiac;
    }
    public String getPhoto() {
        return photo;
    }

    // setters
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
}
