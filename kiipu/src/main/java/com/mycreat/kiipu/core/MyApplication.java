package com.mycreat.kiipu.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Config;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.mycreat.kiipu.retrofit.RetrofitClient;
import com.mycreat.kiipu.retrofit.RetrofitService;

import java.util.ArrayList;

/**
 * Created by leaderliang on 2017/3/30.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class MyApplication extends Application {


    private static MyApplication instance;
    private static Context appContext;
    private ArrayList<Activity> listActivitys;
    public static int SCREEN_WIDTH = -1;
    public static int SCREEN_HEIGHT = -1;
    public static float DIMEN_RATE = -1.0F;
    public static int DIMEN_DPI = -1;
    public static RetrofitService mRetrofitService;

    public static synchronized MyApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appContext = this;
        //开启debug模式，方便定位错误，具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，正式发布，请关闭该模式
//        Config.DEBUG = true;
        getScreenSize();
        mRetrofitService = RetrofitClient.getInstance().create(RetrofitService.class);

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
