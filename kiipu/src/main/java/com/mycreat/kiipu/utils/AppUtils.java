package com.mycreat.kiipu.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import com.mycreat.kiipu.core.AppManager;
import org.jetbrains.annotations.NotNull;

/**
 * 跟App相关的辅助类
 * Created by liangyanqiao on 2017/5/27.
 */
public class AppUtils {

    private AppUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void logOutApp(Context mContext) {
        LogUtil.e("when logOutApp ACCESS_TOKEN " + SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN, ""));
        AppManager.getAppManager().appExit(mContext);
    }

    /**
     * @param context
     * @param metaKey
     * @return null if 'AndroidManifest.xml' does not contains the metaKey
     */
    @Nullable
    public static String getMetaData(Context context, @NotNull String metaKey){
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getApplicationInfo().packageName, PackageManager.GET_META_DATA);

            if (applicationInfo != null && applicationInfo.metaData != null) {
                Object object = applicationInfo.metaData.get(metaKey);
                if (object != null) {
                    return object.toString();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
