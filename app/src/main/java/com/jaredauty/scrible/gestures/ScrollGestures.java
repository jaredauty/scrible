package com.jaredauty.scrible.gestures;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.jaredauty.scrible.MainSurface;

/**
 * Created by Jared on 8/14/2016.
 */
public class ScrollGestures extends GestureDetector.SimpleOnGestureListener implements  View.OnTouchListener {
    private GestureDetector mGestures;
    private MainSurface view;
    private PointF mCurrentScroll;

    public ScrollGestures(Context c){
        mGestures = new GestureDetector(c, this);
        mCurrentScroll = new PointF();
    }
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        this.view = (MainSurface) view;
        mGestures.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Matrix matrix = new Matrix();
        mCurrentScroll.offset(distanceX, distanceY);
        matrix.postTranslate(-1 * mCurrentScroll.x, -1 * mCurrentScroll.y);
        view.setViewportMatrix(matrix);
        return true;
    }
}
