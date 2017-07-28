package com.mycreat.kiipu.view.bookmark;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.webkit.WebView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.mycreat.kiipu.model.Bookmark;

/**
 * Created by zhanghaihai on 2017/7/27.
 */
public class BookmarkBindingAdapter {

    @BindingAdapter(value = {"bookmark", "onLinkClick"}, requireAll = false)
    public static void setBookmark(BookmarkTemplateWebVIew view, Bookmark bookmark, BookmarkTemplateWebVIew.OnLinkClickListener onLinkClickListener){
        if(onLinkClickListener != null)
            view.setOnLinkClickListener(onLinkClickListener);
        view.refresh(bookmark);
    }

}
