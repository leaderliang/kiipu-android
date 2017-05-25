package com.mycreat.kiipu.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.activity.BookMarkActivity;
import com.mycreat.kiipu.model.Collections;

/**
 * Created by leaderliang on 2017/5/26.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class CollectionListAdapter extends BaseQuickAdapter<Collections, com.mycreat.kiipu.utils.BaseViewHolder> {


    private BookMarkActivity mContext;

    public CollectionListAdapter(BookMarkActivity context) {
        super(R.layout.list_item);
        this.mContext = context;
    }

    @Override
    protected void convert(com.mycreat.kiipu.utils.BaseViewHolder holder, Collections collections) {
        holder.setText(R.id.tv_collection_name, collections.collectionName);
    }


}
