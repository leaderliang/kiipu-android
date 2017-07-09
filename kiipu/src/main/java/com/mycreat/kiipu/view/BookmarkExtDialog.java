package com.mycreat.kiipu.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mycreat.kiipu.R;
import com.mycreat.kiipu.utils.bind.BindView;
import com.mycreat.kiipu.utils.ViewUtils;

/**
 * 用于展示书签详情，可左右切换书签
 * Created by zhanghaihai on 2017/6/28.
 */

public class BookmarkExtDialog extends AppCompatDialogFragment implements GestureDetector.OnGestureListener, View.OnTouchListener {
    @BindView(R.id.recyclerView)
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private GestureDetector gestureDetector;
    private int currentPosition;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtils.bindViews(view, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        adapter = new BookMarkSwitchPanelAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        view.setOnTouchListener(this);
        gestureDetector = new GestureDetector(getContext(), this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_bookmark_detail, container, false);
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
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > 0 && currentPosition < recyclerView.getChildCount()) {
            currentPosition++;
            // 手向左滑动，下一个bookmark
        } else if (e2.getX() - e1.getX() > 0 && currentPosition > 0) {
            // 向右滑动，上一个bookmark
            currentPosition--;
        }
        recyclerView.smoothScrollToPosition(currentPosition);

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }


}
