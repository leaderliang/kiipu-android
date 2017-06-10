package com.mycreat.kiipu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.CollectionListAdapter;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.Collections;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.ToastUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends BaseActivity {

    private List<Collections> mCollectionList = new ArrayList<>();

    private RecyclerView recyclerView;

    private CollectionListAdapter adapter;

    private String bookmarkId;

    private int dataPosition;

    @Override
    protected void onViewClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initViews();
        initData();
        getCollectionList();
    }

    @Override
    protected void initViews() {
        super.initViews();
        setBaseTitle("移动到");
        setBackBtn();
        setFloatingVisibile(false);
        recyclerView = initViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFloatingActionButton.hide();
    }

    @Override
    protected void initData() {
        super.initData();
        bookmarkId = getIntent().getStringExtra("currentBookmarkId");
        dataPosition = getIntent().getIntExtra("dataPosition",0);
        adapter = new CollectionListAdapter(CollectionActivity.this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new onRecyclerViewItemClick());
    }

    /**
     * 获取收藏夹列表
     */
    private void getCollectionList() {
        Call<List<Collections>> call = mKiipuApplication.mRetrofitService.getCollectionList(userAccessToken);
        call.enqueue(new Callback<List<Collections>>() {
            @Override
            public void onResponse(Call<List<Collections>> call, Response<List<Collections>> response) {
                mCollectionList = response.body();
                adapter.addData(mCollectionList);
            }

            @Override
            public void onFailure(Call<List<Collections>> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    /**
     * move bookmark
     */
    private void requestMoveBookmark(final int collectionPosition) {
        if(TextUtils.isEmpty(bookmarkId)){
            ToastUtil.showToastShort("数据异常，请稍后重试");
            return;
        }
        Call<Bookmark> call = mKiipuApplication.mRetrofitService.moveBookmark(userAccessToken, bookmarkId, mCollectionList.get(collectionPosition).collectionId);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
//                requestData.remove(dataPosition);
//                adapter.remove(collectionPosition);
                Intent intent = new Intent();
                intent.putExtra("dataPosition",dataPosition);
                intent.putExtra("collectionName", mCollectionList.get(collectionPosition).collectionName);
                setResult(Constants.RESULT_MOVE_BOOKMARK_CODE, intent);
                finish();
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    private class onRecyclerViewItemClick implements BaseQuickAdapter.OnItemClickListener {
        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            requestMoveBookmark(position);
        }
    }
}
