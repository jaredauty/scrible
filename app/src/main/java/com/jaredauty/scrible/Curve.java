package com.jaredauty.scrible;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by Jared on 04/07/2016.
 * Class for handling curves created by the user.
 */
public class Curve extends Drawable {

    protected Paint paint;

    public Curve() {
        super();
        paint = new Paint();
        paint.setARGB(255, 0, 0, 0);
    }
    public void setAlpha(int i) {;}
    public int getAlpha() {return 255;}
    public int getOpacity() {return 255;};
    public void setColorFilter(ColorFilter colorFilter) {;}

    public void draw(Canvas canvas) {
        canvas.drawCircle(50.0f, 50.0f, 50.0f, paint);
    }
}
