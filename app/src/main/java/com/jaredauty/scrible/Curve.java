package com.jaredauty.scrible;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.jaredauty.scrible.curve.CurveResampler;

import java.util.ArrayList;


/**
 * Created by Jared on 04/07/2016.
 * Class for handling curves created by the user.
 */
public class Curve extends Drawable {

    protected Paint curvePaint;
    private Paint debugDotPaint;
    private Path path;
    private boolean debug;
    private ArrayList<PointF> points;

    public Curve() {
        super();
        // Define paint for curve
        curvePaint = new Paint();
        curvePaint.setColor(Color.BLUE);
        curvePaint.setAntiAlias(true);
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeWidth(5.0f);

        // Define paint for debug dots
        debugDotPaint = new Paint();
        debugDotPaint.setColor(Color.RED);
        debugDotPaint.setAntiAlias(true);

        path = new Path();
        debug = true;
        points = new ArrayList<PointF>();
    }
    public void setAlpha(int i) {;}
    public int getAlpha() {return 255;}
    public int getOpacity() {return 255;};
    public void setColorFilter(ColorFilter colorFilter) {;}

    public void draw(Canvas canvas) {
        canvas.drawPath(path, curvePaint);
        if(debug) {
            // Draw dots for all control points.
            for(PointF point: points) {
                canvas.drawCircle(point.x, point.y, 10.0f, debugDotPaint);
            }
        }
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
            points.add(new PointF(x, y));
        }
    }
    public void touch_up() {
        path.lineTo(mX, mY);
        rebuildCurve();
    }

    // Rebuild the curve to reduce the amount of points required.
    protected void rebuildCurve() {
        CurveResampler resampler = new CurveResampler(points);
        points = resampler.getOptimisedPoints(150.0f);
    }
}
