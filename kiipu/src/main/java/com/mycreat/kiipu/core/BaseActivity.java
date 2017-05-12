package com.mycreat.kiipu.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    private SparseArray<Object> mViews;

    protected Activity mContext;
    protected ProgressBar mProgress;
    private LinearLayout linearLayout;
    private View contentView;
    private LinearLayout mLinearContent;
    private int layoutId;
    public KiipuApplication mKiipuApplication;

    protected abstract int getLayoutId();

    protected void initViews(){}

    protected void initData(){}

    protected void initListener(){}

    protected abstract void onViewClick(View v);

    @Override
    public void onClick(View v) {
        onViewClick(v);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViews = new SparseArray<>();
        //过渡动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        mKiipuApplication = KiipuApplication.getInstance();
        setContentView(getLayoutId());
        initViews();
        initData();
        initListener();
        mContext = this;
        AppManager.getAppManager().addActivity(this);

    }

    protected <T extends View> T initViewById(int id) {
        //return返回view时,加上泛型T
        return (T) findViewById(id);
    }

    protected <T extends View> T initViewById(int viewId,int others) {
        T view = (T) mViews.get(viewId);
        if (view == null) {
            view = (T) findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    protected <T extends View> void setOnClick(T view) {
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

//    public void showProgressDialog(Context context) {
//        if (mProgress == null || !(mProgress.getVisibility() == View.VISIBLE)) {
//            linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_loding_progress,null);
//            mProgress = (ProgressBar) linearLayout.findViewById(R.id.pb_view);
//            mProgress.setVisibility(View.VISIBLE);
//        }
//    }
//
//    public void disProgressDialog(Context context) {
//        if (mProgress != null && mProgress.getVisibility() == View.VISIBLE) {
//            linearLayout.setVisibility(View.GONE);
//        }
//    }

}