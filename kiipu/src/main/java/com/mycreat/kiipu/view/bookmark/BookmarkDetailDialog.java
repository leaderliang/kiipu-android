package com.mycreat.kiipu.view.bookmark;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.databinding.BookmarkDetailDialogBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarkDialog;
import com.mycreat.kiipu.utils.BitmapUtil;
import com.mycreat.kiipu.utils.ColorUtil;
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

public class BookmarkDetailDialog extends DialogFragment{
    @BindView(R.id.recyclerView)
    private RecyclerView recyclerView;
    private int currentPosition;
    private List<Bookmark> _bookmarks = Collections.synchronizedList(new ArrayList<Bookmark>());
    private boolean ifCloseWhenTouchBlank = true;
    private OnCancelListener onCancelListener;
    private BookmarkDetailDialogBinding binding;
    private BookmarkDialog bookmarkDialog;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookmarkDialog = new BookmarkDialog();

        //无title样式
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //无边框
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        //设置背景透明
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ViewUtils.bindViews(view, this);
        bookmarkDialog.setAdapter(new BookmarkDetailAdapter(getActivity(), _bookmarks));
        bookmarkDialog.setBookmarks(_bookmarks);
        bookmarkDialog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bookmarkDialog.setCurrentPosition(currentPosition);

        binding.setBookmarkDialog(bookmarkDialog);
        binding.executePendingBindings();
        PaperLikeRecyclerViewHandler recyclerViewTouchListener = new PaperLikeRecyclerViewHandler(recyclerView, view, bookmarkDialog, binding);
        recyclerView.setOnTouchListener(recyclerViewTouchListener);
        recyclerView.addOnScrollListener(new RecyclerScrollListener(recyclerView, bookmarkDialog.getAdapter()));
        recyclerView.scrollToPosition(bookmarkDialog.getCurrentPosition());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_bookmark_detail, container, false);
        return binding.getRoot();
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
    }
}
