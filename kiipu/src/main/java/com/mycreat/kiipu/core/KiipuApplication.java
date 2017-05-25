package com.mycreat.kiipu.core;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.mycreat.kiipu.retrofit.RetrofitClient;
import com.mycreat.kiipu.retrofit.RetrofitService;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.SharedPreferencesUtil;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.common.QueuedWork;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class KiipuApplication extends MultiDexApplication {


    public static KiipuApplication instance;

    public static Context appContext;

    private static int SCREEN_WIDTH = -1;

    private static int SCREEN_HEIGHT = -1;

    private static float DIMEN_RATE = -1.0F;

    private static int DIMEN_DPI = -1;

    public static RetrofitService mRetrofitService;


    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setSinaWeibo("3763012369", "70ca7f8be6f87b1157ced83e67d27a48","http://sns.whalecloud.com");

    }

    public static synchronized KiipuApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Config.DEBUG = true;
        QueuedWork.isUseThreadPool = false;
        getScreenSize();
        mRetrofitService = RetrofitClient.getInstance().create(RetrofitService.class);

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


}
