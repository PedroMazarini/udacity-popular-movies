package com.movix.movix.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.movix.movix.BuildConfig;
import com.movix.movix.local.Dao.MovieDao;


@Database(entities = {MovieLocal.class},
        version = BuildConfig.VERSION_CODE, exportSchema = false)
public abstract class MovixDatabase extends RoomDatabase {

    private static final String DB_NAME = "movixDatabase.db";
    private static volatile MovixDatabase instance;

    public static synchronized MovixDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static MovixDatabase create(final Context context) {

        return Room.databaseBuilder(
                context,
                MovixDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract MovieDao getMovieDao();
}