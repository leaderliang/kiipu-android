package com.mycreat.kiipu.view.bookmark;

import android.support.v7.widget.RecyclerView;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.view.cardswipelayout.CardConfig;
import com.mycreat.kiipu.view.cardswipelayout.OnSwipeListener;

/**
 * Created by zhanghaihai on 2017/7/27.
 */
public class BookmarkOnSwipeListener implements OnSwipeListener<Bookmark>{
    private RecyclerView recyclerView;

    @Override
    public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {
//        HunterResultAdapter.PageInfoHolder myHolder = (HunterResultAdapter.PageInfoHolder) viewHolder;
//        viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);
//        if (direction == CardConfig.SWIPING_LEFT) {
//            swipeLeft(myHolder, Math.abs(ratio));
//        } else if (direction == CardConfig.SWIPING_RIGHT) {
//            swipeRight(myHolder, Math.abs(ratio));
//        } else {
//            swipeLeft(myHolder, 0f);
//            swipeRight(myHolder,0f);
//        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, Bookmark bookmark, int direction) {

    }

//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, Object o, int direction) {
////        HunterResultAdapter.PageInfoHolder myHolder = (HunterResultAdapter.PageInfoHolder) viewHolder;
//////        viewHolder.itemView.setBtnAlpha(1f);
//////        myHolder.dislikeImageView.setBtnAlpha(0f);
//////        myHolder.likeImageView.setBtnAlpha(0f);
//////        ToastUtils.show(direction == CardConfig.SWIPED_LEFT ? "swiped left" : "swiped right");
////        //恢复透明度
////        myHolder.itemView.setAlpha(1f);
////        swipeLeft(myHolder, 0f);
////        swipeRight(myHolder, 0f);
//
//    }

    @Override
    public void onSwipedClear() {
//        ToastUtils.show("data clear");
//        recyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initData();
//                recyclerView.getAdapter().notifyDataSetChanged();
//            }
//        }, 3000L);
    }

    private void swipeLeft(RecyclerView.ViewHolder holder, float rate){
//        ((HunterResultAdapter.PageInfoHolder)holder).setBtnAlpha(rate, 0);
    }

    private void swipeRight(RecyclerView.ViewHolder holder, float rate){
       /* ((HunterResultAdapter.PageInfoHolder)holder).setBtnAlpha(rate, 1);*/
    }
}
