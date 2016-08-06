package com.jaredauty.scrible.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jaredauty.scrible.bible.Bible;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jared on 30/07/2016.
 */
public class BibleDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "scrible.db";
    private static String DB_PATH = "/data/data/com.jaredauty.scrible/databases/";
    protected Context m_context;

    protected static final Pattern STEM_DISCARD_PATTERN = Pattern.compile("^\\w*$");

    public BibleDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.m_context = context;
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
        Cursor cursor = db.rawQuery(BibleContract.SQL_BOOK_NAMES_QUERY, new String [] {bibleName});

        if (cursor != null && cursor.moveToFirst()) {
            ArrayList<String> books =  new ArrayList<String>();
            int columnId = cursor.getColumnIndex(BibleContract.BookEntry.COLUMN_NAME_TITLE);
            do {
                String bookName = cursor.getString(columnId);
                books.add(bookName);
            } while (cursor.moveToNext());
            return books;
        }
        return new ArrayList<String>();
    }

    public ArrayList<Integer> getChapters(String bibleName, String bookName) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i("info", BibleContract.SQL_CHAPTERS_QUERY);
        Cursor cursor = db.rawQuery(
                BibleContract.SQL_CHAPTERS_QUERY,
                new String[]{bibleName, bookName}
        );
        ArrayList<Integer> chapters = new ArrayList<Integer>();
        if (cursor != null && cursor.moveToFirst()) {
            int columnId = cursor.getColumnIndex(BibleContract.ChapterEntry.COLUMN_NAME_CHAPTER_NUM);
            do {
                chapters.add(cursor.getInt(columnId));
            } while (cursor.moveToNext());
        }
        return chapters;
    }

    public ArrayList<Integer> getVerses(String bibleName, String bookName, int chapterNumber) {
        SQLiteDatabase db = getReadableDatabase();
        Log.i("info", BibleContract.SQL_VERSES_QUERY);
        Cursor cursor = db.rawQuery(
                BibleContract.SQL_VERSES_QUERY,
                new String[]{bibleName, bookName, Integer.toString(chapterNumber)}
        );
        ArrayList<Integer> chapters = new ArrayList<Integer>();
        if (cursor != null && cursor.moveToFirst()) {
            int columnId = cursor.getColumnIndex(BibleContract.VerseEntry.COLUMN_NAME_VERSE_NUM);
            do {
                chapters.add(cursor.getInt(columnId));
            } while (cursor.moveToNext());
        }
        return chapters;
    }

    public String verseLookup(String bible, String book, int chapter, int verse) {

        // Build selection query only once and then reuse.
        Log.i("info", BibleContract.SQL_VERSE_QUERY);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                BibleContract.SQL_VERSE_QUERY,
                new String[]{bible, book, Integer.toString(chapter), Integer.toString(verse)}
        );
        String verseText = new String();
        if (cursor != null && cursor.moveToFirst()) {
            int columnId = cursor.getColumnIndex(BibleContract.WordEntry.COLUMN_NAME_CHARS);
            do {
                verseText += cursor.getString(columnId);
            } while (cursor.moveToNext());
            return verseText;
        }
        return null;
    }

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

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){
            //database does't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        InputStream myInput = m_context.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
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
    public void updateWordStems() {
        SQLiteDatabase db = getWritableDatabase();
        // Get all words
        Cursor wordsCursor = db.query(
                BibleContract.WordEntry.TABLE_NAME,
                new String[]{BibleContract.WordEntry.COLUMN_NAME_CHARS, BibleContract.WordEntry._ID},
                null, null, null, null, null
        );
        if (wordsCursor != null && wordsCursor.moveToFirst()) {
            int wordIDColumn = wordsCursor.getColumnIndex(BibleContract.WordEntry._ID);
            int charColumn = wordsCursor.getColumnIndex(BibleContract.WordEntry.COLUMN_NAME_CHARS);
            int stemColumn = wordsCursor.getColumnIndex(BibleContract.WordEntry.COLUMN_NAME_WORDS_STEM_ID);

            do {
                // TODO find out whether the stem field has already been set.
                //if (wordsCursor.getLong(stemColumn))
                // Do the stemming
                String word = wordsCursor.getString(charColumn);

                String stem = getStem(word);
                if (stem != "") {
                    Log.i("info", "Using '" + stem + "' as stem for '" + word + "'");
                    // Insert stem into database if it's not already there.
                    long stemID = getStemID(stem);
                    if (stemID == -1){
                        // Insert in stem
                        ContentValues stems = new ContentValues();
                        stems.put(BibleContract.WordStemEntry.COLUMN_NAME_CHARS, stem);
                        stemID = db.insert(
                                BibleContract.WordStemEntry.TABLE_NAME,
                                null,
                                stems
                        );

                        // Update stem in word
                        long wordID = wordsCursor.getLong(wordIDColumn);
                        ContentValues words = new ContentValues();
                        words.put(BibleContract.WordEntry.COLUMN_NAME_WORDS_STEM_ID, stemID);
                        db.update(BibleContract.WordEntry.TABLE_NAME, words, "_id = ?", new String[]{Long.toString(wordID)});
                    }
                }
            } while (wordsCursor.moveToNext());
        }
    }

    protected long insertWord(String word, long verseID, int position) {
        SQLiteDatabase db = getWritableDatabase();
        // Insert word
        // Check if the word already exists, if it does just use the id.
        long wordID = getWordID(word);
        if(wordID == -1) {
            ContentValues words = new ContentValues();
            words.put(BibleContract.WordEntry.COLUMN_NAME_CHARS, word);
            //words.put(BibleContract.WordEntry.COLUMN_NAME_WORDS_STEM_ID, stemID);
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
        //
        return verse.split("((?<=[^\\w\\\'])|(?=[^\\w\\\']))");
    }

    protected String getStem(String word) {
        // Check that the word just contains letters, otherwise don't bother stemming.
        Matcher m = STEM_DISCARD_PATTERN.matcher(word);
        if(!m.find()){
            Log.i("info", "Skipping stemming for '" + word + "' since it contains un-stemmable characters.");
            return word;
        }
        EnglishAnalyzer en_an = new EnglishAnalyzer(Version.LUCENE_34);
        QueryParser parser = new QueryParser(Version.LUCENE_34, "", en_an);
        try{
            return parser.parse(word.toLowerCase()).toString();
        } catch (ParseException err) {
            Log.e("error", "Failed to stem '" + word + "' got: " + err.toString());
        }
        return word;
    }
}
