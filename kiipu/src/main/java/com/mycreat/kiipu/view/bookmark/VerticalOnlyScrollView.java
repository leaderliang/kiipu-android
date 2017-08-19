package com.mycreat.kiipu.view.bookmark;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by zhanghaihai on 2017/8/19.
 */
public class VerticalOnlyScrollView extends ScrollView{
    private float mDownPosY;
    private float mDownPosX;

    public VerticalOnlyScrollView(Context context) {
        super(context);
    }

    public VerticalOnlyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalOnlyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        final int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mDownPosX = x;
                mDownPosY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaX = Math.abs(x - mDownPosX);
                final float deltaY = Math.abs(y - mDownPosY);
                // 这里是够拦截的判断依据是左右滑动，读者可根据自己的逻辑进行是否拦截
                if (deltaX > deltaY) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
