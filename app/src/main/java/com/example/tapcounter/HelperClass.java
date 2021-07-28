package com.example.tapcounter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class HelperClass extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "counter.db";
    public static final int DATABASE_VERSION = 1;

    public HelperClass(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE_QUERY = "CREATE TABLE " +
                CollectionClass.CollectionInnerClass.TABLE_NAME + "(" +
                CollectionClass.CollectionInnerClass._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CollectionClass.CollectionInnerClass.COLUMN_NAME + " TEXT NOT NULL" + ");";

        db.execSQL(SQL_CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CollectionClass.CollectionInnerClass.TABLE_NAME);
        onCreate(db);
    }
}
