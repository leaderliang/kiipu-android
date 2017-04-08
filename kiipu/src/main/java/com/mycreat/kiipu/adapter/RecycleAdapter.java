package com.mycreat.kiipu.adapter;

import android.content.Context;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.utils.Constants;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ItemViewHolder> {


    private final Context context;
    private List<Bookmark> mBookmarkList  = new ArrayList<>();
    private List<Bookmark> mRefreshList  = new ArrayList<>();
    private LayoutInflater mInflater;
    private BookmarksInfo mBookmarkInfo;


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

    public String getLastItemId(){
        return mBookmarkList.size() > 0 ? mBookmarkList.get(mBookmarkList.size() - 1).getId() : "";
    }

    public RecycleAdapter(Context context, List<Bookmark> list) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mBookmarkList = list;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recycle_view, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder ItemViewHolder, int position) {
        mBookmarkInfo = mBookmarkList.get(position).getInfo();
        Glide.with(context)
                .load(mBookmarkInfo.getImg())
                .placeholder(R.mipmap.ic_launcher) // 占位图
                .error(R.drawable.error) // 加载失败占位图
                .diskCacheStrategy(DiskCacheStrategy.NONE)// 禁用掉Glide的缓存功能,默认是打开的
                .centerCrop() // 取图片的中间区域
//                .fitCenter()
                .into(ItemViewHolder.iv_item_header);
        Glide.with(context)
                .load(mBookmarkInfo.getIcon())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(ItemViewHolder.iv_icon);
        ItemViewHolder.img_more_info.setTag(position);
        ItemViewHolder.img_more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                showListPopupWindow(v);
            }
        });
        ItemViewHolder.tv_title.setText(mBookmarkInfo.getTitle());
        java.net.URL  url = null;
        try {
            url = new java.net.URL(mBookmarkInfo.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ItemViewHolder.tv_url.setText(url.getHost());
    }


    @Override
    public int getItemCount() {
        return mBookmarkList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_item_header,iv_icon,img_more_info;
        TextView tv_title,tv_url;

        public ItemViewHolder(View view) {
            super(view);
            iv_item_header = (ImageView) view.findViewById(R.id.iv_item_header);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_url = (TextView) view.findViewById(R.id.tv_url);
            img_more_info = (ImageView) view.findViewById(R.id.img_more_info);

        }
    }

    public void showListPopupWindow(View view) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(context);

        // ListView适配器
        listPopupWindow.setAdapter(
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, Constants.ITEMS));

        // 选择item的监听事件
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Toast.makeText(context,  Constants.ITEMS[pos], Toast.LENGTH_SHORT).show();
                 listPopupWindow.dismiss();
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
