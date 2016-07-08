package com.jaredauty.scrible.shapes;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;

/**
 * Created by Jared on 08/07/2016.
 */
public class MultiTouchSceneManipulator extends SceneManipulator {
    private PointF mPreviousPoint1;
    private PointF mPreviousPoint2;
    private PointF mCurrentPoint1;
    private PointF mCurrentPoint2;

    public MultiTouchSceneManipulator(PointF initialPoint1, PointF initialPoint2) {
        mPreviousPoint1 = initialPoint1;
        mPreviousPoint2 = initialPoint2;
        mCurrentPoint1 = initialPoint1;
        mCurrentPoint2 = initialPoint2;
    }

    public void setPoint1(PointF point){
        mPreviousPoint1 = mCurrentPoint1;
        mCurrentPoint1 = point;
    }
    public void setPoint2(PointF point){
        mPreviousPoint2 = mCurrentPoint2;
        mCurrentPoint2 = point;
    }
    protected Matrix constructMatrix() {
        Log.i("info", "Constructing Matrix");
        Matrix matrix = new Matrix();
        // Translation
        float dx = ((mCurrentPoint1.x + mCurrentPoint2.x) / 2.0f) - ((mPreviousPoint1.x + mPreviousPoint2.x) / 2.0f);
        float dy = ((mCurrentPoint1.y + mCurrentPoint2.y) / 2.0f) - ((mPreviousPoint1.y + mPreviousPoint2.y) / 2.0f);
        matrix.postTranslate(dx, dy);

        // Scale
        float scaleValue = 1;
        if (mCurrentPoint1 != mCurrentPoint2 && mPreviousPoint1 != mPreviousPoint2) {
            scaleValue = (
                    ((float) Math.pow(mCurrentPoint1.x - mCurrentPoint2.x, 2) + (float) Math.pow(mCurrentPoint1.y - mCurrentPoint2.y, 2)) /
                    ((float) Math.pow(mPreviousPoint1.x - mPreviousPoint2.x, 2) + (float) Math.pow(mPreviousPoint1.y - mPreviousPoint2.y, 2))
            );
        }
        matrix.postScale(scaleValue, scaleValue, ((mCurrentPoint1.x + mCurrentPoint2.x) / 2.0f), ((mCurrentPoint1.y + mCurrentPoint2.y) / 2.0f));
        return matrix;
    }
}
