package com.mycreat.kiipu.view.bookmark;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.view.cardswipelayout.OnSwipeListener;

/**
 * Created by zhanghaihai on 2017/7/27.
 */
public class MOnSwipeListener implements OnSwipeListener<Bookmark>{
    @Override
    public void onSwiping(RecyclerView.ViewHolder viewHolder, float ratio, int direction) {

//        MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
//        viewHolder.itemView.setAlpha(1 - Math.abs(ratio) * 0.2f);
//        if (direction == CardConfig.SWIPING_LEFT) {
//            myHolder.dislikeImageView.setAlpha(Math.abs(ratio));
//        } else if (direction == CardConfig.SWIPING_RIGHT) {
//            myHolder.likeImageView.setAlpha(Math.abs(ratio));
//        } else {
//            myHolder.dislikeImageView.setAlpha(0f);
//            myHolder.likeImageView.setAlpha(0f);
//        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, Bookmark t, int direction) {
        //        MyAdapter.MyViewHolder myHolder = (MyAdapter.MyViewHolder) viewHolder;
//        viewHolder.itemView.setAlpha(1f);
//        myHolder.dislikeImageView.setAlpha(0f);
//        myHolder.likeImageView.setAlpha(0f);
//        Toast.makeText(MainActivity.this, direction == CardConfig.SWIPED_LEFT ? "swiped left" : "swiped right", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSwipedClear() {
//        Toast.makeText(MainActivity.this, "data clear", Toast.LENGTH_SHORT).show();
//        recyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initData();
//                recyclerView.getAdapter().notifyDataSetChanged();
//            }
//        }, 3000L);
    }
}
