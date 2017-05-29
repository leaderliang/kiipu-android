package com.mycreat.kiipu.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.activity.BookMarkActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.utils.ImageCallback;
import com.mycreat.kiipu.view.LeftIvTextView;

import java.net.MalformedURLException;

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
        final BookmarksInfo mBookmarksInfo = bookmark.info;
        if(bookmark.type.equals("1")) {
            holder.getView(R.id.iv_item_header).setVisibility(View.VISIBLE);
            holder.getView(R.id.iv_cover).setVisibility(View.VISIBLE);
            holder.getView(R.id.tv_introduce).setVisibility(View.GONE);
            holder.setImage(R.id.iv_item_header, new ImageCallback() {
                @Override
                public void callback(ImageView imageView) {
                    Glide.with(mContext)
                            .load(mBookmarksInfo.getImg())
                            .placeholder(R.drawable.error) // 占位图
                            .error(R.drawable.error) // 加载失败占位图
                            .diskCacheStrategy(DiskCacheStrategy.NONE)// 禁用掉Glide的缓存功能,默认是打开的
                            .centerCrop() // 取图片的中间区域
//                            .fitCenter()
                            .dontAnimate()//停止动画，解决加载图片变小问题
                            .into(imageView);
                }
            });
        }else{
            holder.getView(R.id.iv_item_header).setVisibility(View.GONE);
            holder.getView(R.id.iv_cover).setVisibility(View.GONE);
            holder.getView(R.id.tv_introduce).setVisibility(View.VISIBLE);
            holder.setText(R.id.tv_introduce,mBookmarksInfo.getIntroduce());
        }

        LeftIvTextView mLeftIvTextView = holder.getView(R.id.card_view_title);
        mLeftIvTextView.loadImage(mBookmarksInfo.getIcon());
        mLeftIvTextView.setText(mBookmarksInfo.getTitle());

        java.net.URL url = null;
        try {
            url = new java.net.URL(mBookmarksInfo.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        holder.setText(R.id.tv_url, url.getHost());
        holder.addOnClickListener(R.id.img_more_info);
    }


}
