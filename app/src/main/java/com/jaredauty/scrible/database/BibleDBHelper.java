package com.jaredauty.scrible.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jaredauty.scrible.bible.Bible;
import com.jaredauty.scrible.database.BibleContract;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jared on 30/07/2016.
 */
public class BibleDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "scrible.db";

    public BibleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* TODO work out how to upgrade. */
        dropTables(db);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<String> getBookNames(String bibleName) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_TITLE + " FROM " +
                BibleContract.BookEntry.TABLE_NAME + " JOIN " + BibleContract.BibleEntry.TABLE_NAME +
                " ON " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_BIBLE_ID +
                " = " + BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry._ID +
                " WHERE " + BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry.COLUMN_NAME_TITLE +
                " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String [] {bibleName});

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

//    public String verseLookup(String bible, String book, int chapter, int verse) {
//        String selectQuery =
//    }

    public long getWordID(String word) {
        // Get the id of a particular word in the database if it exits.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                BibleContract.WordEntry.TABLE_NAME,
                new String[]{BibleContract.WordEntry._ID},
                BibleContract.WordEntry.COLUMN_NAME_CHARS + " = ?",
                new String[]{word},
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            int columnId = cursor.getColumnIndex(BibleContract.WordEntry._ID);
            do {
                return cursor.getLong(columnId);
            } while (cursor.moveToNext());
        }
        return -1;
    }

    public long getStemID(String stem) {
        // Get the id of a particular stem in the database if it exits.
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                BibleContract.WordStemEntry.TABLE_NAME,
                new String[]{BibleContract.WordStemEntry._ID},
                BibleContract.WordStemEntry.COLUMN_NAME_CHARS + " = ?",
                new String[]{stem},
                null,
                null,
                null
        );
        if (cursor != null && cursor.moveToFirst()) {
            int columnId = cursor.getColumnIndex(BibleContract.WordStemEntry._ID);
            do {
                return cursor.getLong(columnId);
            } while (cursor.moveToNext());
        }
        return -1;
    }

    public void createTables() {
        SQLiteDatabase db = getWritableDatabase();
        createTables(db);
    }

    public void createTables(SQLiteDatabase db) {
        db.execSQL(BibleContract.BibleEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.BookEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.ChapterEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.VerseEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.WordEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.VerseWordEntry.SQL_CREATE_ENTRIES);
        db.execSQL(BibleContract.WordStemEntry.SQL_CREATE_ENTRIES);
    }

    public void dropTables() {
        SQLiteDatabase db = getWritableDatabase();
        dropTables(db);
    }

    public void dropTables(SQLiteDatabase db) {
        db.execSQL(BibleContract.BibleEntry.SQL_DROP_TABLE);
        db.execSQL(BibleContract.BookEntry.SQL_DROP_TABLE);
        db.execSQL(BibleContract.ChapterEntry.SQL_DROP_TABLE);
        db.execSQL(BibleContract.VerseEntry.SQL_DROP_TABLE);
        db.execSQL(BibleContract.WordEntry.SQL_DROP_TABLE);
        db.execSQL(BibleContract.VerseWordEntry.SQL_DROP_TABLE);
        db.execSQL(BibleContract.WordStemEntry.SQL_DROP_TABLE);
    }

    public long insertBible(String bibleName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues bibles = new ContentValues();
        bibles.put(BibleContract.BibleEntry.COLUMN_NAME_TITLE, bibleName);
        return db.insert(
                BibleContract.BibleEntry.TABLE_NAME,
                null,
                bibles
        );
    }

    public long insertBook(String bookName, long bibleID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues books = new ContentValues();
        books.put(BibleContract.BookEntry.COLUMN_NAME_TITLE, bookName);
        books.put(BibleContract.BookEntry.COLUMN_NAME_BIBLE_ID, bibleID);
        return db.insert(
                BibleContract.BookEntry.TABLE_NAME,
                null,
                books
        );
    }

    public long insertChapter(int chapterNumber, long bookID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues chapters = new ContentValues();
        chapters.put(BibleContract.ChapterEntry.COLUMN_NAME_CHAPTER_NUM, chapterNumber);
        chapters.put(BibleContract.ChapterEntry.COLUMN_NAME_BOOK_ID, bookID);
        return db.insert(
                BibleContract.ChapterEntry.TABLE_NAME,
                null,
                chapters
        );
    }

    public long insertVerse(int verseNumber, String verseText, long chapterID) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues verses = new ContentValues();
        verses.put(BibleContract.VerseEntry.COLUMN_NAME_VERSE_NUM, verseNumber);
        verses.put(BibleContract.VerseEntry.COLUMN_NAME_CHAPTER_ID, chapterID);
        long verseID = db.insert(
                BibleContract.VerseEntry.TABLE_NAME,
                null,
                verses
        );
        String [] words = splitVerse(verseText);
        for(int i=0; i < words.length; i++) {
            insertWord(words[i], verseID, i);
        }
        return verseID;
    }

    protected long insertWord(String word, long verseID, int position) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues stems = new ContentValues();
        // Insert stem
        String stem = getStem(word);
        // Check if the stem already exists, if it does just use the id.
        long stemID = getStemID(stem);
        if(stemID == -1) {
            stems.put(BibleContract.WordStemEntry.COLUMN_NAME_CHARS, stem);
            stemID = db.insert(
                    BibleContract.WordStemEntry.TABLE_NAME,
                    null,
                    stems
            );
        }

        // Insert word
        // Check if the word already exists, if it does just use the id.
        long wordID = getWordID(word);
        if(wordID == -1) {
            ContentValues words = new ContentValues();
            words.put(BibleContract.WordEntry.COLUMN_NAME_CHARS, word);
            words.put(BibleContract.WordEntry.COLUMN_NAME_WORDS_STEM_ID, stemID);
            wordID = db.insert(
                    BibleContract.WordEntry.TABLE_NAME,
                    null,
                    words
            );
        }

        // Connect word and verse
        ContentValues verseWords = new ContentValues();
        verseWords.put(BibleContract.VerseWordEntry.COLUMN_NAME_VERSE_ID, verseID);
        verseWords.put(BibleContract.VerseWordEntry.COLUMN_NAME_WORD_ID, wordID);
        verseWords.put(BibleContract.VerseWordEntry.COLUMN_NAME_POSITION, position);
        long verseWordsID = db.insert(
                BibleContract.VerseWordEntry.TABLE_NAME,
                null,
                verseWords
        );
        return wordID;
    }

    protected String[] splitVerse(String verse) {
        // This regex will split the verse into a list of words, whitespace and punctuation.
        // We count single quotes as part of the word e.g. isn't or God's are both single words.
        // Hyphens are also counted as part of the word e.g. state-of-the-art is counted as one word.
        // TODO remove double hyphens as part of words e.g. -- is sometimes used between words.
        return verse.split("((?<=[^\\w\\-\\'])|(?=[^\\w\\-\\']))");
    }

    protected String getStem(String word) {
        // Todo build the stem
        return word.toLowerCase();
    }
}
