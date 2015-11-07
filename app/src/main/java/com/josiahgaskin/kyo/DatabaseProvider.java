package com.josiahgaskin.kyo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

/**
 * Helper for getting a database singleton
 */
public class DatabaseProvider extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseProvider instance;

    public static void init(Context context) {
        instance = new DatabaseProvider(context);
    }

    @Nullable
    public static SQLiteDatabase getDb() {
        if (instance == null) {
            return null;
        }
        return instance.getWritableDatabase();
    }

    private DatabaseProvider(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CounterDefs (" +
                "_id integer primary key," +
                "name text not null," +
                "type text not null" +
                "value integer not null default 0," +
                ")");
        db.execSQL("CREATE TABLE Counters (" +
                "_id integer primary key," +
                "name text not null," +
                "value integer," +
                "dt datetime default current_timestamp" +
                ")");
        db.execSQL("CREATE TABLE Ideas (" +
                "_id integer primary key," +
                "value text not null," +
                "dt datetime default current_timestamp" +
                "status text not null default \"brainstorm\"" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
