package com.mycreat.kiipu.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.*;
import android.widget.*;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.utils.*;
import com.mycreat.kiipu.view.RequestErrorLayout;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.utils.Log;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, NetChangeReceiver.CallBackState {

    private static Dialog loadingDialog;

    protected TextView baseTitle;

    private ImageView mImgBack;

    private SparseArray<Object> mViews;

    protected Activity mContext;

//    protected ProgressBar mProgress;

//    private LinearLayout linearLayout;

    public String userAccessToken;

    private LinearLayout includeLayout;
    /*用了自定义布局，且设置了 getSupportActionBar().setDisplayHomeAsUpEnabled(false); getSupportActionBar().setDisplayShowTitleEnabled(false); 则直接拿 toolbar 对象设置不起作用*/
    protected Toolbar toolbar;

    protected boolean useBaseLayout = true;

    protected RequestErrorLayout mRequestErrorLayout;

    protected FloatingActionButton mBaseFloatingActionButton;

    protected ProgressBar mProgressView;

    private NetChangeReceiver mNetChangeReceiver;

    protected boolean kiipuNetState;

    protected void initViews() {
    }

    protected void initData() {
    }

    protected void initListener() {
    }

    protected void netStateChanged(boolean state) {
    }

    protected abstract void onViewClick(View v);

    protected final String TAG = this.getClass().getSimpleName();

    public BaseActivity() {
        mContext = BaseActivity.this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*过渡动画  requestFeature() must be called before adding content*/
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // 经测试在代码里直接声明透明状态栏更有效
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
        // 这句很关键，注意是调用父类的方法
        super.setContentView(R.layout.activity_base);
        initToolbar();
        initBaseView();
        mViews = new SparseArray<>();
        getUserAccessToken();
        AppManager.getAppManager().addActivity(this);

    }

    private void initBaseView() {
        mBaseFloatingActionButton = initViewById(R.id.floating_action_bt);
        mProgressView = initViewById(R.id.pb_view);
        setOnClick(mBaseFloatingActionButton);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mImgBack = (ImageView) findViewById(R.id.img_back);
        baseTitle = (TextView) findViewById(R.id.title);
    }

    /**
     * scroll，enterAlways，enterAlwaysCollapsed，snap，exitUntilCollapsed
     * 代码样式 params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
     *
     * @param flags
     */
    protected void setToolbarScrollFlags(int flags) {
        if (toolbar == null) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        AppBarLayout.LayoutParams params =
                (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(flags);
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
        if (useBaseLayout) {
            setContentView(View.inflate(this, layoutId, null));
        } else {
            super.setContentView(layoutId);
        }
    }

    @Override
    public void setContentView(View view) {
        includeLayout = (LinearLayout) findViewById(R.id.include_layout);
        if (includeLayout == null) return;
        includeLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
    }

    protected void setBaseTitle(String msg) {
        if (baseTitle != null) {
            baseTitle.setText(msg);
        }
    }

    protected void setBaseTitle(int msg) {
        if (baseTitle != null) {
            baseTitle.setText(msg);
        }
    }

    /**
     * sometime you want to define back event
     */
    protected void setBackBtn() {
        if (mImgBack != null) {
            mImgBack.setVisibility(View.VISIBLE);
            mImgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            LogUtil.e(TAG, "back img is null , please check out");
        }
    }

    protected void setBackIcon(int resId) {
        if (mImgBack != null) {
            mImgBack.setImageResource(resId);
        } else {
            LogUtil.e(TAG, "back img is null , please check out");
        }
    }

    protected void showProgressBar() {
        if (mProgressView != null) {
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            LogUtil.e(TAG, "ProgressBar is null , please check out");
        }
    }

    protected void dismissProgressBar() {
        if (mProgressView != null) {
            mProgressView.setVisibility(View.GONE);
        } else {
            LogUtil.e(TAG, "ProgressBar is null , please check out");
        }
    }

    public Dialog createLoadingDialog(String str) {
        LayoutInflater inflater = LayoutInflater.from(KiipuApplication.appContext);
        View v = inflater.inflate(R.layout.view_loading_dialog, null);// 得到加载view
        TextView loadingContent = (TextView) v.findViewById(R.id.loading_content);
        Dialog loadingDialog = new Dialog(mContext, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(v);// 设置布局
        /**
         * 设置Dialog的宽度为屏幕宽度的61.8%，高度为自适应
         */
        WindowManager.LayoutParams lp = loadingDialog.getWindow().getAttributes();
        lp.width = (int) (DensityUtils.getScreenWidth(mContext) * 0.7f);
        lp.height = lp.WRAP_CONTENT;
        loadingDialog.getWindow().setAttributes(lp);
        if(!StringUtils.isEmpty(str)){
            loadingContent.setText(str);
        }
        return loadingDialog;
    }

    protected void showLoadingDialog(final String loadingContent){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(loadingDialog == null){
                    loadingDialog = createLoadingDialog(loadingContent);
                }
                try {
                    if(loadingDialog != null && !loadingDialog.isShowing()) {
                        loadingDialog.show();
                    }
                } catch (Exception e) {
                    LogUtil.e("showLoadingDialog", "dialog show error "+ e.getMessage());
                }
            }
        });
    }

    protected static void dismissLoadingDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
    }


    protected void setBackClickListener(View.OnClickListener l) {
        if (mImgBack != null) {
            mImgBack.setVisibility(View.VISIBLE);
            mImgBack.setOnClickListener(l);
        } else {
            LogUtil.e(TAG, "back img is null ,please check out");
        }

    }

    protected View getEmptyView() {
        if (mRequestErrorLayout == null) {
            mRequestErrorLayout = (RequestErrorLayout) getLayoutInflater().inflate(R.layout.view_empty, null);
        } else {
            if (mRequestErrorLayout.getParent() != null) {
                ((ViewGroup) mRequestErrorLayout.getParent()).removeAllViews();
            }
        }
        return mRequestErrorLayout;
    }

    protected void setFloatingVisibile(boolean isVisible) {
//        mBaseFloatingActionButton.hide(isVisible);
//        if(isVisible){
//            // 在 baseLayout 上展示
//            mBaseFloatingActionButton.hide(false);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mBaseFloatingActionButton.show(true);
//                    mBaseFloatingActionButton.setShowAnimation(AnimationUtils.loadAnimation(BaseActivity.this, R.anim.show_from_bottom));
//                    mBaseFloatingActionButton.setHideAnimation(AnimationUtils.loadAnimation(BaseActivity.this, R.anim.hide_to_bottom));
//                }
//            }, 300);
//        }
    }

    protected <T extends View> void setOnClick(T view) {
        view.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetChangeReceiver != null) {
            unregisterReceiver(mNetChangeReceiver);
        }
        AppManager.getAppManager().finishActivity(this);
    }

    public void getUserAccessToken() {
        userAccessToken = "Bearer " + SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN, "");
    }

    /**
     * register net receiver
     */
    protected void registerKiipuNetReceiver() {
        // net callback
        if (mNetChangeReceiver == null) {
            mNetChangeReceiver = new NetChangeReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mNetChangeReceiver, filter);
            mNetChangeReceiver.setNetCallBackListener(BaseActivity.this);
        }
    }

    @Override
    public void getNetState(boolean state) {
        this.kiipuNetState = state;
        netStateChanged(kiipuNetState);
        if (!kiipuNetState && useBaseLayout) {
            showNetSettingView(mBaseFloatingActionButton);
        }
    }

    protected void showNetSettingView(View view) {
        Snackbar.make(view, "网络连接不可用", Snackbar.LENGTH_LONG)
                .setAction("设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                })
                .setActionTextColor(Color.parseColor("#FFB74D"))
                .show();
    }

    @Override
    public void onClick(View v) {
        onViewClick(v);
        switch (v.getId()) {
            case R.id.floating_action_bt:
                Snackbar.make(mBaseFloatingActionButton, "Replace with your own action", Snackbar.LENGTH_LONG).setDuration(2000).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}