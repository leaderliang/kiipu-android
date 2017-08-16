package com.mycreat.kiipu.view.bookmark;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.LogUtil;
import com.mycreat.kiipu.utils.ViewUtils;
import com.mycreat.kiipu.utils.bind.BindView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于展示书签详情，可左右切换书签
 * Created by zhanghaihai on 2017/6/28.
 */

public class BookmarkDetailDialog extends DialogFragment  implements GestureDetector.OnGestureListener, View.OnTouchListener {
    @BindView(R.id.recyclerView)
    private RecyclerView recyclerView;
    private BookmarkDetailAdapter adapter;
    private GestureDetector gestureDetector;
    private int currentPosition;
    private List<Bookmark> _bookmarks = Collections.synchronizedList(new ArrayList<Bookmark>());
    private boolean ifCloseWhenTouchBlank = true;
    private OnCancelListener onCancelListener;
    private View rootView;
    private float dragDistanceX = 0;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        //无title样式
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //无边框
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        ViewUtils.bindViews(view, this);
        gestureDetector = new GestureDetector(getContext(), this);
        adapter = new BookmarkDetailAdapter(getActivity(), _bookmarks);
        recyclerView.setAdapter(adapter);

        //设置左右滑动布局管理
//        CardItemTouchHelperCallback<Bookmark> callback = new CardItemTouchHelperCallback<>(adapter, _bookmarks);
//        callback.setOnSwipedListener(new BookmarkOnSwipeListener());
//        final ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(recyclerView);
//        CardLayoutManager cardLayoutManager = new CardLayoutManager(recyclerView, touchHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setOnTouchListener(this);
        recyclerView.addOnScrollListener(new RecyclerScrollListener(recyclerView, adapter));
        recyclerView.scrollToPosition(currentPosition);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_bookmark_detail, container, false);
    }

    public void show(FragmentManager manager, String tag, int firstlyShowingPosition, List<Bookmark> bookmarks) {
        _bookmarks.clear();
        _bookmarks.addAll(bookmarks);
        currentPosition = firstlyShowingPosition;
        super.show(manager, tag);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DF_NO_PADDING){

            @Override
            public void onBackPressed() {
                if(onCancelListener != null){
                    onCancelListener.onCancel(BookmarkDetailDialog.this);
                }else{
                    dismiss();
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {

                return ifCloseWhenTouchBlank;
            }

        };
    }

    public interface OnCancelListener{
        public void onCancel(BookmarkDetailDialog customDialogFragment);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        dragDistanceX = 0;
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
        dragDistanceX = Math.abs(e2.getX() - e1.getX());
        if(Math.abs(distanceX) > rootView.getWidth() * 0.4 ){
            if(e2.getX() - e1.getX() > 0 && currentPosition > 0) {
                recyclerView.scrollToPosition(--currentPosition);
                return true;
            }else if(e2.getX() - e1.getX() < 0 && currentPosition < adapter.getItemCount() -1){
                recyclerView.scrollToPosition(++currentPosition);
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
        if (e1.getX() - e2.getX() > 0 && currentPosition < adapter.getItemCount() -1) {
            currentPosition++;
            recyclerView.smoothScrollToPosition(currentPosition);
            return true;
            // 手向左滑动，下一个bookmark
        } else if (e2.getX() - e1.getX() > 0 && currentPosition > 0) {
            // 向右滑动，上一个bookmark
            currentPosition--;
            recyclerView.smoothScrollToPosition(currentPosition);
            return true;
        }else {
            return false;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean handled = gestureDetector.onTouchEvent(event);
        if(!handled && event.getAction() ==  MotionEvent.ACTION_UP && dragDistanceX <= rootView.getWidth() * 0.4f ){
            dragDistanceX = 0;
            recyclerView.scrollToPosition(currentPosition);
            return true;
        }else{
            return handled;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
