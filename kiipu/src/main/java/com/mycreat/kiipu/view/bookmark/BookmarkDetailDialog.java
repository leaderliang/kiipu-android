package com.mycreat.kiipu.view.bookmark;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.BookmarkDetailAdapter;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.bind.BindView;
import com.mycreat.kiipu.utils.ViewUtils;
import com.mycreat.kiipu.view.cardswipelayout.CardItemTouchHelperCallback;
import com.mycreat.kiipu.view.cardswipelayout.CardLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于展示书签详情，可左右切换书签
 * Created by zhanghaihai on 2017/6/28.
 */

public class BookmarkDetailDialog extends AppCompatDialogFragment  implements GestureDetector.OnGestureListener, View.OnTouchListener {
    @BindView(R.id.recyclerView)
    private RecyclerView recyclerView;
    private BookmarkDetailAdapter adapter;
    private GestureDetector gestureDetector;
    private int currentPosition;
    private List<Bookmark> _bookmarks = Collections.synchronizedList(new ArrayList<Bookmark>());
    private boolean ifCloseWhenTouchBlank = true;
    private OnCancelListener onCancelListener;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewUtils.bindViews(view, this);
        adapter = new BookmarkDetailAdapter(getActivity(), _bookmarks);
        CardItemTouchHelperCallback<Bookmark> callback = new CardItemTouchHelperCallback<>(adapter, _bookmarks);
        callback.setOnSwipedListener(new MOnSwipeListener());
        callback.setOnSwipedListener(new BookmarkOnSwipeListener());
        LinearLayoutManager layoutManager = new LinearLayoutManager( getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        //去除边距
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        gestureDetector = new GestureDetector(getContext(), this);
        recyclerView.setOnTouchListener(this);
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
        //sortAdd(firstlyShowingPosition, bookmarks);
        super.show(manager, tag);
    }

    /**
     * 将要显示的放在第一个
     * @param firstlyShowingPosition
     * @param bookmarks
     */
    private void sortAdd(int firstlyShowingPosition, List<Bookmark> bookmarks) {
        int len = bookmarks.size();
        //_bookmarks.add(bookmarks.get(firstlyShowingPosition));
        for(int i = firstlyShowingPosition; i < len ;  i ++){
            _bookmarks.add(bookmarks.get(i));
        }

        for(int i = 0; i < firstlyShowingPosition ;  i ++){
            _bookmarks.add(bookmarks.get(i));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){

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
        if (e1.getX() - e2.getX() > 0 && currentPosition < adapter.getItemCount()) {
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