package com.jaredauty.scrible.curve;

import android.graphics.PointF;
import android.util.Log;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Class to resample a curve to change the number or arrangements of points without altering the
 * shape.
 * Created by Jared on 04/07/2016.
 */
public class CurveResampler {
    protected ArrayList<PointF> mPoints;
    public CurveResampler(ArrayList<PointF> points) {
        mPoints = points;
    }

    public ArrayList<PointF> getOptimisedPoints(float angleThreshold) {
        double angleThresholdRads = Math.toRadians(angleThreshold);
        Log.i("info", String.format("Threshold %.2f", angleThreshold));
        ArrayList<PointF> points = mPoints;
        // Start the round
        // Should never go more times than there are points
        for(int j = 0; j < mPoints.size(); j++) {
            // Check that there are enough points left in the array
            if(points.size() < 3) {
                // Not enough points to remove
                break;
            }

            // Get all the angles
            double[] angleList = new double[points.size() - 2];
            for (int i = 1; i < points.size() - 1; i++) {
                PointF start = points.get(i - 1);
                PointF middle = points.get(i);
                PointF end = points.get(i + 1);
                double angle = angleBetween2Lines(start, middle, end);
                Log.i("info", String.format("angle %.2f", Math.toDegrees(angle)));
                angleList[i - 1] = angle;
            }
            // Find the min angle
            double maxAngle = -1;
            int maxIndex = -1;
            for (int i = 0; i < angleList.length; i++) {
                if (angleList[i] > maxAngle) {
                    if (angleList[i] < angleThresholdRads) {

                        maxAngle = angleList[i];
                        maxIndex = i+1;  // + 1 to account for angleList starting from point 1
                    }
                }
            }
            Log.i("info", String.format("maxAngle %.2f", maxAngle));
            if(maxIndex != -1) {
                Log.i("info", String.format("removing point %d",maxIndex));
                points.remove(maxIndex);
            } else {
                // Can't reduce anymore
                break;
            }
        }
        return points;
    }

    protected double angleBetween2Lines(PointF start, PointF mid, PointF end)
    {
        Vector2D vec1 = new Vector2D(start.y - mid.y, start.x - mid.x);
        Vector2D vec2 = new Vector2D(mid.y - end.y, mid.x - end.x);

        return Vector2D.angle(vec1, vec2);
    }
}
