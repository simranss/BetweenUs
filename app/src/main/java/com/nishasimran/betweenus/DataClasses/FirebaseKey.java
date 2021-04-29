package com.nishasimran.betweenus.DataClasses;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class FirebaseKey {

    private String id;
    private String myPublic;
    private long currMillis;


    public FirebaseKey() {
        // required no argument constructor
    }


    public FirebaseKey(@NonNull String id, String myPublic, long currMillis) {
        this.id = id;
        this.myPublic = myPublic;
        this.currMillis = currMillis;
    }


    // getters
    @NonNull
    public String getId() {
        return id;
    }
    public long getCurrMillis() {
        return currMillis;
    }
    public String getMyPublic() {
        return myPublic;
    }

    // setters
    public void setCurrMillis(long currMillis) {
        this.currMillis = currMillis;
    }
    public void setMyPublic(String myPublic) {
        this.myPublic = myPublic;
    }

    @Override
    @NotNull
    public String toString() {
        return "FirebaseKey{ " +
                "id='" + id + '\'' +
                ", myPublic='" + myPublic + '\'' +
                ", currMillis=" + currMillis +
                " }";
    }
}
