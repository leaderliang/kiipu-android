package com.mycreat.kiipu.utils;

import android.support.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;

/**
 * @author leaderliang
 */
public class BaseViewHolder extends com.chad.library.adapter.base.BaseViewHolder{

    public BaseViewHolder(View view) {
        super(view);
    }

    public BaseViewHolder setImage(@IdRes int imageId, ImageCallback callback){
        ImageView imageView = getView(imageId);
        if (imageView != null && callback != null){
            callback.callback(imageView);
        }
        return this;
    }
}
