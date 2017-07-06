package com.mycreat.kiipu.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mycreat.kiipu.R;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.retrofit.RetrofitService;
import com.mycreat.kiipu.rxbus.RxBus;
import com.mycreat.kiipu.utils.BindView;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.SharedPreferencesUtil;
import com.mycreat.kiipu.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mycreat.kiipu.core.KiipuApplication.appContext;
import static com.mycreat.kiipu.core.KiipuApplication.mRetrofitService;

/**
 * Created by zhanghaihai on 2017/7/1.
 */

public class BookMarkSwitchPanelAdapter extends RecyclerView.Adapter {
    private List<Bookmark> bookmarks = new ArrayList<>();
    private int TYPE_END = 0;
    private int TYPE_BOOKMARK = 1;
    private String userAccessToken;
    public BookMarkSwitchPanelAdapter(List<Bookmark> bookmarks, int positiion) {
        userAccessToken = "Bearer " + SharedPreferencesUtil.getData(appContext, Constants.ACCESS_TOKEN, "");
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_BOOKMARK;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BookMarkViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  BookMarkViewHolder) {
            if ( position > bookmarks.size() || position == 0) {
                ((BookMarkViewHolder) holder).onLoading();
                if(bookmarks.size() >= position){
                    loadMore(1);
                }else{
                    loadMore(-1);
                }
            }else{
                Bookmark bookmark = bookmarks.get(position - 1);
                ((BookMarkViewHolder) holder).update(bookmark, position);
            }
        }
    }


    private void loadMore(int direct) {
//        if(direct > 0) {
//            Call<List<Bookmark>> call = mRetrofitService.getBookmarkList(userAccessToken, Constants.PAGE_SIZE, mStartId, Constants.ALL_COLLECTION);
//            call.enqueue(new Callback<List<Bookmark>>() {
//                @Override
//                public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
//                    //RxBus.Companion.get().post();
//                }
//
//                @Override
//                public void onFailure(Call<List<Bookmark>> call, Throwable t) {
//
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return bookmarks.size() + 1;
    }

    public void addBookMark(Bookmark bookmark){
        bookmarks.add(bookmark);
    }

    private static class BookMarkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.bd_bar_close_btn)
        ImageButton closeBtn;
        @BindView(R.id.bd_bar_options_btn)
        ImageButton optionsBtn;
        @BindView(R.id.bd_bar_title)
        TextView title;
        BookMarkViewHolder(View itemView) {
            super(itemView);
            ViewUtils.bindViews(itemView, this);
        }

        public void update(Bookmark bookmark, int position){
            closeBtn.setOnClickListener(this);
        }

        public void onLoading(){

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){

            }
        }
    }

}
