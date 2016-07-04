package com.jaredauty.scrible;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.util.Log;

import com.jaredauty.scrible.Curve;

/**
 * Created by Jared on 03/07/2016.
 */
public class MainSurface extends SurfaceView implements SurfaceHolder.Callback {
    private Paint paint;
    private SurfaceHolder holder;
    private Curve testCurve;

    public MainSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);

        testCurve = new Curve();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("info", "running onDraw");
        canvas.drawColor(Color.WHITE);
        testCurve.draw(canvas);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Canvas c = holder.lockCanvas();
        onDraw(c);
        holder.unlockCanvasAndPost(c);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

}
