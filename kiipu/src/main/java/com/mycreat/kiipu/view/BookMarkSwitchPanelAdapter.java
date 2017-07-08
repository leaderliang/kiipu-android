package com.mycreat.kiipu.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.bind.BindView;
import com.mycreat.kiipu.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhanghaihai on 2017/7/1.
 */

public class BookMarkSwitchPanelAdapter extends RecyclerView.Adapter {
    private List<Object> data = new ArrayList<>();
    private int TYPE_END = 0;
    private int TYPE_BOOKMARK = 1;
    private String userAccessToken;
    private Lock dataOperateLock = new ReentrantLock();
    public BookMarkSwitchPanelAdapter(List<Bookmark> bookmarks, int positiion) {
//        userAccessToken = "Bearer " + SharedPreferencesUtil.getData(appContext, Constants.ACCESS_TOKEN, "");
        data.addAll(bookmarks);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_BOOKMARK;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookMarkViewHolder(inflateItem(R.layout.view_card_detail_dialog, parent));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  BookMarkViewHolder) {
            if ( position > data.size() || position == 0) {
                ((BookMarkViewHolder) holder).onLoading();
                if(data.size() >= position){
                    loadMore(1);
                }else{
                    loadMore(-1);
                }
            }else{
                Object obj;
                if((obj = data.get(position - 1)) instanceof Bookmark) {
                    Bookmark bookmark = (Bookmark) obj;
                    ((BookMarkViewHolder) holder).update(bookmark);
                }
            }
        }
    }


    private void loadMore(int direct) {

    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    public void addBookMark(Bookmark bookmark){
        dataOperateLock.lock();
        data.add(bookmark);
        notifyDataSetChanged();
        dataOperateLock.lock();
    }

    public void addBookMark(List<Bookmark> bookmarks){
        dataOperateLock.lock();
        for(Bookmark bookmark: bookmarks) {
            data.add(bookmark);
        }
        notifyDataSetChanged();
        dataOperateLock.lock();
    }

    public void updateItem(Bookmark bookmark, int position){
        dataOperateLock.lock();
        if(data.size() > position) {
            data.remove(position);
            data.add(position, bookmark);
            notifyItemChanged(position);
        }
        dataOperateLock.unlock();
    }

    public void clear(){
        dataOperateLock.lock();
        data.clear();
        notifyDataSetChanged();
        dataOperateLock.lock();
    }

    public void remove(int position){
        dataOperateLock.lock();
        if(data.size() > position){
            data.remove(position);
        }
        notifyDataSetChanged();
        dataOperateLock.lock();
    }

    private static class BookMarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_icon)
        ImageView mIvIcon;
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_url)
        TextView mTvUrl;
        @BindView(R.id.iv_detail)
        ImageView mIvDetail;
        @BindView(R.id.tv_introduce)
        TextView mTvIntroduce;
        Bookmark bookmark;

        BookMarkViewHolder(View itemView) {
            super(itemView);
            ViewUtils.bindViews(itemView, this);
        }

        void update(Bookmark bookmark){
//            GlideUtil.getInstance().loadImage(mIvIcon, bookmark.info.getIcon(), R.drawable.default_logo_small,true);
//            mTvTitle.setText(bookmark.info.getTitle());
//            mTvUrl.setText(bookmark.info.getUrl());
//            if (bookmark.type.equals("1")) {
//                mIvDetail.setVisibility(View.VISIBLE);
//                mTvIntroduce.setVisibility(View.GONE);
//                GlideUtil.getInstance().loadImage(mIvDetail, bookmark.info.getImg(), true);
//            } else {
//                mIvDetail.setVisibility(View.GONE);
//                mTvIntroduce.setVisibility(View.VISIBLE);
//                mTvIntroduce.setText(bookmark.info.getIntroduce());
//            }
        }

        void onLoading(){

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){

            }
        }
    }

    private View inflateItem(int layout, ViewGroup parent){
        return LayoutInflater.from(KiipuApplication.appContext).inflate(layout, parent,
                false);
    }
}
