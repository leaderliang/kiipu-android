package com.mycreat.kiipu.adapter;

import android.widget.ImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.model.LayoutManagerType;
import com.mycreat.kiipu.utils.BaseViewHolder;
import com.mycreat.kiipu.utils.GlideUtil;
import com.mycreat.kiipu.utils.ImageCallback;
import com.mycreat.kiipu.utils.StringUtils;

import java.util.List;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class BookMarkListAdapter extends BaseQuickAdapter<Bookmark, BaseViewHolder> {


    private LayoutManagerType mCurrentLayoutManagerType;

    public BookMarkListAdapter(List<Bookmark> data) {
        super(R.layout.item_bookmark_linear_layut_manager, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, Bookmark bookmark) {
        final BookmarksInfo mBookmarksInfo = bookmark.info;
        if (getmCurrentLayoutManagerType() != null) {
            if (getmCurrentLayoutManagerType().equals(LayoutManagerType.LINEAR_LAYOUT_MANAGER)) {
                holder.setImage(R.id.iv_icon, new ImageCallback() {
                    @Override
                    public void callback(ImageView imageView) {
                        GlideUtil.getInstance().loadImage(imageView, mBookmarksInfo.icon, R.drawable.default_logo_small,true);
                    }
                });
                holder.setText(R.id.tv_title, StringUtils.dealWithEmptyStr(mBookmarksInfo.title));
                holder.addOnClickListener(R.id.ll_more_info);
            }
        }
    }

    public void setCurrentLayoutManagerType(LayoutManagerType mCurrentLayoutManagerType) {
        this.mCurrentLayoutManagerType = mCurrentLayoutManagerType;
    }

    public LayoutManagerType getmCurrentLayoutManagerType() {
        return mCurrentLayoutManagerType;
    }
}
