package com.mycreat.kiipu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.LoginInfo;
import com.mycreat.kiipu.utils.*;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.SocializeUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Map;

/**
 * Login
 *
 * @author leaderliang
 */
public class LoginActivity extends BaseActivity {

    private final String TAG = getClass().getSimpleName();

    private RelativeLayout mRlSinaAuth;

    private SHARE_MEDIA[] list = {SHARE_MEDIA.SINA};

    public ArrayList<SnsPlatform> platforms = new ArrayList<>();

//    private ProgressDialog dialog;

    private String access_token, userName, uid;

    private boolean isUseClient = false;

    private CoordinatorLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        useBaseLayout = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initData();
        registerKiipuNetReceiver();
    }

    @Override
    protected void initViews() {

        mRlSinaAuth = (RelativeLayout) findViewById(R.id.rl_sina_auth);
        mContainer = initViewById(R.id.container);
//        dialog = new ProgressDialog(this);
        mRlSinaAuth.setOnClickListener(this);
        for (SHARE_MEDIA e : list) {
            if (!e.toString().equals(SHARE_MEDIA.GENERIC.toString())) {
                platforms.add(e.toSnsPlatform());
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        String accessToken = (String) SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN, "");
        String userId = (String) SharedPreferencesUtil.getData(mContext, Constants.USER_ID, "");
        if (!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(userId)) {
            if (!NetChangeReceiver.isNetWorkAvailable(this)) {
                showNetSettingView(mContainer);
                return;
            }
            showLoadingDialog(null);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissLoadingDialog();
                    startActivity(new Intent(mContext, BookMarkActivity.class));
                    finish();
                }
            }, 300);
        }

    }

    @Override
    protected void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.rl_sina_auth:
                UMShareAPI.get(this).doOauthVerify(this, platforms.get(0).mPlatform, authListener);
                break;
        }
    }


    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
//            SocializeUtils.safeShowDialog(dialog);
//            ToastUtil.showToastShort("授权开始");
            showLoadingDialog("正在登录...");
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

//            SocializeUtils.safeCloseDialog(dialog);
//            ToastUtil.showToastShort("授权成功");
            dismissLoadingDialog();
            Log.e(TAG, "onComplete data" + data);
            if (!isUseClient) {
                isUseClient = true;
                access_token = data.get("access_token");
                uid = data.get("uid");
                requestLogin(access_token, uid);
            }

        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
//            SocializeUtils.safeCloseDialog(dialog);
            dismissLoadingDialog();
            ToastUtil.showToastShort("授权失败：" + t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
//            SocializeUtils.safeCloseDialog(dialog);
            dismissLoadingDialog();
            ToastUtil.showToastShort("授权取消");
        }
    };

    /**
     * 执行顺序早于 authListener 实现的方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult " + data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (!isUseClient) {
                isUseClient = true;
                Bundle bundle = data.getExtras();
                access_token = bundle.getString("access_token");
                userName = bundle.getString("userName");
                uid = bundle.getString("uid");
                Log.e(TAG, "access_token" + access_token + "  userName" + userName + "  uid" + uid);
                requestLogin(access_token, uid);
            }
        }
    }

    private void requestLogin(String accessToken, String userId) {
//        SocializeUtils.safeShowDialog(dialog);
        showLoadingDialog(null);
        Call<LoginInfo> call = KiipuApplication.mRetrofitService.loginBookmark(accessToken, userId);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {
                dismissLoadingDialog();
                LoginInfo loginInfo = response.body();
                if (loginInfo != null) {
                    if (!NetChangeReceiver.isNetWorkAvailable(LoginActivity.this)) {
                        showNetSettingView(mContainer);
                        return;
                    }
                    String accessToken = loginInfo.accessToken;
                    String userId = loginInfo.userId;
                    SharedPreferencesUtil.saveData(mContext, Constants.ACCESS_TOKEN, accessToken);
                    SharedPreferencesUtil.saveData(mContext, Constants.USER_ID, userId);
                    Log.e(TAG, "loginInfo userId " + loginInfo.userId + " token " + loginInfo.accessToken);
                    startActivity(new Intent(mContext, BookMarkActivity.class));
                    finish();
                } else {
                    ToastUtil.showToastShort(getString(R.string.login_fail));
                }
            }

            @Override
            public void onFailure(Call<LoginInfo> call, Throwable t) {
                dismissLoadingDialog();
                ToastUtil.showToastShort(t.getMessage());
            }
        });
    }

    @Override
    protected void netStateChanged(boolean state) {
        super.netStateChanged(state);
        if(!state){
            showNetSettingView(mContainer);
        }
    }

}
