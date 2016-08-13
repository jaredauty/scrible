package com.jaredauty.scrible.bible;

/**
 * Created by Jared on 8/13/2016.
 */
public class BibleReference {
    private String m_bookTitle;
    private int m_chapterNum;
    private int m_verseNum;
    public BibleReference(String bookTile, int chapterNumber, int verseNumber) {
        m_bookTitle = bookTile;
        m_chapterNum = chapterNumber;
        m_verseNum = verseNumber;
    }

    public String getBookTitle() {
        return m_bookTitle;
    }
    public int getChapterNum() {
        return m_chapterNum;
    }
    public int getVerseNum() {
        return m_verseNum;
    }
}
