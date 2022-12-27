package com.dinis.whatsnext;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = MovieModelClass.class, version = 1, exportSchema = false)
public abstract class DB extends RoomDatabase{
    private static DB db;
    private static String dbName = "watchlist";

    public synchronized static DB getInstance(Context context){
        if (db == null){
            db = Room.databaseBuilder(context.getApplicationContext(), DB.class, dbName).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return db;
    }

    public abstract MainDAO dao();
}
