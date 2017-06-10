package com.jaredauty.scrible;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredauty.scrible.shapes.CurveShape;
import com.jaredauty.scrible.text.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jared on 8/13/2016.
 */
public class Page {
    ArrayList<CurveShape> m_curves;
    Text m_text;
    TextView m_testText;
    ArrayList<String> m_verses;
    String m_bookTitle;
    int m_chapterNumber;
    private int mEdgeOffset;
    private boolean mHtmlDirty;
    private LinearLayout m_layout;

    public Page(Context context) {
        mEdgeOffset = 70;
        m_text = new Text(context.getResources(), "");
        m_curves = new ArrayList<CurveShape>();
        m_bookTitle = new String("");
        m_verses = new ArrayList<String>();
        mHtmlDirty = true;
        m_layout = new LinearLayout(context);

        m_testText = new TextView(context);
        m_testText.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        m_layout.addView(m_testText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(mEdgeOffset, 0, mEdgeOffset, 0);
        m_testText.setLayoutParams(params);

    }

    public void draw(Canvas canvas){
        if(mHtmlDirty){
            refreshPageHtml();
        }
        m_text.draw(canvas);
        //m_testText.draw(canvas);
        for(CurveShape curve: m_curves) {
            curve.draw(canvas);
        }
    }

    public void setTextBounds(int left, int top, int right, int bottom) {
        m_text.setBounds(left+mEdgeOffset, top, right-mEdgeOffset, bottom);
        //m_testText.setWidth((right - left) - (2 * mEdgeOffset));
       // m_testText.setX(left + mEdgeOffset);
        //m_testText.setY(top);
        m_testText.setLeft(left + mEdgeOffset);
        m_testText.setRight(right - mEdgeOffset);
        m_testText.setTop(top);
        m_testText.setBottom(bottom);
        //m_layout.setMinimumWidth((right - left) - (2 * mEdgeOffset));
        //m_testText.setWidth((right - left) - (2 * mEdgeOffset));
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
        mHtmlDirty = true;
    }

    public void addVerses(ArrayList<String> verses) {
        Log.i("info", "Updating verses with " + verses.toString());
        m_verses.addAll(verses);
        mHtmlDirty = true;
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
        mHtmlDirty = true;
    }

    protected void refreshPageHtml() {
        String htmlText = new String("<html><h1>" + m_bookTitle + " " + Integer.toString(m_chapterNumber) + "</h1>");
        int verseNumber = 1;
        //htmlText += "<body align=\"justify\">";
        for (String verse: m_verses) {
            htmlText += "<sup><small><small>" + verseNumber + "</small></small></sup>";
            htmlText += verse + " ";
            verseNumber++;
        }
        //htmlText += "</body></html>";
        m_text.setHtmlText(htmlText);
        m_testText.setText(Html.fromHtml(htmlText));
        // Nasty hack to clear the view. From android developer website.
        // https://developer.android.com/reference/android/webkit/WebView.html#clearView()
        //m_testText.loadUrl("about:blank");
        //m_testText.loadDataWithBaseURL("same://ur/l/tat/does/not/work", "data", "text/html", "utf-8", null);
        //m_testText.loadData(htmlText, "text/html", "utf-8");
        mHtmlDirty = false;
    }
}
