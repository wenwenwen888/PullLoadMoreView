package com.kingcheergame.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by won on 2019-09-26.
 * 小圆点自定义View
 */
public class DotView extends View {

    private float percent = 0.0f;
    private float maxRadius = 10f;
    private float maxDist = 30f;
    private Paint mPaint;

    public DotView(Context context) {
        super(context);
        init();
    }

    public DotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
    }

    public void setPercent(Float percent) {
        this.percent = percent;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = (float) (getWidth() / 2);
        float centerY = (float) (getHeight() / 2);
        float fl = 255 * percent * 1.5f + 30;
        if (fl > 255) {
            mPaint.setAlpha(255);
        } else {
            mPaint.setAlpha((int) fl);
        }
        if (percent <= 0.3f) {
            float radius = percent * 3.33f * maxRadius;
            canvas.drawCircle(centerX, centerY, radius, mPaint);
        } else {//画三个个圆
            float afterPercent = (percent - 0.3f) / 0.7f;
            if (afterPercent <= 1) {
                float radius = maxRadius - maxRadius / 2f * afterPercent;
                canvas.drawCircle(centerX, centerY, radius, mPaint);
                canvas.drawCircle(centerX - afterPercent * maxDist, centerY, maxRadius / 2, mPaint);
                canvas.drawCircle(centerX + afterPercent * maxDist, centerY, maxRadius / 2, mPaint);
            } else if (afterPercent > 1) {
                double d = afterPercent - 1.0;
                if (d > 1) {
                    d = 1.0;
                }
                double dfl = (1 - d * 2) * 255;
                if (dfl < 60) {
                    mPaint.setAlpha(0);
                } else {
                    mPaint.setAlpha((int) dfl);
                }
                canvas.drawCircle(centerX, centerY, maxRadius / 2, mPaint);
                canvas.drawCircle(centerX - maxDist, centerY, maxRadius / 2, mPaint);
                canvas.drawCircle(centerX + maxDist, centerY, maxRadius / 2, mPaint);
            }
        }

    }
}
