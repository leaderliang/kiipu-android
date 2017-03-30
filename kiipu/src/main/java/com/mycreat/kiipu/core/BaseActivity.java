package com.mycreat.kiipu.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private SparseArray<Object> mViews;

    protected Activity mContext;

    public abstract int getLayoutId();

    public abstract void initViews();

    public abstract void initListener();

    public abstract void initData();

    public abstract void onViewClick(View v);

    @Override
    public void onClick(View v) {
        onViewClick(v);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViews = new SparseArray<>();
        setContentView(getLayoutId());
        initViews();
        initListener();
        initData();
        mContext = this;
        AppManager.getAppManager().addActivity(this);

    }

    protected <T extends View> T initViewById(int viewId) {
        T view = (T) mViews.get(viewId);
        if (view == null) {
            view = (T) findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    public <T extends View> void setOnClick(T view) {
        view.setOnClickListener(this);
    }

    protected void setToolBar(Toolbar toolbar, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }
}