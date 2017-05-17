package com.mycreat.kiipu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.model.LoginInfo;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.SharedPreferencesUtil;
import com.mycreat.kiipu.utils.ToastUtil;
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

    private Button mBtSinaAuth;

    private SHARE_MEDIA[] list = {SHARE_MEDIA.SINA};

    public ArrayList<SnsPlatform> platforms = new ArrayList<>();

    private ProgressDialog dialog;

    private String access_token,userName,uid;

    private boolean isUseClient = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initViews() {
        super.initViews();
        mBtSinaAuth = (Button) findViewById(R.id.bt_sina_auth);
        dialog = new ProgressDialog(this);
        mBtSinaAuth.setOnClickListener(this);

        for (SHARE_MEDIA e : list) {
            if (!e.toString().equals(SHARE_MEDIA.GENERIC.toString())) {
                platforms.add(e.toSnsPlatform());
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
        String accessToken = (String) SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN,"");
        String userId = (String) SharedPreferencesUtil.getData(mContext,Constants.USER_ID,"");
        if(!TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(userId)){
            ToastUtil.showToastLong(this,"自动登录中，请稍后...");
            SocializeUtils.safeShowDialog(dialog);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SocializeUtils.safeCloseDialog(dialog);
                    startActivity(new Intent(mContext,BookMarkActivity.class));
                }
            },3000);
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sina_auth:
                UMShareAPI.get(this).doOauthVerify(this, platforms.get(0).mPlatform, authListener);
                break;
        }
    }


    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SocializeUtils.safeShowDialog(dialog);
            Toast.makeText(LoginActivity.this, "授权开始", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            SocializeUtils.safeCloseDialog(dialog);
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onComplete data" + data );
            if(!isUseClient){
                access_token = data.get("access_token");
                uid = data.get("uid");
                requestLogin(access_token, uid);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            SocializeUtils.safeCloseDialog(dialog);
            Toast.makeText(LoginActivity.this, "授权失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            SocializeUtils.safeCloseDialog(dialog);
            Toast.makeText(LoginActivity.this, "授权取消", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 执行顺序早于 authListener 实现的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult " + data );
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            isUseClient = true;
            Bundle bundle = data.getExtras();
            access_token = bundle.getString("access_token");
            userName = bundle.getString("userName");
            uid = bundle.getString("uid");
            Log.e(TAG, "access_token" + access_token + "  userName" + userName + "  uid" + uid);
            requestLogin(access_token, uid);
        }
    }

    private void requestLogin(String accessToken, String userId) {
        SocializeUtils.safeShowDialog(dialog);
        Call<LoginInfo> call = mKiipuApplication.mRetrofitService.loginBookmark(accessToken, userId);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {
                SocializeUtils.safeCloseDialog(dialog);
                LoginInfo loginInfo = response.body();
                String accessToken = loginInfo.accessToken;
                String userId = loginInfo.userId;
                SharedPreferencesUtil.saveData(mContext, Constants.ACCESS_TOKEN,loginInfo.accessToken);
                SharedPreferencesUtil.saveData(mContext,Constants.USER_ID,loginInfo.userId);
                Log.e(TAG, "loginInfo userId " + loginInfo.userId +" token "+loginInfo.accessToken);
                startActivity(new Intent(mContext,BookMarkActivity.class));
            }

            @Override
            public void onFailure(Call<LoginInfo> call, Throwable t) {
                SocializeUtils.safeCloseDialog(dialog);
                ToastUtil.showToastShort(t.getMessage());
            }
        });

    }
}
