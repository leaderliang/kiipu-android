package com.mycreat.kiipu.core;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.mycreat.kiipu.BuildConfig;
import com.mycreat.kiipu.retrofit.RetrofitClient;
import com.mycreat.kiipu.retrofit.RetrofitService;
import com.mycreat.kiipu.service.CommonService;
import com.mycreat.kiipu.utils.AppUtils;
import com.mycreat.kiipu.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.common.QueuedWork;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class KiipuApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler{

    /*各个平台的配置，建议放在全局Application或者程序入口*/
    {
        PlatformConfig.setSinaWeibo("3763012369", "70ca7f8be6f87b1157ced83e67d27a48", "http://sns.whalecloud.com");
        /* 配置 qq 时候使用*/
        PlatformConfig.setQQZone("101429483", "f2268e31f622a15084948cec6db9818d");
    }

    public static KiipuApplication instance;

    public static Context appContext;

    public static int SCREEN_WIDTH = -1;

    public static int SCREEN_HEIGHT = -1;

    public static float DIMEN_RATE = -1.0F;

    public static int DIMEN_DPI = -1;

    public static RetrofitService mRetrofitService, mRetrofitTemplateService;


    @Override
    public void onCreate() {
        super.onCreate();
        initBase();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private void initBase() {
        instance = this;
        appContext = getApplicationContext();
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        getScreenSize();
        mRetrofitService = RetrofitClient.getInstance().create(RetrofitService.class);
        mRetrofitTemplateService = RetrofitClient.getTemplateInstance().create(RetrofitService.class);
        startService(new Intent(getApplicationContext(), CommonService.class)); //启动公用的线程

        //添加友盟相关信息
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        String channel = AppUtils.getMetaData(this, "UMENG_CHANNEL");
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(appContext, "5911e1a21c5dd0604e000e04", channel == null ? "UNKNOWN": channel);
        MobclickAgent.startWithConfigure(config);
        MobclickAgent.setDebugMode(Config.DEBUG);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // 解决Dex超出方法数的限制问题,让你的应用不再爆棚
        MultiDex.install(base);
    }


    public void getScreenSize() {
        WindowManager windowManager = (WindowManager) this.getSystemService(appContext.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        DIMEN_RATE = dm.density / 1.0F;
        DIMEN_DPI = dm.densityDpi;
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        if (SCREEN_WIDTH > SCREEN_HEIGHT) {
            int t = SCREEN_HEIGHT;
            SCREEN_HEIGHT = SCREEN_WIDTH;
            SCREEN_WIDTH = t;
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        MobclickAgent.reportError(appContext, ex);
        ex.printStackTrace();
        StackTraceElement[] sts = thread.getStackTrace().clone();
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement st:sts ){
            sb.append(st.getClass().getPackage()).append(".").append(st.getClassName()).append(">").append(st.getMethodName()).append(":").append(st.getLineNumber());
        }
        LogUtil.e(sb.append("\n").append(ex.getMessage()).toString());
    }

}
