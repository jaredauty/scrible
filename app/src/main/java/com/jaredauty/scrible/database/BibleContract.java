package com.jaredauty.scrible.database;

import android.provider.BaseColumns;

/**
 * Created by Jared on 30/07/2016.
 */
public final class BibleContract {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    private static final String COMMA_SEP = ",";
    private static final String COMMAND_SEP = ";";

    // Stop people from instantiating the class
    public BibleContract() {}

    /* Table for Bibles (different versions) */
    // TODO need to specify foreign keys in all these definitions
    public static abstract class BibleEntry implements BaseColumns {
        public static final String TABLE_NAME = "bibles";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY," +
                        COLUMN_NAME_TITLE + TEXT_TYPE +
                        " )";
        public  static final String SQL_DROP_TABLE = DROP_TABLE + TABLE_NAME;
    }
    /* Table for books (Genesis, leviticus etc) */
    public static abstract class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_BIBLE_ID = "bibleId";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY," +
                        COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BIBLE_ID + INT_TYPE +
                        " )";
        public  static final String SQL_DROP_TABLE = DROP_TABLE + TABLE_NAME;
    }
    /* Table for chapters */
    public static abstract class ChapterEntry implements BaseColumns {
        public static final String TABLE_NAME = "chapters";
        public static final String COLUMN_NAME_CHAPTER_NUM = "chapterNumber";
        public static final String COLUMN_NAME_BOOK_ID = "bookId";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY," +
                        COLUMN_NAME_CHAPTER_NUM + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BOOK_ID + INT_TYPE +
                        " )";
        public  static final String SQL_DROP_TABLE = DROP_TABLE + TABLE_NAME;
    }
    /* Table for verses */
    public static abstract class VerseEntry implements BaseColumns {
        public static final String TABLE_NAME = "verses";
        public static final String COLUMN_NAME_VERSE_NUM = "verseNum";
        public static final String COLUMN_NAME_CHAPTER_ID = "chapterId";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY," +
                        COLUMN_NAME_VERSE_NUM + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CHAPTER_ID + INT_TYPE +
                        " )";
        public  static final String SQL_DROP_TABLE = DROP_TABLE + TABLE_NAME;
    }
    /* Table for the stem of words.*/
    public static abstract class WordStemEntry implements BaseColumns {
        public static final String TABLE_NAME = "wordStems";
        public static final String COLUMN_NAME_CHARS = "chars";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY," +
                        COLUMN_NAME_CHARS + TEXT_TYPE +
                        " )";
        public  static final String SQL_DROP_TABLE = DROP_TABLE + TABLE_NAME;
    }
    /* Table for words. Note that this can also include punctuation and whitespace */
    public static abstract class WordEntry implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String COLUMN_NAME_CHARS = "chars";
        public static final String COLUMN_NAME_WORDS_STEM_ID = "wordStemId";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY," +
                        COLUMN_NAME_CHARS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_WORDS_STEM_ID + INT_TYPE +
                        " )";
        public  static final String SQL_DROP_TABLE = DROP_TABLE + TABLE_NAME;
    }

    /* Table for mapping between verses and words */
    public static abstract class VerseWordEntry implements BaseColumns {
        public static final String TABLE_NAME = "versesWords";
        public static final String COLUMN_NAME_VERSE_ID = "verseId";
        public static final String COLUMN_NAME_WORD_ID = "wordId";
        public static final String COLUMN_NAME_POSITION = "position";
        public static final String SQL_CREATE_ENTRIES =
                CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + INT_TYPE + " PRIMARY KEY," +
                        COLUMN_NAME_VERSE_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_WORD_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_POSITION + INT_TYPE +
                        " )";
        public  static final String SQL_DROP_TABLE = DROP_TABLE + TABLE_NAME;
    }
    /*
       Example:
       SELECT words.chars
       FROM verses
       JOIN chapters
       ON verses.chapterID = chapters._ID
       JOIN books
       ON chapters.bookID = books._ID
       JOIN bibles
       ON books.bibleID = bibles._ID
       JOIN versesWords
       ON verses._ID = versesWords.verseID
       JOIN words
       ON versesWords.wordID = words._ID
       WHERE bibles.title = "ESV" AND books.title = "Genesis" AND chapters.chapterNumber = 2 AND verses.verseNum = 1
       ORDER BY versesWords.position
   */
    public static final String SQL_VERSE_QUERY = "SELECT " + BibleContract.WordEntry.TABLE_NAME + "." + BibleContract.WordEntry.COLUMN_NAME_CHARS +
            " FROM " + BibleContract.VerseEntry.TABLE_NAME +
            " JOIN " + BibleContract.ChapterEntry.TABLE_NAME +
            " ON " + BibleContract.VerseEntry.TABLE_NAME + "." + BibleContract.VerseEntry.COLUMN_NAME_CHAPTER_ID + " = " +
            BibleContract.ChapterEntry.TABLE_NAME + "." + BibleContract.ChapterEntry._ID +
            " JOIN " + BibleContract.BookEntry.TABLE_NAME +
            " ON " + BibleContract.ChapterEntry.TABLE_NAME + "." + BibleContract.ChapterEntry.COLUMN_NAME_BOOK_ID + " = " +
            BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry._ID +
            " JOIN " + BibleContract.BibleEntry.TABLE_NAME +
            " ON " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_BIBLE_ID + " = " +
            BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry._ID +
            " JOIN " + BibleContract.VerseWordEntry.TABLE_NAME +
            " ON " + BibleContract.VerseEntry.TABLE_NAME + "." + BibleContract.VerseEntry._ID + " = " +
            BibleContract.VerseWordEntry.TABLE_NAME + "." + BibleContract.VerseWordEntry.COLUMN_NAME_VERSE_ID +
            " JOIN " + BibleContract.WordEntry.TABLE_NAME +
            " ON " + BibleContract.VerseWordEntry.TABLE_NAME + "." + BibleContract.VerseWordEntry.COLUMN_NAME_WORD_ID + " = " +
            BibleContract.WordEntry.TABLE_NAME + "." + BibleContract.WordEntry._ID +
            " WHERE " + BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry.COLUMN_NAME_TITLE + " = ?" +
            " AND " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_TITLE + " = ?" +
            " AND " + BibleContract.ChapterEntry.TABLE_NAME + "." + BibleContract.ChapterEntry.COLUMN_NAME_CHAPTER_NUM + " = ?" +
            " AND " + BibleContract.VerseEntry.TABLE_NAME + "." + BibleContract.VerseEntry.COLUMN_NAME_VERSE_NUM + " = ?" +
            " ORDER BY " + BibleContract.VerseWordEntry.TABLE_NAME + "." + BibleContract.VerseWordEntry.COLUMN_NAME_POSITION;

    /*
       Example:
       SELECT books.title
       FROM books
       JOIN bibles
       ON books.bibleID = bibles._ID
       WHERE bibles.title = "ESV"
   */
    public static final String SQL_BOOK_NAMES_QUERY = "SELECT " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_TITLE +
            " FROM " + BibleContract.BookEntry.TABLE_NAME +
            " JOIN " + BibleContract.BibleEntry.TABLE_NAME +
            " ON " + BibleContract.BookEntry.TABLE_NAME + "." + BibleContract.BookEntry.COLUMN_NAME_BIBLE_ID +
            " = " + BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry._ID +
            " WHERE " + BibleContract.BibleEntry.TABLE_NAME + "." + BibleContract.BibleEntry.COLUMN_NAME_TITLE +
            " = ?";
}
