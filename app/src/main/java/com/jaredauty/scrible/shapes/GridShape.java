package com.jaredauty.scrible.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by Jared on 07/07/2016.
 */
public class GridShape extends Drawable {
    private float mGridSize;
    private Matrix mMatrix;
    private float mWidth;
    private float mHeight;
    private Paint mPaint;
    private float[] mPoints;
    private boolean mPointsValid;

    public GridShape(float gridSize, float width, float height) {
        super();
        mGridSize = gridSize;
        mWidth = width;
        mHeight = height;
        mMatrix = new Matrix();
        mPointsValid = false;

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(1.0f);
    }

    public void setMatrix(Matrix matrix) {
        mMatrix = matrix;
        mPointsValid = false;
    }
    public void setWidth(float width) {
        mWidth = width;
        mPointsValid = false;
    }
    public void setHeight(float height) {
        mHeight = height;
        mPointsValid = false;
    }
    public void setAlpha(int i) {;}
    public int getAlpha() {return 255;}
    public int getOpacity() {return 255;};
    public void setColorFilter(ColorFilter colorFilter) {;}

    public void draw(Canvas canvas) {
        Log.i("info", "drawing grid");
        int numX = getNumXLines();
        int numY = getNumYLines();
        float points[] = getPoints();
        // Draw Points
        for (int i = 0; i < numX; i++) {
            canvas.drawLine(
                    points[i * 4],
                    points[(i * 4) + 1],
                    points[(i * 4) + 2],
                    points[(i * 4) + 3],
                    mPaint
            );
        }
        int offset = numX * 4;
        for (int i = 0; i < numY; i++) {
            canvas.drawLine(
                    points[offset + (i * 4)],
                    points[offset + (i * 4) + 1],
                    points[offset + (i * 4) + 2],
                    points[offset + (i * 4) + 3],
                    mPaint
            );
        }
    }
    private float[] getPoints() {
        if (!mPointsValid) {
            Log.i("info", "generating points");
            int numX = getNumXLines();
            int numY = getNumYLines();
            // numX + numY -> number of lines, 2 because each line has two points, 2 because point has two coords
            float points[] = new float[(numX + numY) * 4];
            int offset = 0;
            // Vertical
            for (int i = 0; i < numX; i++) {
                // Start
                points[offset] = i * mGridSize;
                offset++;
                points[offset] = 0;
                offset++;
                // End
                points[offset] = i * mGridSize;
                offset++;
                points[offset] = mHeight;
                offset++;
            }
            // Horizontal
            for (int i = 0; i < numY; i++) {
                // Start
                points[offset] = 0;
                offset++;
                points[offset] = i * mGridSize;
                offset++;
                // End
                points[offset] = mWidth;
                offset++;
                points[offset] = i * mGridSize;
                offset++;
            }
            // Transform points
            mMatrix.mapPoints(points);
            mPoints = points;
            mPointsValid = true;
        }
        return mPoints;
    }
    private int getNumXLines() {return (int) (mWidth / mGridSize) + 1;}
    private int getNumYLines() {return (int) (mHeight / mGridSize) + 1;}
}
