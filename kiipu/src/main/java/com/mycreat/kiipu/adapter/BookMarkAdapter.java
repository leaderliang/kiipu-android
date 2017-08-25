package com.mycreat.kiipu.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.model.LayoutManagerType;
import com.mycreat.kiipu.utils.BaseViewHolder;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.GlideUtil;
import com.mycreat.kiipu.utils.ImageCallback;
import com.mycreat.kiipu.view.LeftIvTextView;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class BookMarkAdapter extends BaseMultiItemQuickAdapter<Bookmark, BaseViewHolder> {


    private LayoutManagerType mCurrentLayoutManagerType;

    public BookMarkAdapter(List<Bookmark> data) {
        super(data);
        addItemType(Constants.BOOKMARK_TYPE_IMG, R.layout.item_bookmark_default);
        addItemType(Constants.BOOKMARK_TYPE_TEXT, R.layout.item_bookmark_default);
        addItemType(Constants.BOOKMARK_TYPE_WEB, R.layout.item_bookmark_web);

        setDefaultViewTypeLayout(R.layout.item_bookmark_linear_layut_manager);
    }



    @Override
    protected void convert(com.mycreat.kiipu.utils.BaseViewHolder holder, Bookmark bookmark) {
        final BookmarksInfo mBookmarksInfo = bookmark.info;
        if (getmCurrentLayoutManagerType() != null) {
            if (getmCurrentLayoutManagerType().equals(LayoutManagerType.GRID_LAYOUT_MANAGER)) {
                LeftIvTextView mLeftIvTextView = holder.getView(R.id.card_view_title);
                mLeftIvTextView.loadImage(mBookmarksInfo.getIcon());
                mLeftIvTextView.setText(mBookmarksInfo.getTitle() + "");
                switch (holder.getItemViewType()) {
                    case Constants.BOOKMARK_TYPE_IMG:
                        holder.getView(R.id.iv_item_header).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_introduce).setVisibility(View.GONE);
                        holder.setImage(R.id.iv_item_header, new ImageCallback() {
                            @Override
                            public void callback(ImageView imageView) {
                                imageView.setMaxWidth(KiipuApplication.SCREEN_WIDTH);
                                imageView.setMaxHeight((int) (KiipuApplication.SCREEN_WIDTH / (16 / 9f)));
                                GlideUtil.getInstance().loadImage(imageView, mBookmarksInfo.getImg(), true);
                            }
                        });
                        break;
                    case Constants.BOOKMARK_TYPE_TEXT:
                        holder.getView(R.id.iv_item_header).setVisibility(View.GONE);
                        holder.getView(R.id.tv_introduce).setVisibility(View.VISIBLE);
                        holder.setText(R.id.tv_introduce, mBookmarksInfo.getIntroduce() + "");
                        break;
                    case Constants.BOOKMARK_TYPE_WEB:
                        holder.getView(R.id.iv_item_header).setVisibility(View.VISIBLE);
                        holder.getView(R.id.tv_introduce).setVisibility(View.GONE);
                        holder.getView(R.id.ll_item_header).setBackgroundColor(Color.parseColor((TextUtils.isEmpty(bookmark.viewTheme) ? "#ffffff" : "#") + bookmark.viewTheme));
                        holder.setImage(R.id.iv_item_header, new ImageCallback() {
                            @Override
                            public void callback(ImageView imageView) {
                                GlideUtil.getInstance().loadImage(imageView, mBookmarksInfo.getImg(), R.drawable.default_header_icon, false);
                            }
                        });
                        break;
                }
                try {
                    java.net.URL url;
                    url = new java.net.URL(mBookmarksInfo.getUrl());
                    if (url != null) {
                        holder.setText(R.id.tv_url, url.getHost());
                    }
                    holder.addOnClickListener(R.id.img_more_info);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
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
