package com.jaredauty.scrible;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Canvas;

import com.jaredauty.scrible.bible.Bible;
import com.jaredauty.scrible.database.BibleParser;
import com.jaredauty.scrible.gestures.ScaleGestures;
import com.jaredauty.scrible.gestures.ScrollGestures;
import com.jaredauty.scrible.shapes.CurveShape;
import com.jaredauty.scrible.shapes.GridShape;
import com.jaredauty.scrible.shapes.MultiTouchSceneManipulator;
import com.jaredauty.scrible.text.Text;

import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by Jared on 03/07/2016.
 */
public class MainSurface extends SurfaceView implements SurfaceHolder.Callback {
    private enum TouchModes {
        NEUTRAL, CURVE_DRAW, SCENE_MANIP, MONITORING
    }
    private SurfaceHolder surfaceHolder;
    private CurveShape currentCurve;
    //private ArrayList<CurveShape> curves;
    private boolean debug;
    private int mWidth;
    private int mHeight;
    private GridShape mBackgroundGrid;
    private MultiTouchSceneManipulator mSceneManipulator;
    private Matrix mSceneMatrix;
    private ArrayList<Integer> mCurrentPointers;
    private TouchModes mCurrentTouchMode;
    private PointF mPreviousPointer;
    //private Text mTestText;
    private Page mPage;
    private ScaleGestures mScaleGestures;
    private ScrollGestures mScrollGestures;

    public MainSurface(Context context) {
        super(context);
        initialise();
    }

    public MainSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise();
    }

    protected void initialise(){
        mSceneMatrix = new Matrix();
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        mScaleGestures = new ScaleGestures(getContext());
        mScrollGestures = new ScrollGestures(getContext());

        mBackgroundGrid = new GridShape(50, 200, 200);

        mCurrentTouchMode = TouchModes.NEUTRAL;
        mCurrentPointers = new ArrayList<Integer>();

        //mTestText = new Text(getResources(), "In the beginning...\nAmen.", 510);

        // mPageText = m_bible.getVerse("Genesis", 1, 5);
        //mTestText = new Text(getResources(), "No passage selected yet.");
        mPage = new Page(getContext());

        clean();
    }

    protected void repaint() {
        Canvas c = null;
        try {
            c = surfaceHolder.lockCanvas();
            if (c != null) {
                c.setMatrix(mSceneMatrix);
                drawBackground(c);

                if (debug) {
                    mBackgroundGrid.draw(c);
                }
                mPage.draw(c);
            }
        } finally {
            if (c != null) {
                surfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }

    public void setPassage(String book, int chapterNum) {
        mPage.setBookTitle(book, chapterNum);
    }

    public void addVerses(ArrayList<String> verses) {
        mPage.addVerses(verses);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mPage.setTextBounds(0, 0, getWidth(), getHeight());
        repaint();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.i("info", "Setting width and height.");
        // Calculate the correct height given the
        mBackgroundGrid.setWidth(w);
        mBackgroundGrid.setHeight(h);
        mPage.setTextBounds(0, 0, w, h);
        mWidth = w;
        mHeight = h;
    }
    private static final float CURVEDRAW_TOLERANCE = 10;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //mScaleGestures.onTouch(this, event);
        mScrollGestures.onTouch(this, event);
        return true;
    }

    public void setViewportMatrix(Matrix matrix) {
        mSceneMatrix = matrix;
        repaint();
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        final int action = MotionEventCompat.getActionMasked(event);
//        switch (action) {
//           case MotionEvent.ACTION_POINTER_DOWN:
//               Log.i("info", "action_pointer_down");
//               mCurrentPointers.add(event.getPointerId(event.getActionIndex()));
//               if(mCurrentPointers.size() == 2) {
//                   Log.i("info", "going into scene manip mode.");
//                   if (mCurrentTouchMode == TouchModes.CURVE_DRAW) {
//                       Log.i("info", "Curve drawing interrupted by multitouch.");
//                       // removing current curve.
//                       mPage.removeCurve(currentCurve);
//                   }
//                   mCurrentTouchMode = TouchModes.SCENE_MANIP;
//                   mSceneManipulator = new MultiTouchSceneManipulator(
//                           new PointF(event.getX(mCurrentPointers.get(0)), event.getY(mCurrentPointers.get(0))),
//                           new PointF(event.getX(mCurrentPointers.get(1)), event.getY(mCurrentPointers.get(1)))
//                   );
//               }
//               repaint();
//               break;
//           case MotionEvent.ACTION_POINTER_UP:
//               Log.i("info", "action_pointer_up");
//               mCurrentPointers.remove(event.getPointerId(event.getActionIndex()));
//               if(mCurrentPointers.size() < 2) {
//                   Log.i("info", "going out of scene manip mode");
//                   mCurrentTouchMode = TouchModes.NEUTRAL;
//               }
//               repaint();
//               break;
//            case MotionEvent.ACTION_DOWN:
//                Log.i("info", "action_down");
//                Log.i("info", "start monitoring for curve draw");
//                mCurrentTouchMode = TouchModes.MONITORING;
//                mPreviousPointer = screenToWorldPoint(event.getX(), event.getY());
//                mCurrentPointers.add(event.getPointerId(event.getActionIndex()));
//                repaint();
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.i("info", "action_up");
//                Log.i("info", "turning off monitoring and going back to neutral");
//                if (mCurrentTouchMode == TouchModes.CURVE_DRAW) {
//                    currentCurve.finishCurve();
//                }
//                mCurrentTouchMode = TouchModes.NEUTRAL;
//                mCurrentPointers.clear();
//                repaint();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.i("info", "action_move");
//                if (mCurrentTouchMode == TouchModes.MONITORING) {
//                    float dx = Math.abs(event.getX() - mPreviousPointer.x);
//                    float dy = Math.abs(event.getY() - mPreviousPointer.y);
//                    if (dx >= CURVEDRAW_TOLERANCE || dy >= CURVEDRAW_TOLERANCE) {
//                        Log.i("info", "changing to draw mode.");
//                        mCurrentTouchMode = TouchModes.CURVE_DRAW;
//                        // Create a current curve
//                        currentCurve = new CurveShape(debug, screenToWorldPoint(event.getX(), event.getY()));
//                        mPage.addCurve(currentCurve);
//                    }
//                }
//                switch (mCurrentTouchMode) {
//                    case CURVE_DRAW:
//                        PointF point = screenToWorldPoint(event.getX(), event.getY());
//                        currentCurve.addPoint(point);
//                        break;
//                    case SCENE_MANIP:
//                        PointF point1 = new PointF(event.getX(mCurrentPointers.get(0)), event.getY(mCurrentPointers.get(0)));
//                        PointF point2 = new PointF(event.getX(mCurrentPointers.get(1)), event.getY(mCurrentPointers.get(1)));
//                        mSceneManipulator.setPoint1(point1);
//                        mSceneManipulator.setPoint2(point2);
//                        Matrix sceneOffset = mSceneManipulator.getMatrix();
//                        Log.i("info", "generated scene offset.");
//                        mSceneMatrix.postConcat(sceneOffset);
//                        Log.i("info", "updated scene matrix.");
//                        break;
//                }
//                repaint();
//                break;
//        }
//        return true;
//    }

    public void clean() {
        mSceneManipulator = new MultiTouchSceneManipulator(new PointF(0.0f, 0.0f),new PointF(0.0f, 0.0f));
        mSceneMatrix = new Matrix();
        mPage.cleanCurves();
    }

    public void cleanVerses() {
        mPage.cleanVerses();
    }

    public void toggleDebug() {
        debug = !debug;
        mPage.setDebug(debug);
    }
    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
    }
    private PointF screenToWorldPoint(float x, float y) {
        float point[] = new float[2];
        point[0] = x;
        point[1] = y;
        Matrix inverseScene = new Matrix();
        mSceneMatrix.invert(inverseScene);
        inverseScene.mapPoints(point);
        return new PointF(point[0], point[1]);
    }
}
