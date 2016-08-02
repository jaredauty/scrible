package com.jaredauty.scrible.bible;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jaredauty.scrible.database.BibleContract;
import com.jaredauty.scrible.database.BibleDBHelper;

import java.nio.CharBuffer;
import java.util.ArrayList;

/**
 * Class to handle interface to the bible.
 */
public class Bible {
    public Bible(String translation, Context context) {
        m_traslation = translation;
        m_dbHelper = new BibleDBHelper(context);
        // TODO move this to separate thread
        populateDB();
    }

    public ArrayList<String> getBookNames() {
        SQLiteDatabase db = m_dbHelper.getReadableDatabase();
        String selectQuery = "SELECT " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_TITLE + " FROM " +
                BibleContract.BookEntry.TABLE_NAME + " JOIN " + BibleContract.BibleEntry.TABLE_NAME +
                " ON " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_BIBLE_ID +
                " = " + BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry._ID +
                " WHERE " + BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry.COLUMN_NAME_TITLE +
                " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String [] {m_traslation});

        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<String> books =  new ArrayList<String>();
            int columId = cursor.getColumnIndex(BibleContract.BookEntry.COLUMN_NAME_TITLE);
            do {
                String bookName = cursor.getString(columId);
                books.add(bookName);
            } while (cursor.moveToNext());
            return books;
        }
        return new ArrayList<String>();
    }

    private String m_traslation;
    private BibleDBHelper m_dbHelper;

    private void populateDB() {
        // TODO make sure this doesn't create duplicate entries.
        SQLiteDatabase db = m_dbHelper.getWritableDatabase();
        // Bibles
        ContentValues bibles = new ContentValues();
        bibles.put(BibleContract.BibleEntry.COLUMN_NAME_TITLE, "ESV");
        long bibleId = db.insert(
                BibleContract.BibleEntry.TABLE_NAME,
                null,
                bibles
        );
        // Books
        for(String bookName: new String [] {"Genesis", "Exodus"}) {
            ContentValues books = new ContentValues();
            books.put(BibleContract.BookEntry.COLUMN_NAME_TITLE, bookName);
            books.put(BibleContract.BookEntry.COLUMN_NAME_BIBLE_ID, bibleId);
            long bookId = db.insert(
                    BibleContract.BookEntry.TABLE_NAME,
                    null,
                    books
            );
        }
    }
}
