package com.jaredauty.scrible.gestures;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.jaredauty.scrible.MainSurface;

/**
 * Based on:
 * http://stackoverflow.com/questions/5790503/can-we-use-scale-gesture-detector-for-pinch-zoom-in-android
 */
public class ScaleGestures implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
    private MainSurface view;
    private ScaleGestureDetector gestureScale;
    private float mScaleFactor = 1;
    private boolean inScale;
    private Matrix mMatrix;
    private PointF lastFocus = new PointF();
    private float lastSpan = 1;

    public ScaleGestures(Context c){
        mMatrix = new Matrix();
        gestureScale = new ScaleGestureDetector(c, this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        this.view = (MainSurface) view;
        gestureScale.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {

        lastSpan = scaleGestureDetector.getCurrentSpan();
        lastFocus = new PointF(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        Log.i("info", "ScaleBegin, focus: " + lastFocus.toString());
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleOffset = mScaleFactor * detector.getCurrentSpan();
        PointF translatOffset = new PointF(detector.getFocusX() - lastFocus.x, detector.getFocusY() - lastFocus.y);
        Matrix matrix = new Matrix();

        matrix.postTranslate(translatOffset.x, translatOffset.y);

        // Scale
        mScaleFactor *= detector.getScaleFactor();
        matrix.postScale(scaleOffset, scaleOffset, detector.getFocusX(), detector.getFocusY());

//        // Scale
//        mScaleFactor *= detector.getScaleFactor();
////        if (mCurrentPoint1 != mCurrentPoint2 && mPreviousPoint1 != mPreviousPoint2) {
////            scaleValue = (
////                    ((float) Math.pow(mCurrentPoint1.x - mCurrentPoint2.x, 2) + (float) Math.pow(mCurrentPoint1.y - mCurrentPoint2.y, 2)) /
////                            ((float) Math.pow(mPreviousPoint1.x - mPreviousPoint2.x, 2) + (float) Math.pow(mPreviousPoint1.y - mPreviousPoint2.y, 2))
////            );
////        }
//        PointF centerPoint = new PointF((focusX - startFocus.x) + (gestureScale.getCurrentSpan() / 2.0f), (focusY - startFocus.y) + (gestureScale.getCurrentSpan() / 2.0f));
//        matrix.postScale(mScaleFactor, mScaleFactor, focusX - startFocus.x, focusY - startFocus.y);

        view.setViewportMatrix(matrix);
        return true;
    }
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) { inScale = false; }

}