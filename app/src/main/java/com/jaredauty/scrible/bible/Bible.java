package com.jaredauty.scrible.bible;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.jaredauty.scrible.database.BibleDBHelper;
import com.jaredauty.scrible.database.BibleParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to handle interface to the bible.
 */
public class Bible {
    static final boolean BUILD_FROM_XML = false;
    public Bible(String translation, Context context) {
        m_translation = translation;
        initialiseDatabase(context);
    }

    public ArrayList<String> getBookNames() {
        return m_dbHelper.getBookNames(m_translation);
    }
    public String getVerse(String bookName, int chapterNumber, int verseNumber) {
        return m_dbHelper.verseLookup(m_translation, bookName, chapterNumber, verseNumber);
    }

    public void initialiseDatabase(Context context) {
        // TODO move this into the database helper
        m_dbHelper = new BibleDBHelper(context);
        if (BUILD_FROM_XML == true) {
            BibleParser parser = new BibleParser(m_dbHelper);
            AssetManager assetManager = context.getResources().getAssets();
            try {
                parser.parse(assetManager.open("ESV.xml"));
            } catch (XmlPullParserException err){
                Log.e("error", err.toString());
            } catch (IOException err){
                Log.e("error", err.toString());
            }
        } else {
            try {
                m_dbHelper.createDataBase();
            } catch (IOException err) {
                Log.e("error", err.toString());
            }
        }


    }

    private String m_translation;
    private BibleDBHelper m_dbHelper;
}
