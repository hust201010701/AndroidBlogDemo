package com.orzangleli.textbounddemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * <p>description：
 * <p>===============================
 * <p>creator：lixiancheng
 * <p>create time：2018/8/13 下午9:16
 * <p>===============================
 * <p>reasons for modification：
 * <p>Modifier：
 * <p>Modify time：
 * <p>@version
 */

public class MyView extends View {
    private Paint mPaint;
    private final int TEXT_SIZE = 40;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.setStyle(Paint.Style.STROKE);

        measureText("e");
        Log.i("lxc", " <----分割线----> ");
        measureText("j");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String str = "e";
        Rect rect = new Rect();
        mPaint.getTextBounds(str, 0, str.length(), rect);
        float baseline = Math.abs(rect.top);
        canvas.drawText(str, 0, baseline, mPaint);
    }

    private void measureText(String str) {
        if (str == null) {
            return;
        }
        float width1 = mPaint.measureText(str);
        Log.i("lxc", "width1 ---> " + width1);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float height1 = Math.abs(fontMetrics.leading + fontMetrics.ascent) + fontMetrics.descent;
        Log.i("lxc", "height1 ---> " + height1);

        Rect rect = new Rect();
        mPaint.getTextBounds(str, 0, str.length(), rect);
        float width2 = rect.width();
        float height2 = rect.height();
        Log.i("lxc", "width2 ---> " + width2);
        Log.i("lxc", "height2 ---> " + height2);


        Path textPath = new Path();
        mPaint.getTextPath(str, 0, str.length(), 0.0f, 0.0f, textPath);
        RectF boundsPath = new RectF();
        textPath.computeBounds(boundsPath, true);
        float width3 = boundsPath.width();
        float height3 = boundsPath.height();
        Log.i("lxc", "width3 ---> " + width3);
        Log.i("lxc", "height3 ---> " + height3);
    }
}
