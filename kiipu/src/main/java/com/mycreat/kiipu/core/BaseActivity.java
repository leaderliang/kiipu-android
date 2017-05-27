package com.mycreat.kiipu.core;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.LogUtil;
import com.mycreat.kiipu.utils.SharedPreferencesUtil;
import com.mycreat.kiipu.view.RequestErrorLayout;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView title;

    private ImageView back;

    private SparseArray<Object> mViews;

    protected Activity mContext;

    protected ProgressBar mProgress;

    private LinearLayout linearLayout;

    public KiipuApplication mKiipuApplication;

    public String userAccessToken;

    private CoordinatorLayout rootLayout;

    protected Toolbar toolbar;

    protected boolean useBaseLayout = true;

    protected RequestErrorLayout mRequestErrorLayout;

    protected void initViews() {
    }

    protected void initData() {
    }

    protected void initListener() {
    }

    protected abstract void onViewClick(View v);

    protected final String TAG = this.getClass().getSimpleName();

    public BaseActivity() {
        mContext = BaseActivity.this;
    }

    @Override
    public void onClick(View v) {
        onViewClick(v);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //过渡动画  requestFeature() must be called before adding content
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        mKiipuApplication = KiipuApplication.getInstance();
        // 经测试在代码里直接声明透明状态栏更有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 这句很关键，注意是调用父类的方法
        super.setContentView(R.layout.activity_base);
        initToolbar();
        initBaseView();
        mViews = new SparseArray<>();
        getUserAccessToken();
        AppManager.getAppManager().addActivity(this);

    }

    private void initBaseView() {
        mRequestErrorLayout = (RequestErrorLayout) getLayoutInflater().inflate(R.layout.view_empty, null);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        back = (ImageView) findViewById(R.id.img_back);
        title = (TextView) findViewById(R.id.title);
    }


    protected <T extends View> T initViewById(int id) {
        //return返回view时,加上泛型T
        return (T) findViewById(id);
    }

    protected <T extends View> T initViewById(int viewId, int others) {
        T view = (T) mViews.get(viewId);
        if (view == null) {
            view = (T) findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }

    @Override
    public void setContentView(int layoutId) {
        if(useBaseLayout) {
            setContentView(View.inflate(this, layoutId, null));
        }else{
            super.setContentView(layoutId);
        }
    }

    @Override
    public void setContentView(View view) {
        rootLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
        if (rootLayout == null) return;
        rootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
    }

    protected void setTitle(String msg) {
        if (title != null) {
            title.setText(msg);
        }
    }

    /**
     * sometime you want to define back event
     */
    protected void setBackBtn() {
        if (back != null) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            LogUtil.e(TAG, "back is null , please check out");
        }
    }

    protected void setBackClickListener(View.OnClickListener l) {
        if (back != null) {
            back.setVisibility(View.VISIBLE);
            back.setOnClickListener(l);
        } else {
            LogUtil.e(TAG, "back is null ,please check out");
        }

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

    public void getUserAccessToken() {
        userAccessToken = "Bearer " + SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN, "");
    }

}