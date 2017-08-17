package com.mycreat.kiipu.view.bookmark;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.mycreat.kiipu.BR;
import com.mycreat.kiipu.databinding.BookmarkDetailDialogBinding;
import com.mycreat.kiipu.model.BookmarkDialog;

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
    private float dragDistanceX = 0;
    private RecyclerView.Adapter adapter;
    private GestureDetector gestureDetector;
    private BookmarkDialog bookmarkDialog;
    private BookmarkDetailDialogBinding binding;
    public PaperLikeRecyclerViewHandler(RecyclerView recyclerView, View rootView, BookmarkDialog bookmarkDialog, BookmarkDetailDialogBinding binding) {
        this.recyclerView = recyclerView;
        this.rootView = rootView;
        gestureDetector = new GestureDetector(rootView.getContext(), this);
        this.bookmarkDialog = bookmarkDialog;
        this.binding = binding;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        lock.lock();
        dragDistanceX = 0;
        lock.unlock();
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
        if(e1 == null || e2 == null) return false;
        lock.lock();
        dragDistanceX = e2.getX() - e1.getX();
        lock.unlock();
        if(Math.abs(distanceX) > rootView.getWidth() * 0.4 ){
            if(e2.getX() - e1.getX() > 0 && bookmarkDialog.getCurrentPosition() > 0) {
                bookmarkDialog.setCurrentPosition(bookmarkDialog.getCurrentPosition() - 1);
                recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition());
                return true;
            }else if(e2.getX() - e1.getX() < 0 && bookmarkDialog.getCurrentPosition() < bookmarkDialog.getAdapter().getItemCount() -1){
                bookmarkDialog.setCurrentPosition(bookmarkDialog.getCurrentPosition() + 1);
                recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition());
                return true;
            }

        }else{

        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e1 == null || e2 == null || Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY())) return false; //横向距离下雨纵向距离不滑动
        if (e1.getX() - e2.getX() > 0 && bookmarkDialog.getCurrentPosition() < bookmarkDialog.getAdapter().getItemCount() -1) {
            bookmarkDialog.setCurrentPosition(bookmarkDialog.getCurrentPosition() + 1);
            recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition());
            return true;
            // 手向左滑动，下一个bookmark
        } else if (e2.getX() - e1.getX() > 0 && bookmarkDialog.getCurrentPosition() > 0) {
            // 向右滑动，上一个bookmark
            bookmarkDialog.setCurrentPosition(bookmarkDialog.getCurrentPosition() -1 );
            recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition());
            return true;
        }else {
            return false;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean handled = gestureDetector.onTouchEvent(event);
        if(!handled && event.getAction() ==  MotionEvent.ACTION_UP){
            lock.lock();
            if(Math.abs(dragDistanceX) <= rootView.getWidth() * 0.4f) {
                recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition());
            }else{
                if(dragDistanceX > 0 && bookmarkDialog.getCurrentPosition() > 0) {
                    recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition() - 1);
                }else if(bookmarkDialog.getCurrentPosition() < bookmarkDialog.getAdapter().getItemCount() - 1){
                    recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition() + 1);
                }else{
                    recyclerView.smoothScrollToPosition(bookmarkDialog.getCurrentPosition());
                }
            }
            dragDistanceX = 0;
            lock.unlock();
            return true;
        }else{
            return handled;
        }

    }

    public void smoothScrollTo(){
        binding.notifyPropertyChanged(BR.bookmarkDialog);
    }
}
