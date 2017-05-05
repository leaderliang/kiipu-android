package com.mycreat.kiipu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.activity.BookMarkInfoActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.view.CustomViewClick;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ItemViewHolder> {


    private final Activity mContext;
    private List<Bookmark> mBookmarkList = new ArrayList<>();
    private List<Bookmark> mRefreshList = new ArrayList<>();
    private LayoutInflater mInflater;
    private BookmarksInfo mBookmarkInfo;
    public View moreView;

    private RecyclerViewItemOnClick mRecyclerViewItemOnClick;
    private View view;

    public void setOnRecyclerItemClick(RecyclerViewItemOnClick mRecyViewItemOnClick) {
        this.mRecyclerViewItemOnClick = mRecyViewItemOnClick;
    }

    public interface RecyclerViewItemOnClick {
        void onItemOnclick(View view, int index);
    }

    public void addItem(List<Bookmark> list) {
        mBookmarkList.clear();
        mBookmarkList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<Bookmark> list) {
        mRefreshList.clear();
        mRefreshList.addAll(list);
        mBookmarkList.addAll(mRefreshList);
        notifyDataSetChanged();
    }

    public String getLastItemId() {
        return mBookmarkList.size() > 0 ? mBookmarkList.get(mBookmarkList.size() - 1).getId() : "";
    }

    public RecycleAdapter(Activity context, List<Bookmark> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mBookmarkList = list;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.item_recycle_view, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view, mRecyclerViewItemOnClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder mItemViewHolder, int position) {
        mBookmarkInfo = mBookmarkList.get(position).getInfo();
        view.setTag(position);
         
        Glide.with(mContext)
                .load(mBookmarkInfo.getImg())
                .placeholder(R.mipmap.ic_launcher) // 占位图
                .error(R.drawable.error) // 加载失败占位图
//                .diskCacheStrategy(DiskCacheStrategy.NONE)// 禁用掉Glide的缓存功能,默认是打开的
                .centerCrop() // 取图片的中间区域
//                .fitCenter()
                .into(mItemViewHolder.iv_item_header);
        Glide.with(mContext)
                .load(mBookmarkInfo.getIcon())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.error)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mItemViewHolder.iv_icon);
        mItemViewHolder.img_more_info.setTag(position);
        mItemViewHolder.img_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int position = (int) v.getTag();
                showListPopupWindow(v);

            }
        });
        mItemViewHolder.tv_title.setText(mBookmarkInfo.getTitle());
        java.net.URL url = null;
        try {
            url = new java.net.URL(mBookmarkInfo.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mItemViewHolder.tv_url.setText(url.getHost());
    }


    @Override
    public int getItemCount() {
        return mBookmarkList.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecyclerViewItemOnClick mRecyViewItemOnClick;
        ImageView iv_item_header, iv_icon, img_more_info;
        TextView tv_title, tv_url;

        public ItemViewHolder(View view, final RecyclerViewItemOnClick mListener) {
            super(view);
            iv_item_header = (ImageView) view.findViewById(R.id.iv_item_header);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_url = (TextView) view.findViewById(R.id.tv_url);
            img_more_info = (ImageView) view.findViewById(R.id.img_more_info);
            this.mRecyViewItemOnClick = mListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mRecyViewItemOnClick.onItemOnclick(v, getAdapterPosition());
        }
    }

    public void showListPopupWindow(View view) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);

        // ListView适配器
        listPopupWindow.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, Constants.ITEMS));

        // 选择item的监听事件
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int pos, long id) {
//                Toast.makeText(mContext, Constants.ITEMS[pos], Toast.LENGTH_SHORT).show();
                listPopupWindow.dismiss();
                Toast.makeText(mContext, "getTop " + parent.getTop(), Toast.LENGTH_SHORT).show();

//                moreView
//                int viewMarginTop = getTop + mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_default_height_material);
//                Toast.makeText(mContext, "getTop "+getTop+" viewMarginTop "+viewMarginTop, Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(mContext, BookMarkInfoActivity.class);
//                intent.putExtra("viewMarginTop", viewMarginTop);
//                mContext.startActivity(intent);
//                mContext.overridePendingTransition(0, 0);
            }
        });

        // 对话框的宽高
        listPopupWindow.setWidth(500);
        listPopupWindow.setHeight(600);

        // ListPopupWindow 相对的View
        listPopupWindow.setAnchorView(view);

        // ListPopupWindow 相对按钮横向 和纵向 的距离
        listPopupWindow.setHorizontalOffset(50);
        listPopupWindow.setVerticalOffset(1);

        //  Set whether this window should be modal when shown.
        // If a popup window is modal, it will receive all touch and key input. If the user touches outside the popup window's content area the popup window will be dismissed.
        // modal boolean: true if the popup window should be modal, false otherwise.
        listPopupWindow.setModal(false);

        listPopupWindow.show();
    }

}
