/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaredauty.scrible.text;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Text extends Drawable {
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXTSIZE = 15;
    private TextPaint mPaint;
    private String mText;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private Paint mDebugPaint;
    private boolean mDebug;
    private int mWidth;
    private StaticLayout mLayout;
    private ArrayList<String> mWords;
    public Text(Resources res, String text) {
        mText = text;
        mWidth = 0;
        mPaint = new TextPaint();
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setTextAlign(Align.LEFT);
        mPaint.setAntiAlias(true);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                DEFAULT_TEXTSIZE, res.getDisplayMetrics());
        mPaint.setTextSize(textSize);

        mIntrinsicWidth = (int) (mPaint.measureText(mText, 0, mText.length()) + .5);
        mIntrinsicHeight = mPaint.getFontMetricsInt(null);

        mLayout = new StaticLayout(
                mText, mPaint,
                mWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0.0f,
                false
        );

        mDebug = false;
        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.MAGENTA);
        mDebugPaint.setAntiAlias(true);
        mDebugPaint.setStrokeWidth(1.5f);
        mDebugPaint.setStyle(Paint.Style.STROKE);

        // To start off with lets just parse the text to find all the words.
        // This will be replaced when we build words from the database.
        String strings[] = mText.split(" ");
        mWords = new ArrayList<String>();
        for(String string: strings) {
            mWords.add(string);
        }

    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mLayout = new StaticLayout(
                mText, mPaint,
                right - left,
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0.0f,
                false
        );
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        canvas.restore();
        mLayout.draw(canvas);

        if(mDebug) {
            canvas.drawRect(bounds, mDebugPaint);
            for (Rect bound: getWordBounds()) {
                canvas.drawRect(bound, mDebugPaint);
            }
        }

    }
    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }
    @Override
    public int getIntrinsicWidth() {
        return mIntrinsicWidth;
    }
    @Override
    public int getIntrinsicHeight() {
        return mIntrinsicHeight;
    }
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }
    @Override
    public void setColorFilter(ColorFilter filter) {
        mPaint.setColorFilter(filter);
    }
    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    protected ArrayList<Rect> getWordBounds() {
        ArrayList<Rect> wordBoundaries = new ArrayList<Rect>();
        for (int i = 0; i < mLayout.getLineCount(); i++) {
            Rect bounds = new Rect();
            mLayout.getLineBounds(i, bounds);
            //wordBoundaries.add(bounds);
        }
        return wordBoundaries;
    }
}