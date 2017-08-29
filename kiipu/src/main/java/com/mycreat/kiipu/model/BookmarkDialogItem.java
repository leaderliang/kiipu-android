package com.mycreat.kiipu.model;

import android.databinding.ObservableField;
import android.support.v4.content.ContextCompat;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;

/**
 * Created by zhanghaihai on 2017/8/20.
 */
public class BookmarkDialogItem {
    private Bookmark bookmark;
    RequestListener<? super String, GlideDrawable> glideListener;
    public ObservableField<Integer> vibRantColor = new ObservableField<>(ContextCompat.getColor(KiipuApplication.appContext, R.color.colorPrimary));

    public Bookmark getBookmark() {
        return bookmark;
    }

    public void setBookmark(Bookmark bookmark) {
        this.bookmark = bookmark;
    }

    public RequestListener<? super String, GlideDrawable> getGlideListener() {
        return glideListener;
    }

    public void setGlideListener(RequestListener<? super String, GlideDrawable> glideListener) {
        this.glideListener = glideListener;
    }
}
