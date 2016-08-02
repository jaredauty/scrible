package com.jaredauty.scrible.bible;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jaredauty.scrible.database.BibleContract;
import com.jaredauty.scrible.database.BibleDBHelper;
import com.jaredauty.scrible.database.BibleParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to handle interface to the bible.
 */
public class Bible {
    public Bible(String translation, Context context) throws XmlPullParserException, IOException {
        m_translation = translation;
        m_dbHelper = new BibleDBHelper(context);

        BibleParser parser = new BibleParser(m_dbHelper);
        AssetManager assetManager = context.getResources().getAssets();
        parser.parse(assetManager.open("ESV.xml"));
    }

    public ArrayList<String> getBookNames() {
        return m_dbHelper.getBookNames(m_translation);
    }

    private String m_translation;
    private BibleDBHelper m_dbHelper;
}
