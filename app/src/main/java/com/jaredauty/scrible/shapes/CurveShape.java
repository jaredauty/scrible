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

    public CurveShape(boolean debug, PointF initialPoint) {
        this(initialPoint);
        mDebug = debug;
    }

    public CurveShape(PointF initialPoint) {
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
        mDebug = false;

        // Initialise start point
        points = new ArrayList<PointF>();
        mX = initialPoint.x;
        mY = initialPoint.y;
        points.add(initialPoint);
        path = new Path();
        path.moveTo(initialPoint.x, initialPoint.y);
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
    public void addPoint(PointF point) {
        float dx = Math.abs(point.x - mX);
        float dy = Math.abs(point.y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (point.x + mX)/2, (point.y + mY)/2);
            mX = point.x;
            mY = point.y;
            points.add(point);
        }
    }
    public void finishCurve() {
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
