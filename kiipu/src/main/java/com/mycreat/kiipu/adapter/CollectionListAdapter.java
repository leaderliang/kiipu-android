package com.mycreat.kiipu.adapter;

import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.activity.BookMarkActivity;
import com.mycreat.kiipu.activity.CollectionActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Collections;
import com.mycreat.kiipu.utils.GlideUtil;
import com.mycreat.kiipu.utils.ImageCallback;

/**
 * Created by leaderliang on 2017/5/26.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class CollectionListAdapter extends BaseQuickAdapter<Collections, com.mycreat.kiipu.utils.BaseViewHolder> {


    private CollectionActivity mContext;

    public CollectionListAdapter(CollectionActivity context) {
        super(R.layout.list_item);
        this.mContext = context;
    }

    @Override
    protected void convert(com.mycreat.kiipu.utils.BaseViewHolder holder, final Collections collections) {
        holder.setText(R.id.tv_collection_name, collections.collectionName);
        holder.setImage(R.id.iv_icon, new ImageCallback() {
            @Override
            public void callback(ImageView imageView) {
                GlideUtil.getInstance().loadImage(imageView, collections.menuIcon, R.drawable.default_logo_small, true);
            }
        });
    }


}
