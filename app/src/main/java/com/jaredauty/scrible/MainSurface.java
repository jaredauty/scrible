package com.jaredauty.scrible;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;

import com.jaredauty.scrible.shapes.CurveShape;
import com.jaredauty.scrible.shapes.GridShape;

/**
 * Created by Jared on 03/07/2016.
 */
public class MainSurface extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private CurveShape currentCurve;
    private ArrayList<CurveShape> curves;
    private boolean debug;
    private int mWidth;
    private int mHeight;
    private GridShape mBackgroundGrid;
    private Matrix mSceneMatrix;

    public MainSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSceneMatrix = new Matrix();
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        mBackgroundGrid = new GridShape(50, 200, 200);
        clean();
    }

    protected void repaint() {
        Canvas c = null;
        try {
            c = surfaceHolder.lockCanvas();
            drawBackground(c);
            // Draw curves
            for(CurveShape curve: curves)
            {
                curve.draw(c);
            }
        } finally {
            if (c != null) {
                surfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }


    public void surfaceCreated(SurfaceHolder holder) {
        repaint();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        mBackgroundGrid.setWidth(w);
        mBackgroundGrid.setHeight(h);
    }

//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        mWidth = w;
//        mHeight = h;
//        //mBackgroundGrid.setWidth(w);
//        //mBackgroundGrid.setHeight(h);
//        super.onSizeChanged(w, h, oldw, oldh);
//        repaint();
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentCurve = new CurveShape(debug);
                curves.add(currentCurve);
                currentCurve.touch_start(x, y);
                repaint();
                break;
            case MotionEvent.ACTION_MOVE:
                currentCurve.touch_move(x, y);
                repaint();
                break;
            case MotionEvent.ACTION_UP:
                currentCurve.touch_up();
                repaint();
                break;
        }
        return true;
    }

    public void clean() {
        curves = new ArrayList<CurveShape>();
    }

    public void toggleDebug() {
        debug = !debug;
        for(CurveShape curve: curves) {
            curve.setDebug(debug);
        }
    }
    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        if (debug) {
            mBackgroundGrid.draw(canvas);
        }
    }
}
