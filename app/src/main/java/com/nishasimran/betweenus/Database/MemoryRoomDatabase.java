package com.nishasimran.betweenus.Database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nishasimran.betweenus.DAOs.AlbumDao;
import com.nishasimran.betweenus.DAOs.MemoryDao;
import com.nishasimran.betweenus.DAOs.SpecialDayDao;
import com.nishasimran.betweenus.DataClasses.Album;
import com.nishasimran.betweenus.DataClasses.Memory;
import com.nishasimran.betweenus.DataClasses.SpecialDay;
import com.nishasimran.betweenus.Strings.DatabaseStrings;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities =  {Memory.class, Album.class, SpecialDay.class}, version = 1, exportSchema = false)
public abstract class MemoryRoomDatabase extends RoomDatabase {

    public abstract MemoryDao memoryDao();
    public abstract AlbumDao albumDao();
    public abstract SpecialDayDao specialDayDao();

    private static volatile MemoryRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MemoryRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MemoryRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MemoryRoomDatabase.class,
                            DatabaseStrings.DATABASE_MEMORIES
                    )
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                MemoryDao dao = INSTANCE.memoryDao();
                AlbumDao albumDao = INSTANCE.albumDao();
                SpecialDayDao specialDayDao = INSTANCE.specialDayDao();
            });
        }
    };
}
