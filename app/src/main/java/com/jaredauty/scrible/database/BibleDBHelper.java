package com.jaredauty.scrible.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jaredauty.scrible.database.BibleContract;

/**
 * Created by Jared on 30/07/2016.
 */
public class BibleDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "scrible.db";

    public BibleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BibleContract.BibleEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.BookEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.ChapterEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.VerseEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.WordEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.VerseWordEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.WordStemEntry.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* TODO work out how to upgrade. */
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
