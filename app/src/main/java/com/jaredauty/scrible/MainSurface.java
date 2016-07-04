package com.jaredauty.scrible;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;

import com.jaredauty.scrible.Curve;

/**
 * Created by Jared on 03/07/2016.
 */
public class MainSurface extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private Curve testCurve;

    public MainSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        testCurve = new Curve();
    }

    protected void repaint() {
        Canvas c = null;
        try {
            c = surfaceHolder.lockCanvas();
            c.drawColor(Color.WHITE);
            testCurve.draw(c);
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                testCurve.touch_start(x, y);
                repaint();
                break;
            case MotionEvent.ACTION_MOVE:
                testCurve.touch_move(x, y);
                repaint();
                break;
            case MotionEvent.ACTION_UP:
                testCurve.touch_up();
                repaint();
                break;
        }
        return true;
    }

}
