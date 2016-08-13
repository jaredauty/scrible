package com.jaredauty.scrible;

import android.content.res.Resources;
import android.graphics.Canvas;

import com.jaredauty.scrible.shapes.CurveShape;
import com.jaredauty.scrible.text.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Jared on 8/13/2016.
 */
public class Page {
    ArrayList<CurveShape> m_curves;
    Text m_text;
    ArrayList<String> m_verses;
    String m_bookTitle;
    int m_chapterNumber;
    private int mEdgeOffset;

    public Page(Resources resources) {
        mEdgeOffset = 10;
        m_text = new Text(resources, "");
        m_curves = new ArrayList<CurveShape>();
        m_bookTitle = new String("");
        m_verses = new ArrayList<String>();
    }

    public void draw(Canvas canvas){
        m_text.draw(canvas);
        for(CurveShape curve: m_curves) {
            curve.draw(canvas);
        }
    }

    public void setTextBounds(int left, int top, int right, int bottom) {
        m_text.setBounds(left+mEdgeOffset, top, right-mEdgeOffset, bottom);
    }

    public void addCurve(CurveShape curve) {
        m_curves.add(curve);
    }
    public void removeCurve(CurveShape curve) {
        m_curves.remove(curve);
    }

    public void cleanCurves() {
        m_curves = new ArrayList<CurveShape>();
    }

    public void cleanVerses() {
        m_verses.clear();
        refreshPageHtml();
    }

    public void addVerse(String verse) {
        m_verses.add(verse);
        refreshPageHtml();
    }

    public void setDebug(boolean debug){
        for(CurveShape curve: m_curves) {
            curve.setDebug(debug);
        }
        m_text.setDebug(debug);
    }

    public void setBookTitle(String bookTitle, int chapterNumber) {
        m_bookTitle = bookTitle;
        m_chapterNumber = chapterNumber;
        refreshPageHtml();
    }

    protected void refreshPageHtml() {
        String htmlText = new String("<h1>" + m_bookTitle + " " + Integer.toString(m_chapterNumber) + "</h1>");
        for (String verse: m_verses) {
            htmlText += "<sup>" + verse + "</sup>";
        }
        m_text.setHtmlText(htmlText);
    }
}
