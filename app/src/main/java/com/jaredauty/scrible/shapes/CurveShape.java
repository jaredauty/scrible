package com.jaredauty.scrible.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.jaredauty.scrible.curve.CurveResampler;

import java.util.ArrayList;


/**
 * Created by Jared on 04/07/2016.
 * Class for handling curves created by the user.
 */
public class CurveShape extends Drawable {

    protected Paint curvePaint;
    private Paint debugDotPaint;
    private Path path;
    private boolean mDebug;
    private ArrayList<PointF> points;

    public CurveShape(boolean debug) {
        this();
        mDebug = debug;
    }

    public CurveShape() {
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
        mDebug = false;
        points = new ArrayList<PointF>();
    }
    public void setAlpha(int i) {;}
    public int getAlpha() {return 255;}
    public int getOpacity() {return 255;};
    public void setColorFilter(ColorFilter colorFilter) {;}

    public void draw(Canvas canvas) {
        canvas.drawPath(path, curvePaint);
        if(mDebug) {
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
        points.add(new PointF(x, y));
    }
    public void touch_move(float x, float y) {
        Log.i("info", "touch_move");
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
        Log.i("info", "touch_up");
        path.lineTo(mX, mY);
        rebuildCurve();
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    // Rebuild the curve to reduce the amount of points required.
    protected void rebuildCurve() {
        Log.i("info", "Rebuilding curve");
        CurveResampler resampler = new CurveResampler(points);
        points = resampler.getOptimisedPoints(5.0f);
        if(points.size() != 0) {
            path.reset();
            path.moveTo(points.get(0).x, points.get(0).y);
            for (int i = 1; i < points.size() - 1; i++) {
                path.quadTo(
                        points.get(i - 1).x, points.get(i - 1).y,
                        (points.get(i - 1).x + points.get(i).x) / 2, (points.get(i - 1).y + points.get(i).y) / 2
                );
            }
            path.lineTo(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
        }
    }
}
