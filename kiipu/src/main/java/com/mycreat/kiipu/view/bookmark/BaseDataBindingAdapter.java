package com.mycreat.kiipu.view.bookmark;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.DrawableRes;
import android.webkit.WebView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.mycreat.kiipu.view.RippleRelativeLayout;

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

    @BindingAdapter(value = {"imageUrl", "placeHolder", "error", "listener"}, requireAll = false)
    public static void setImageUrl(ImageView view, String imageUrl, @DrawableRes int placeHolder, @DrawableRes int error, RequestListener requestListener){
        try {
            Glide.with(view.getContext()).load(imageUrl).listener(requestListener).placeholder(placeHolder).error(error).into(view);
        }catch (Exception e){//RequestListener类型设置不正确可能转换失败
            Glide.with(view.getContext()).load(imageUrl).placeholder(placeHolder).error(error).into(view);
        }
    }

    @BindingAdapter(value = {"changeBackground"})
    public static void changeBackground(RippleRelativeLayout view, int color){
        if(color != 0) {
            view.setBackgroundColor(Color.TRANSPARENT);
            view.changeBackground(color);
        }
    }

}
