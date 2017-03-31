package com.mycreat.kiipu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {


    private final Context context;
    private List<Bookmark> mBookmarkList  = new ArrayList<>();
    private LayoutInflater mInflater;
    private BookmarksInfo mBookmarkInfo;

    public RecycleAdapter(Context context, List<Bookmark> list) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mBookmarkList = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recycle_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mBookmarkInfo = mBookmarkList.get(position).getInfo();
        Glide.with(context)
                .load(mBookmarkInfo.getImg())
                .placeholder(R.mipmap.ic_launcher) // 占位图
                .error(R.drawable.error) // 加载失败占位图
                .diskCacheStrategy(DiskCacheStrategy.NONE)// 禁用掉Glide的缓存功能,默认是打开的
                .centerCrop()
                .into(holder.iv_item_header);
        Glide.with(context)
                .load(mBookmarkInfo.getImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.iv_icon);
        holder.tv_right.setText(mBookmarkInfo.getTitle());
        holder.tv_url.setText("www.zhihu.com");
    }


    @Override
    public int getItemCount() {
        return mBookmarkList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_item_header,iv_icon;
        TextView tv_right,tv_url;

        public ViewHolder(View view) {
            super(view);
            iv_item_header = (ImageView) view.findViewById(R.id.iv_item_header);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_right = (TextView) view.findViewById(R.id.tv_title_right);
            tv_url = (TextView) view.findViewById(R.id.tv_url);

        }
    }

}
