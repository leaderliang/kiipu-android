package com.mycreat.kiipu.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.tool.Binding;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.activity.BookMarkActivity;
import com.mycreat.kiipu.databinding.BookmarkDetailBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.CustomTabsUtils;
import com.mycreat.kiipu.utils.ViewUtils;
import com.mycreat.kiipu.view.bookmark.BookmarkTemplateWebVIew;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
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
