package com.jaredauty.scrible.database;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Object to handling parsing an xml file and outputting it to the database.
 * This shouldn't be run in the actual app but is useful for generating the
 * canned data for shipping with the app.
 */
public class BibleParser {

    public BibleParser(BibleDBHelper dbHelper) {
        m_dbHelper = dbHelper;
    }

    private static final String ns = null;

    public void parse(InputStream in) throws XmlPullParserException, IOException {
        // Clear the database
        // TODO this should only remove the current translation and accociated records
        // This should have to rebuild the tables from scratch
        m_dbHelper.dropTables();
        m_dbHelper.createTables();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readBible(parser);
        } finally {
            in.close();
        }
        m_dbHelper.updateWordStems();
    }

    protected  void readBible(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "bible");
        // TODO get bible name from xml
        long bibleID = m_dbHelper.insertBible("ESV");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("b")) {
                // Found a book
                readBook(parser, bibleID);
            }
            else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "bible");
    }

    protected void readBook(XmlPullParser parser, long bibleID) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "b");
        String bookName = parser.getAttributeValue(null, "n");
        Log.i("info", "Found book "+ bookName);
        long bookID = m_dbHelper.insertBook(bookName, bibleID);
        // Search for chapters
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("c")) {
                // Found a chapter
                readChapter(parser, bookID);
            }
            else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "b");
    }
    protected void readChapter(XmlPullParser parser, long bookID) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "c");
        String chapterNumber = parser.getAttributeValue(null, "n");
        Log.i("info", "Found chapter "+ chapterNumber);
        long chapterID = m_dbHelper.insertChapter(Integer.parseInt(chapterNumber), bookID);
        // Search for chapters
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("v")) {
                // Found a verse
                readVerse(parser, chapterID);
            }
            else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "c");
    }

    protected void readVerse(XmlPullParser parser, long chapterID) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "v");
        String verseNumber = parser.getAttributeValue(null, "n");
        Log.i("info", "Found verse "+ verseNumber);
        parser.next();
        parser.require(XmlPullParser.TEXT, ns, null);
        String verseText = parser.getText();
        long verseID = m_dbHelper.insertVerse(Integer.parseInt(verseNumber), verseText, chapterID);
        parser.next();
        parser.require(XmlPullParser.END_TAG, ns, "v");
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    protected BibleDBHelper m_dbHelper;
}
