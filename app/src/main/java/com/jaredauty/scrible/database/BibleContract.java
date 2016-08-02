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
    }
}
