package com.mycreat.kiipu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mycreat.kiipu.R;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {


    private final Context context;
    private LayoutInflater mInflater;
    private String[] mTitles = null;

    public RecycleAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);

        this.mTitles = new String[222];
        for (int i = 0; i < 222; i++) {
            int index = i + 1;
            mTitles[i] = "recycleView " + index;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_recycle_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item_tv.setText(mTitles[position]);
    }


    @Override
    public int getItemCount() {
        return mTitles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_tv;

        public ViewHolder(View view) {
            super(view);
            item_tv = (TextView) view.findViewById(R.id.item_recycle_tv);
        }
    }

}
