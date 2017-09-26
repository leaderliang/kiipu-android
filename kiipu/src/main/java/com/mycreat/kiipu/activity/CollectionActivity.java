package com.mycreat.kiipu.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.CollectionListAdapter;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.Collections;
import com.mycreat.kiipu.utils.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 书签界面/移动到
 *
 * @author leaderliang
 */
public class CollectionActivity extends BaseActivity {

    private List<Collections> mCollectionList = new ArrayList<>();

    private RecyclerView recyclerView;

    private CollectionListAdapter adapter;

    private String bookmarkId, currentCollectionName;

    private int dataPosition;

    private View footer;

    private String inputName;
    private Button finalButton;

    @Override
    protected void onViewClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        initViews();
        registerKiipuNetReceiver();
        initData();
        getCollectionList();
    }

    @Override
    protected void initViews() {
        super.initViews();
        setBaseTitle(getString(R.string.move_to));
        setBackIcon(R.drawable.ic_close);
        setBackBtn();
        setFloatingVisibile(false);
        recyclerView = initViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBaseFloatingActionButton.hide();
    }

    @Override
    protected void initData() {
        super.initData();
        currentCollectionName = getIntent().getStringExtra("currentCollection");
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
        showProgressBar();
        Call<List<Collections>> call = KiipuApplication.mRetrofitService.getCollectionList(userAccessToken);
        call.enqueue(new Callback<List<Collections>>() {
            @Override
            public void onResponse(Call<List<Collections>> call, Response<List<Collections>> response) {
                mCollectionList = response.body();
                if(!CollectionUtils.isEmpty(mCollectionList)) {
                    if (!StringUtils.isEmpty(currentCollectionName)) {
                        for (int i = 0; i < mCollectionList.size(); i++) {
                            if (mCollectionList.get(i).collectionName.equals(currentCollectionName)) {
                                mCollectionList.remove(mCollectionList.get(i));
                                break;
                            }
                        }
                    }
                    adapter.addData(mCollectionList);
                    adapter.setFooterView(getFooterView());

                }else{
                    adapter.setEmptyView(mRequestErrorLayout);
                }
                dismissProgressBar();
            }

            @Override
            public void onFailure(Call<List<Collections>> call, Throwable t) {
                dismissProgressBar();
                Snackbar.make(mBaseFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    private View getFooterView() {
        footer = LayoutInflater.from(CollectionActivity.this).inflate(R.layout.list_item, (ViewGroup) recyclerView.getParent(), false);
        TextView addCollectionsTv = (TextView) footer.findViewById(R.id.tv_collection_name);
        addCollectionsTv.setText(getString(R.string.add_collections));
        footer.findViewById(R.id.iv_icon).setBackgroundResource(R.drawable.ic_add_collection);
        footer.setOnClickListener(new FooterClickListener());
        return footer;
    }

    private class FooterClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            ArrayMap<Object, Object> arrayMap = new ArrayMap<>();
            arrayMap.put("title","名称");
            arrayMap.put("hint","输入你要创建的书签名");
            arrayMap.put("content","");// 为了当输入框没有文本时，按钮置灰
            editDialog(Constants.CREATE_COLLECTION, null ,arrayMap);
        }
    }

    /**
     * move bookmark
     */
    private void requestMoveBookmark(final int collectionPosition) {
        if(TextUtils.isEmpty(bookmarkId)){
            ToastUtil.showToastShort("数据异常，请稍后重试");
            return;
        }
        Call<Bookmark> call = KiipuApplication.mRetrofitService.moveBookmark(userAccessToken, bookmarkId, mCollectionList.get(collectionPosition).collectionId);
         call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
//                requestData.remove(dataPosition);
//                adapter.remove(collectionPosition);
                Intent intent = new Intent();
                intent.putExtra("dataPosition", dataPosition);
                intent.putExtra("collectionName", mCollectionList.get(collectionPosition).collectionName);
                setResult(Constants.RESULT_MOVE_BOOKMARK_CODE, intent);
                finish();
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
                Snackbar.make(mBaseFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
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

    protected void editDialog(final int toDoTag, DialogInterface.OnClickListener NeutralButtonClick, ArrayMap<Object,Object> arrayMap) {
        DialogUtil.showEditDialog(this, Constants.INPUT_MAX_LENGTH,
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        inputName = s.toString().trim();
                        if (StringUtils.isEmpty(inputName)) {
                            if (finalButton != null) {
                                finalButton.setEnabled(false);
                            }
                        } else {
                            if (finalButton != null) {
                                finalButton.setEnabled(true);
                            }
                        }
                    }
                }, new DialogUtil.ButtonCallBack() {
                    @Override
                    public void buttonCallBack(Button btn) {
                        finalButton = btn;
                        if (StringUtils.isEmpty(inputName)) {
                            finalButton.setEnabled(false);
                            return;
                        }
                        finalButton.setEnabled(true);
                    }
                }, NeutralButtonClick
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (toDoTag == Constants.CREATE_COLLECTION) {
                            createCollection(inputName);
                        }
                    }
                }, arrayMap);
    }

    private void createCollection(String collectionName) {
        showProgressBar();
        if (StringUtils.isEmpty(collectionName)) {
            Snackbar.make(mBaseFloatingActionButton, "书签名不能为空~", Snackbar.LENGTH_LONG)
                    .setDuration(2500)
                    .show();
            return;
        }
        Call<Collections> call = KiipuApplication.mRetrofitService.createCollection(userAccessToken, collectionName);
        call.enqueue(new Callback<Collections>() {
            @Override
            public void onResponse(Call<Collections> call, Response<Collections> response) {
                if (response.body() != null) {
                    Collections collection = response.body();
                    adapter.addData(mCollectionList.size(), collection);
                    mCollectionList.add(mCollectionList.size(), collection);
                    Snackbar.make(mBaseFloatingActionButton, "创建书签成功啦~", Snackbar.LENGTH_SHORT)
                            .show();

                    Intent intent = new Intent();
                    intent.putExtra("collection", collection);
                    setResult(Constants.RESULT_ADD_BOOKMARK_CODE, intent);
                } else {
                    Snackbar.make(mBaseFloatingActionButton, "创建书签失败，请稍后重试~", Snackbar.LENGTH_SHORT).show();
                }
                dismissProgressBar();
            }

            @Override
            public void onFailure(Call<Collections> call, Throwable t) {
                Snackbar.make(mBaseFloatingActionButton, "创建书签失败，请稍后重试~" + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    @Override
    protected void netStateChanged(boolean state) {
        if(!state){
            showNetSettingView(mBaseFloatingActionButton);
        }
    }
}
