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
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Text extends Drawable {
    private static final int DEFAULT_COLOR = Color.BLACK;
    private static final int DEFAULT_TEXTSIZE = 17;
    private TextPaint mPaint;
    private Spanned mTextHTML;
    private int mIntrinsicWidth;
    private int mIntrinsicHeight;
    private Paint mDebugPaint;
    private boolean mDebug;
    private int mWidth;
    private DynamicLayout mLayout;
    public Text(Resources res, String text) {
        mTextHTML = Html.fromHtml(text, null, null);
        mWidth = 0;
        mPaint = new TextPaint();
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setTextAlign(Align.LEFT);
        mPaint.setAntiAlias(true);
        float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                DEFAULT_TEXTSIZE, res.getDisplayMetrics());
        mPaint.setTextSize(textSize);

        // TODO This needs to be updated to use correct spanned data.
        mIntrinsicWidth = (int) (mPaint.measureText(text, 0, text.length()) + .5);
        mIntrinsicHeight = mPaint.getFontMetricsInt(null);

        updateLayout();

        mDebug = false;
        mDebugPaint = new Paint();
        mDebugPaint.setColor(Color.MAGENTA);
        mDebugPaint.setAntiAlias(true);
        mDebugPaint.setStrokeWidth(1.5f);
        mDebugPaint.setStyle(Paint.Style.STROKE);
    }

    public void setHtmlText(String htmlText) {
        mTextHTML = Html.fromHtml(htmlText, null, null);
        updateLayout();
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mWidth = right - left;
        updateLayout();
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        mLayout.draw(canvas);
        if(mDebug) {
            for (Rect bound: getWordBounds()) {
                canvas.drawRect(bound, mDebugPaint);
            }
        }
        canvas.restore();
        if (mDebug) {
            canvas.drawRect(bounds, mDebugPaint);
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
            wordBoundaries.add(bounds);
        }
        return wordBoundaries;
    }

    protected void updateLayout() {
        mLayout = new DynamicLayout(
                mTextHTML, mPaint,
                mWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0.0f,
                true
        );
    }
}