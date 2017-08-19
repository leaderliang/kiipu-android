package com.mycreat.kiipu.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.*;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;

/**
 * Created by haihai.zhang on 2017/1/12.
 */

public abstract class RippleUtil {
    private PointF startPoint = new PointF();
    private float radius = 0;
    private Paint mPaint;
    private int color = ContextCompat.getColor(KiipuApplication.appContext, R.color.colorPrimary);
    private ObjectAnimator objectAnimator;
    private RadialGradient mRadialGradient;
    private float centerAlphaRate = 0.1f;
    private boolean clearAfterEnd = true;
    public abstract float getRadius();
    public abstract float getExpandRadius();
    public abstract void invalidateUI();

    public int getColor(){
        return color;
    }
    public void setColor(Integer nColor) {
        color = nColor == null ? ContextCompat.getColor(KiipuApplication.appContext, R.color.colorPrimary): nColor;
        mPaint = new Paint();
        mPaint.setAlpha(100);
        mPaint.setColor(color);
    }

    private boolean tapDown = false;
    public void handleTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startRipple(event.getX(), event.getY(), color);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                invalidateUI();
                break;
            case MotionEvent.ACTION_CANCEL:
                tapDown = false;
                invalidateUI();
                break;
        }

    }

    public void startRipple(float x, float y, Integer nColor){
        //取消之前动画
//        setRadius(0);
//        if(objectAnimator != null){
//            objectAnimator.cancel();
//        }
        //开始新的动画
        setColor(nColor);
        startPoint.x = x;
        startPoint.y = y;
        radius = getRadius();
        setRadius(radius);
        tapDown = true;

        objectAnimator = ObjectAnimator.ofFloat(this, "radius", getRadius(), getExpandRadius()).setDuration(500);
        objectAnimator.setInterpolator(new LinearInterpolator());
        if(tapDown) {
            objectAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator arg0) {

                }

                @Override
                public void onAnimationRepeat(Animator arg0) {

                }

                @Override
                public void onAnimationEnd(Animator arg0) {
                    if(clearAfterEnd)
                        setRadius(0);
                }

                @Override
                public void onAnimationCancel(Animator arg0) {

                }
            });
            tapDown = false;
        }
        objectAnimator.start();
    }

    /**
     * 使用
     * @param x
     * @param y
     */
    public void startRipple(float x, float y){
        startRipple(x, y, color);
    }


    /** 在执行ObjectAnimator的过程中就会调用此方法 */
    public void setRadius(float radius) {
        this.radius = (int) radius;
        if (this.radius > 0) {
            mRadialGradient = new RadialGradient(startPoint.x, startPoint.y, this.radius, ColorUtil.Companion.getColor(
                    color, centerAlphaRate), color, Shader.TileMode.MIRROR);
            mPaint.setShader(mRadialGradient);
        }
        invalidateUI();
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(startPoint.x, startPoint.y, radius, mPaint);
    }


    public boolean isClearAfterEnd() {
        return clearAfterEnd;
    }

    public void setClearAfterEnd(boolean clearAfterEnd) {
        this.clearAfterEnd = clearAfterEnd;
    }

    public float getCenterAlphaRate() {
        return centerAlphaRate;
    }

    public void setCenterAlphaRate(float centerAlphaRate) {
        this.centerAlphaRate = centerAlphaRate;
    }
}
