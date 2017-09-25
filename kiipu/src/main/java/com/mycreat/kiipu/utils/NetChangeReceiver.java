package com.mycreat.kiipu.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 监听 app 网络变化
 * @author liangyanqiao
 */
public class NetChangeReceiver extends BroadcastReceiver {

    private CallBackState listener;

    private String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (listener != null) {
                listener.getNetState(isNetWorkAvailable(context));
                LogUtil.d(TAG, "net changed , current state is " + isNetWorkAvailable(context));
            }
        }
    }

    public void setNetCallBackListener(CallBackState listener) {
        this.listener = listener;
    }

    public interface CallBackState {
        void getNetState(boolean state);
    }

    /**
     * 网络判断
     *
     * @return true代表有网络 false代表没可用网络
     */
    public static boolean isNetWorkAvailable(Context context) {
        // 系统里面提供的网络访问状况相关的服务
        if (context == null)
            return false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;

        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null) {
            return info.isAvailable();
        } else {
            return false;
        }
    }

}
