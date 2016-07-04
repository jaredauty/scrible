package com.jaredauty.scrible;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;


/**
 * Created by Jared on 04/07/2016.
 * Class for handling curves created by the user.
 */
public class Curve extends Drawable {

    protected Paint paint;
    private Path path;

    public Curve() {
        super();
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10.0f);

        path = new Path();
    }
    public void setAlpha(int i) {;}
    public int getAlpha() {return 255;}
    public int getOpacity() {return 255;};
    public void setColorFilter(ColorFilter colorFilter) {;}

    public void draw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    public void touch_start(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        mX = x;
        mY = y;
    }
    public void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }
    public void touch_up() {
        path.lineTo(mX, mY);
    }
}
