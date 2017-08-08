package com.mycreat.kiipu.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.databinding.BookmarkDetailBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.CustomTabsUtils;
import com.mycreat.kiipu.utils.ViewUtils;
import com.mycreat.kiipu.utils.bind.BindView;
import com.mycreat.kiipu.view.bookmark.BookmarkTemplateWebVIew;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 书签详情列表Adapter
 * Created by zhanghaihai on 2017/7/26.
 */
public class BookmarkDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    private List<Bookmark> bookmarks;
    public BookmarkDetailAdapter(Activity activity, List<Bookmark> bookmarks) {
        this.activity = new WeakReference<>(activity).get() ;
        this.bookmarks = bookmarks;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BookmarkDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_card_detail_dialog, parent, false);
        return new BookmarkHolder(binding.getRoot(), binding, activity);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BookmarkHolder){
            ((BookmarkHolder)holder).update(bookmarks.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return bookmarks.size();
    }

    private static class BookmarkHolder extends RecyclerView.ViewHolder{
        private BookmarkDetailBinding mBinding;
        private Activity activity;
        @BindView(R.id.wv_ext_detail)
        BookmarkTemplateWebVIew tmplWebView;
        public BookmarkHolder(View itemView, BookmarkDetailBinding nBinding, Activity activity) {
            this(itemView);
            mBinding = nBinding;
            this.activity = activity;
        }
        public BookmarkHolder(View itemView) {
            super(itemView);
            ViewUtils.bindViews(itemView, this);
        }

        public void update(Bookmark bookmark){
            //处理滑动冲突
            tmplWebView.setOnTouchListener(new View.OnTouchListener() {
                private float oldX;
                private float oldY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //按下事件不拦截
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        tmplWebView.requestDisallowInterceptTouchEvent(false);
                        oldX = event.getX();
                        oldY = event.getY();
                        return false;
                    }else{
                        float distanceX = event.getX() - oldX;
                        float distanceY = event.getY() - oldY;
                        //横向移动大于竖向移动 不拦截
                        if(oldX > oldY) {
                            tmplWebView.requestDisallowInterceptTouchEvent(false);
                            return false;
                        }else{
                            tmplWebView.requestDisallowInterceptTouchEvent(true);
                            return true;
                        }
                    }
                }
            });
            if(mBinding != null){
                mBinding.setBookmark(bookmark);
                mBinding.setOnLinkClickListener(new BookmarkTemplateWebVIew.OnLinkClickListener() {
                    @Override
                    public void onClick(@NotNull String url, @NotNull Bookmark bookmark) {
                        if(activity != null) {
                            String viewTheme = TextUtils.isEmpty(bookmark.viewTheme) ? Constants.DEFAULT_COLOR_VALUE : bookmark.viewTheme;
                            CustomTabsUtils.showCustomTabsView(activity, url, viewTheme);
                        }
                    }
                });
            }
        }
    }
}
