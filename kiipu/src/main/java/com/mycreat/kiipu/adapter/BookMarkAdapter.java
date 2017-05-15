package com.mycreat.kiipu.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.activity.BookMarkActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.utils.ImageCallback;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class BookMarkAdapter extends BaseQuickAdapter<Bookmark, com.mycreat.kiipu.utils.BaseViewHolder> {


    private BookMarkActivity mContext;

    public BookMarkAdapter(BookMarkActivity context) {
        super(R.layout.item_recycle_view);
        this.mContext = context;
    }

    @Override
    protected void convert(com.mycreat.kiipu.utils.BaseViewHolder holder, Bookmark bookmark) {
        final BookmarksInfo mBookmarksInfo = bookmark.getInfo();
        holder.setImage(R.id.iv_item_header, new ImageCallback() {
            @Override
            public void callback(ImageView imageView) {
                Glide.with(mContext)
                        .load(mBookmarksInfo.getImg())
                        .placeholder(R.mipmap.ic_launcher) // 占位图
                        .error(R.drawable.error) // 加载失败占位图
//                      .diskCacheStrategy(DiskCacheStrategy.NONE)// 禁用掉Glide的缓存功能,默认是打开的
                        .centerCrop() // 取图片的中间区域
//                      .fitCenter()
                        .into(imageView);
            }
        });

        holder.setImage(R.id.iv_icon, new ImageCallback() {
            @Override
            public void callback(ImageView imageView) {
                Glide.with(mContext)
                        .load(mBookmarksInfo.getImg())
                        .placeholder(R.mipmap.ic_launcher) // 占位图
                        .error(R.drawable.error) // 加载失败占位图
                        .into(imageView);
            }
        });

        holder.addOnClickListener(R.id.img_more_info);
        holder.setText(R.id.tv_title, mBookmarksInfo.getTitle());
        java.net.URL url = null;
        try {
            url = new java.net.URL(mBookmarksInfo.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        holder.setText(R.id.tv_url, url.getHost());
    }


//
//    public String getLastItemId() {
//        return mBookmarkList.size() > 0 ? mBookmarkList.get(mBookmarkList.size() - 1).getId() : "";
//    }


}
