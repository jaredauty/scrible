package com.jaredauty.scrible;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by Jared on 03/07/2016.
 */
public class MainSurface extends SurfaceView implements SurfaceHolder.Callback {
    private Paint paint;
    private SurfaceHolder holder;

    public MainSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);

        paint = new Paint();
        paint.setARGB(255, 0, 0, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("info", "running onDraw");
        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(50.0f, 50.0f, 50.0f, paint);
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
