package com.mycreat.kiipu.view.bookmark;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.webkit.WebView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

/**
 * Created by zhanghaihai on 2017/7/27.
 */
public class BaseDataBindingAdapter {

    @BindingAdapter("url")
    public static void setLoadUrl(WebView view, String url){
        view.loadUrl(url);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    @BindingAdapter(value = {"imageUrl", "placeHolder", "error"}, requireAll = false)
    public static void setImageUrl(ImageView view, String imageUrl, Drawable placeHolder, Drawable error){
        Glide.with(view.getContext()).load(imageUrl).placeholder(placeHolder).error(error).into(view);
    }
}
