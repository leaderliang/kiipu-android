package com.mycreat.kiipu.view.bookmark;

import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.mycreat.kiipu.BR;
import com.mycreat.kiipu.databinding.BookmarkDetailDialogBinding;
import com.mycreat.kiipu.model.BookmarkDialog;
import com.mycreat.kiipu.utils.Constants;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用于控制RecyclerView翻页效果
 * Created by zhanghaihai on 2017/8/16.
 */
public class PaperLikeRecyclerViewHandler implements GestureDetector.OnGestureListener, View.OnTouchListener  {
    private RecyclerView recyclerView;
    private View rootView;
    private Lock lock = new ReentrantLock();
    private float touchDownX = 0;
    private RecyclerView.Adapter adapter;
    private GestureDetector gestureDetector;
    private BookmarkDialog bookmarkDialog;
    private BookmarkDetailDialogBinding binding;
    private float dragDistanceX;

    public PaperLikeRecyclerViewHandler(RecyclerView recyclerView, View rootView, BookmarkDialog bookmarkDialog, BookmarkDetailDialogBinding binding) {
        this.recyclerView = recyclerView;
        this.rootView = rootView;
        gestureDetector = new GestureDetector(rootView.getContext(), this);
        this.bookmarkDialog = bookmarkDialog;
        this.binding = binding;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(e1 == null || e2 == null || isVerticalScroll(e1, e2 ) ) {
            lock.lock();
            dragDistanceX = 0;
            lock.unlock();
            return false;
        }else {
            lock.lock();
            dragDistanceX = e2.getX() - e1.getX();
            lock.unlock();
            return true;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        lock.lock();
        dragDistanceX = 0;
        lock.unlock();

        if(e1 != null && e2 != null &&isVerticalScroll(e1, e2)) return false; //横向距离下雨纵向距离不滑动
        float flingDistanceX = dragDistanceX;
        if(e1 != null || e2 != null){
            flingDistanceX = dragDistanceX; //onFling未获取到距离，使用滚动距离
            if(flingDistanceX == 0 && e2 != null){//未获取到滚动距离，使用监听到的touch距离
                flingDistanceX = e2.getX() - touchDownX;
            }
        }
        if ( flingDistanceX < 0 && bookmarkDialog.getCurrentPosition() < bookmarkDialog.getAdapter().getItemCount() -1) {
            smoothScrollTo(bookmarkDialog.getCurrentPosition() + 1);
            return true;
            // 手向左滑动，下一个bookmark
        } else if (flingDistanceX > 0 && bookmarkDialog.getCurrentPosition() > 0) {
            // 向右滑动，上一个bookmark
            smoothScrollTo(bookmarkDialog.getCurrentPosition() -1);
            return true;
        }else {
            recyclerView.scrollToPosition(bookmarkDialog.getCurrentPosition());
            return true;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean handled = gestureDetector.onTouchEvent(event);
        if(!handled && event.getAction() ==  MotionEvent.ACTION_UP && dragDistanceX != 0){
            lock.lock();
            if(Math.abs(dragDistanceX) <= rootView.getWidth() * 0.3f) {
                smoothScrollTo(bookmarkDialog.getCurrentPosition());
            }else{
                if(dragDistanceX > 0 && bookmarkDialog.getCurrentPosition() > 0) {
                    smoothScrollTo(bookmarkDialog.getCurrentPosition() - 1);
                }else if(dragDistanceX < 0 && bookmarkDialog.getCurrentPosition() < bookmarkDialog.getAdapter().getItemCount() - 1){
                    smoothScrollTo(bookmarkDialog.getCurrentPosition() + 1);
                }else{
                    if(dragDistanceX > 0 && bookmarkDialog.getCurrentPosition() == 0 && bookmarkDialog.getCurrentPosition() == bookmarkDialog.getAdapter().getItemCount() - 1) {
                        smoothScrollTo(bookmarkDialog.getCurrentPosition());
                    }
                }
            }
            lock.unlock();
            return true;
        }else if(event.getAction() == MotionEvent.ACTION_DOWN){
            touchDownX = event.getX();
            return handled;
        }else{
            return handled;
        }

    }

    public void smoothScrollTo(int position){
        bookmarkDialog.setCurrentPosition(position);
        recyclerView.smoothScrollToPosition(position);
    }

    public boolean isVerticalScroll(MotionEvent e1, MotionEvent e2){
        return Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY());
    }
}
