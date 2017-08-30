package com.mycreat.kiipu.model;

import android.databinding.ObservableField;
import android.support.v4.content.ContextCompat;
import android.view.View;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;

/**
 * Created by zhanghaihai on 2017/8/20.
 */
public class BookmarkDialogEndItem {
    public ObservableField<Integer> pbVisibility = new ObservableField<>(View.VISIBLE);
    public ObservableField<Integer> msgVisibility = new ObservableField<>(View.INVISIBLE);
    public ObservableField<String> msg = new ObservableField<>();
}
