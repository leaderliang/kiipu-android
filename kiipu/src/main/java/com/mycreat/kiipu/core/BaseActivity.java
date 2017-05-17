package com.mycreat.kiipu.core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.SharedPreferencesUtil;

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

    public String userAccessToken;

    protected abstract int getLayoutId();

    protected void initViews(){}

    protected void initData(){}

    protected void initListener(){}

    protected abstract void onViewClick(View v);

    @Override
    public void onClick(View v) {
        onViewClick(v);
    }

    public BaseActivity() {
        mContext = BaseActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViews = new SparseArray<>();
        //过渡动画
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        mKiipuApplication = KiipuApplication.getInstance();
        getUserAccessToken();
        setContentView(getLayoutId());
        initViews();
        initData();
        initListener();

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

    public void showProgressBar(View view) {
        if (mProgress == null || !(mProgress.getVisibility() == View.VISIBLE)) {
            linearLayout = (LinearLayout) view.findViewById(R.layout.view_loading_progress);
            mProgress = (ProgressBar) view.findViewById(R.id.pb_view);
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    public void disProgressBar(View view) {
        if (mProgress != null && mProgress.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void getUserAccessToken(){
         userAccessToken = "Bearer " + SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN, "");
    }

}