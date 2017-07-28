package com.mycreat.kiipu.view.bookmark;

import android.support.v7.widget.RecyclerView;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.view.cardswipelayout.OnSwipeListener;

/**
 * Created by zhanghaihai on 2017/7/27.
 */
public class BookmarkOnSwipeListener implements OnSwipeListener<Bookmark>{
    @Override
    public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, Bookmark bookmark, int direction) {

    }

    @Override
    public void onSwipedClear() {

    }
}
