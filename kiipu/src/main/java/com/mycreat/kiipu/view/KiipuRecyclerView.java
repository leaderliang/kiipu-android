package com.mycreat.kiipu.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import com.mycreat.kiipu.utils.GlideUtil;

/**
 * 自定义 RecyclerView
 * 实现在 view 滑动过程中，优化内存，节省流量
 * Created by liangyanqiao on 2017/5/30.
 */
public class KiipuRecyclerView extends RecyclerView {

    public KiipuRecyclerView(Context context) {
        super(context,null);
    }

    public KiipuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public KiipuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addOnScrollListener(new ImageAutoLoadScrollListener());
    }

    //监听滚动来对图片加载进行判断处理
    public class ImageAutoLoadScrollListener extends OnScrollListener{

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState){
                case SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                    //当屏幕停止滚动，加载图片
                    try {
                        if(getContext() != null) GlideUtil.getInstance().resumeRequests(getContext());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case SCROLL_STATE_DRAGGING: // The RecyclerView is currently being dragged by outside input such as user touch input.
                    //当屏幕滚动且用户使用的触碰或手指还在屏幕上，停止加载图片
                case SCROLL_STATE_SETTLING: // The RecyclerView is currently animating to a final position while not under outside control.
                    //由于用户的操作，屏幕产生惯性滑动，停止加载图片
                    try {
                        if(getContext() != null) GlideUtil.getInstance().pauseRequests(getContext());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

}
