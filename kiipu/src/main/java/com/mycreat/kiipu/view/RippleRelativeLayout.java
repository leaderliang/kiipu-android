package com.mycreat.kiipu.view;

/**
 * Created by haihai.zhang on 2017/1/12.
 */

import android.content.Context;
import android.graphics.*;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.utils.ColorUtil;
import com.mycreat.kiipu.utils.DrawUtil;
import com.mycreat.kiipu.utils.RippleUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Created by haihai.zhang on 2017/1/12.
 */

public class RippleRelativeLayout extends RelativeLayout {

    private RippleUtil rippleUtil;
    private boolean isStartedAfterMeasured;
    private Lock lock = new ReentrantLock();
    private Paint bgPaint = new Paint(ContextCompat.getColor(getContext(), R.color.white));
    public RippleRelativeLayout(Context context) {
        super(context);
       // init();
    }

    public RippleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public RippleRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       // init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if(isStartedAfterMeasured){
//            lock.lock();
//            if(isStartedAfterMeasured) {
//                isStartedAfterMeasured = false;
//                rippleUtil.startRipple(View.MeasureSpec.makeMeasureSpec(widthMeasureSpec,View.MeasureSpec.UNSPECIFIED) / 2f, View.MeasureSpec.makeMeasureSpec(heightMeasureSpec,View.MeasureSpec.UNSPECIFIED) / 2f);
//            }
//            lock.unlock();
//        }
    }

    private void init(){
//        setClickable(true);
//        rippleUtil = new RippleUtil() {
//            @Override
//            public float getRadius() {
//                return getWidth() > getHeight() ? getHeight() / 2 : getWidth() /2;
//            }
//
//            @Override
//            public float getExpandRadius() {
//                return getWidth() > getHeight() ? getWidth()  : getHeight();
//            }
//            @Override
//            public void invalidateUI() {
//                invalidate();
//            }
//
//        };
//        rippleUtil.setCenterAlphaRate(0.8f);
//        rippleUtil.setClearAfterEnd(false);
//        rippleUtil.setColor(ColorUtil.Companion.getColor(R.color.colorPrimary));
    }

    /** 在执行ObjectAnimator的过程中就会调用此方法 */
    public void setRadius(float radius) {
        rippleUtil.setRadius(radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.TRANSPARENT);
//        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), bgPaint, Canvas.ALL_SAVE_FLAG);
//        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
//
//        rippleUtil.draw(canvas);
//        //DrawUtil.drawRoundCorner(canvas, getWidth(), getHeight(), 10, 10, 0, 0);
//        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //rippleUtil.handleTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void changeBackground(int color) {
        setBackgroundColor(color);
//        if (getWidth() != 0 || getHeight() != 0){
//            rippleUtil.startRipple(getWidth() / 2f, getHeight() / 2f, color);
//        }else{
//            lock.lock();
//            isStartedAfterMeasured = true;
//            rippleUtil.setColor(color);
//            lock.unlock();
//        }
    }

}

