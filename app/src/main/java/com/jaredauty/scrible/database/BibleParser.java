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
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readBible(parser);
        } finally {
            in.close();
        }
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
            Log.i("info", "Checking tag " + name);

            if (name.equals("b")) {
                // Found a book
                Log.i("info", "Found a book");
                String bookName = readBook(parser);
                Log.i("info", "Name is " + bookName);
                if (bookName != "") {
                    long bookID = m_dbHelper.insertBook(bookName, bibleID);
                }
            }
            else {
                skip(parser);
            }
        }
    }

    protected String readBook(XmlPullParser parser) throws XmlPullParserException, IOException {
        String bookName = new String("");
        parser.require(XmlPullParser.START_TAG, ns, "b");
        String tag = parser.getName();
        if (tag.equals("b")) {
            bookName = parser.getAttributeValue(null, "n");
            parser.nextTag();
        }
        //parser.require(XmlPullParser.END_TAG, ns, "b");
        return bookName;
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
