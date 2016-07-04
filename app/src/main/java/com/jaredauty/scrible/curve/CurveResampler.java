package com.jaredauty.scrible.curve;

import android.graphics.PointF;

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
                angleList[i - 1] = angleBetween2Lines(start, middle, end);
            }
            // Find the min angle
            double maxAngle = -1;
            int maxIndex = -1;
            for (int i = 0; i < angleList.length; i++) {
                if (angleList[i] > maxAngle) {
                    if (angleList[i] > angleThreshold) {
                        maxAngle = angleList[i];
                        maxIndex = i;
                    }
                }
            }
            if(maxIndex > -1) {
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
        double angle1 = Math.atan2(start.y - mid.y,
                start.x - mid.x);
        double angle2 = Math.atan2(mid.y - end.y,
               mid.x - end.x);
        return Math.abs(angle1-angle2);
    }
}
