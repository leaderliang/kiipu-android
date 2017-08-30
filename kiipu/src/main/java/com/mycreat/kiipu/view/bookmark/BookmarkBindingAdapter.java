package com.mycreat.kiipu.view.bookmark;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.mycreat.kiipu.model.Bookmark;

import java.util.List;

/**
 * 书签特定binding
 * Created by zhanghaihai on 2017/7/27.
 */
public class BookmarkBindingAdapter {

    @BindingAdapter(value = {"bookmark", "onLinkClick"}, requireAll = false)
    public static void setBookmark(BookmarkTemplateWebVIew view, Bookmark bookmark, BookmarkTemplateWebVIew.OnLinkClickListener onLinkClickListener){
        if(onLinkClickListener != null)
            view.setOnLinkClickListener(onLinkClickListener);
        view.refresh(bookmark);
    }

    @BindingAdapter(value = {"addBookmarks", "adapter"})
    public static void setBookmarks(RecyclerView view, List<Bookmark> addBookmarks, BookmarkDetailAdapter adapter){
        if(view.getAdapter() == null || view.getAdapter() != adapter){
            view.setAdapter(adapter);
        }
        adapter.addBookMarks(addBookmarks);
        adapter.notifyDataSetChanged();
    }

}
