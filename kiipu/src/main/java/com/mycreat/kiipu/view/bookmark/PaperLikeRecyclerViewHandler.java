package com.mycreat.kiipu.view.bookmark;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import com.mycreat.kiipu.databinding.BookmarkDetailDialogBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarkDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

/**
 * 用于控制RecyclerView翻页效果
 * Created by zhanghaihai on 2017/8/16.
 */
public class PaperLikeRecyclerViewHandler extends RecyclerView.OnScrollListener implements View.OnTouchListener {
    private BookmarkDialog bookmarkDialog;
    private RecyclerView mRecyclerView;
    private MOnFlingListener onFlingListener = new MOnFlingListener();
    private DataResolver dataResolver;
    private float offsetX = 0;
    private int offsetY= 0;
    private float startX = 0;
    private float startY;

    public void smoothScrollTo(int position){
        scrollTo(position, true);
    }

    public void scrollTo(int position, boolean isSmooth){
        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.setOnFlingListener(null);
        bookmarkDialog.setCurrentPosition(position);
        if(isSmooth)
            mRecyclerView.smoothScrollToPosition(position);
        else
            mRecyclerView.scrollToPosition(position);


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.addOnScrollListener(this);
        mRecyclerView.setOnFlingListener(onFlingListener);
        if(event.getAction() == MotionEvent.ACTION_DOWN)  {
            startX = offsetX;
            startY = offsetY;
        }
        return false;

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(newState == SCROLL_STATE_IDLE){
            onFlingListener.onFinished((int)(offsetX -startX), (int)(offsetY - startY), false);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        offsetX += dx;
        offsetY += dy;
        int lastVisiblePosition = -1;
        if(dataResolver != null && recyclerView.getAdapter().getItemCount() > 0 && (lastVisiblePosition = dataResolver.getLastVisiblePosition()) == recyclerView.getAdapter().getItemCount() -1 ){
            dataResolver.onLoadMore(lastVisiblePosition);
        }

    }

    class MOnFlingListener extends RecyclerView.OnFlingListener{

        @Override
        public boolean onFling(int velocityX, int velocityY) {

            return onFinished(velocityX, velocityY, true);
        }

        public boolean onFinished(int velocityX, int velocityY, boolean isFling){

            if(Math.abs(velocityX )> Math.abs(velocityY)) { //横向大于纵向时移动
                if(isFling || Math.abs(offsetX) >= 0.45 * mRecyclerView.getWidth()){
                    if(velocityX <= 0 && bookmarkDialog.getCurrentPosition() > 0){
                        smoothScrollTo(bookmarkDialog.getCurrentPosition() - 1);
                    }else if(velocityX > 0 &&  bookmarkDialog.getCurrentPosition() + 1 < mRecyclerView.getAdapter().getItemCount()){
                        smoothScrollTo(bookmarkDialog.getCurrentPosition() + 1);
                    }else{
                        scrollTo(bookmarkDialog.getCurrentPosition(), false);
                    }
                }else{
                    smoothScrollTo(bookmarkDialog.getCurrentPosition());
                }
                startX = offsetX = 0;
                return true;
            }else{
                startX = offsetX = 0;
                return false;
            }
        }
    }

    public void setUpRecycleView(@NotNull RecyclerView recycleView, @NotNull BookmarkDetailDialogBinding binding, @Nullable DataResolver dataResolver) {

        mRecyclerView = recycleView;
        bookmarkDialog = binding.getBookmarkDialog();
        mRecyclerView.scrollToPosition(bookmarkDialog.getCurrentPosition());
        //处理滑动
        recycleView.setOnFlingListener(onFlingListener);
        //设置滚动监听，记录滚动的状态，和总的偏移量
        recycleView.addOnScrollListener(this);
        //记录滚动开始的位置
        recycleView.setOnTouchListener(this);
        this.dataResolver = dataResolver;
    }

    public DataResolver getDataResolver() {
        return dataResolver;
    }

    public void setDataResolver(DataResolver dataResolver) {
        this.dataResolver = dataResolver;
    }

    public interface DataResolver{
        public int getLastVisiblePosition();
        public int getFirstVisiblePosition();
        public void onLoadMore(int position);
        public void onRefresh();
    }
}
