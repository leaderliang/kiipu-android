package com.mycreat.kiipu.view.bookmark;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.databinding.BookmarkDetailDialogBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarkDialog;
import com.mycreat.kiipu.model.rxbus.LoadMoreEvent;
import com.mycreat.kiipu.rxbus.RxBus;
import com.mycreat.kiipu.rxbus.RxBusSubscribe;
import com.mycreat.kiipu.rxbus.ThreadMode;
import com.mycreat.kiipu.utils.DensityUtils;
import com.mycreat.kiipu.utils.ViewUtils;
import com.mycreat.kiipu.utils.bind.BindOnclick;
import com.mycreat.kiipu.utils.bind.BindView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于展示书签详情，可左右切换书签
 * Created by zhanghaihai on 2017/6/28.
 */

public class BookmarkDetailDialog extends DialogFragment implements PaperLikeRecyclerViewHandler.DataResolver, View.OnClickListener {
    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @BindOnclick
    @BindView(R.id.dialog_bg)
    public RelativeLayout dialogBg;

    private int currentPosition;
    private boolean ifCloseWhenTouchBlank = true;
    private OnCancelListener onCancelListener;
    private BookmarkDetailDialogBinding binding;
    private BookmarkDialog bookmarkDialog;
    private BookmarkDetailAdapter adapter;

    public BookmarkDetailDialog(){
        bookmarkDialog = new BookmarkDialog();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP) {
            getDialog().getWindow().setStatusBarColor(R.color.colorPrimary);
        }

        //无title样式
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //无边框
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setWindowAnimations(R.style.DF_NO_PADDING_TRANSPARENT);

        ViewUtils.bindViews(view, this);
        adapter = new BookmarkDetailAdapter(getActivity(), recyclerView);
        bookmarkDialog.setAdapter(adapter);
        bookmarkDialog.setLayoutManager(new CustomLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false){});
        bookmarkDialog.setCurrentPosition(currentPosition);

        binding.setBookmarkDialog(bookmarkDialog);
        binding.executePendingBindings();
        PagerSnapHelper paperSnapHelper=  new PagerSnapHelper();
        paperSnapHelper.attachToRecyclerView(recyclerView);
        recyclerView.scrollToPosition(currentPosition);
        final int[] lastVisiblePosition = {-1};
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.getAdapter().getItemCount() > 0 && (lastVisiblePosition[0] = getLastVisiblePosition()) == recyclerView.getAdapter().getItemCount() -1 ){
                    onLoadMore(lastVisiblePosition[0]);
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_bookmark_detail, container, false);
        return binding.getRoot();
    }

    public void show(FragmentManager manager, String tag, int firstlyShowingPosition, List<Bookmark> bookmarks) {
        RxBus.Companion.getDefault().register(this);
        bookmarkDialog.bookmarks.set(bookmarks);
        currentPosition = firstlyShowingPosition;
        super.show(manager, tag);

        if(adapter != null)
            adapter.initLoad();

    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DF_NO_PADDING_TRANSPARENT){

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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.dialog_bg:
                dismiss();
                break;
        }
    }


    public interface OnCancelListener{
        public void onCancel(BookmarkDetailDialog customDialogFragment);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //如果Activity 实现了取消接口
        if(context instanceof OnCancelListener){
            this.onCancelListener = (OnCancelListener) context;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        RxBus.Companion.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.Companion.getDefault().unregister(this);
    }

    @RxBusSubscribe(mode = ThreadMode.MAIN)
    public void onEventDialogEvent(DialogEvent  event){
        if(event.action == 0){
            dismiss();
        }
    }

    @RxBusSubscribe(mode = ThreadMode.MAIN)
    public void onEventLoadMore(LoadMoreEvent event){
        switch (event.action){

            case 1:
                bookmarkDialog.bookmarks.set(event.bookmarks);
                adapter.notifyDataSetChanged();
                isLoadingMore = false;
                break;
            case 2:
                recyclerView.getAdapter().notifyDataSetChanged();
                isLoadingMore = false;
                adapter.noMore();
                break;
        }
    }

    static class DialogEvent{
        int action = 0;
    }

    @Override
    public int getLastVisiblePosition() {
        if(bookmarkDialog != null && bookmarkDialog.getLayoutManager() != null){
            if(bookmarkDialog.getLayoutManager() instanceof LinearLayoutManager){
                return ((LinearLayoutManager)bookmarkDialog.getLayoutManager()).findLastVisibleItemPosition();
            }else if(bookmarkDialog.getLayoutManager() instanceof GridLayoutManager){
                return ((GridLayoutManager)bookmarkDialog.getLayoutManager()).findLastVisibleItemPosition();
            }
        }
        return -1;
    }

    @Override
    public int getFirstVisiblePosition() {
        if(bookmarkDialog != null && bookmarkDialog.getLayoutManager() != null){
            if(bookmarkDialog.getLayoutManager() instanceof LinearLayoutManager){
                return ((LinearLayoutManager)bookmarkDialog.getLayoutManager()).findFirstVisibleItemPosition();
            }else if(bookmarkDialog.getLayoutManager() instanceof GridLayoutManager){
                return ((GridLayoutManager)bookmarkDialog.getLayoutManager()).findFirstVisibleItemPosition();
            }
        }
        return -1;
    }
    private boolean isLoadingMore = false;

    @Override
    public void onLoadMore(int position) {
        if(!isLoadingMore){
            isLoadingMore = true;
            new LoadMoreEvent(0, null).post();
        }
    }

    @Override
    public void onRefresh() {

    }

    class CustomLinearLayoutManager extends LinearLayoutManager{
        boolean canScrollHorizontally = true;
        public CustomLinearLayoutManager(Context context) {
            super(context);
        }

        public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean canScrollHorizontally() {
            return canScrollHorizontally && super.canScrollHorizontally();
        }

        public void setCanScrollHorizontally(boolean canScrollHorizontally) {
            this.canScrollHorizontally = canScrollHorizontally;
        }
    }

}
