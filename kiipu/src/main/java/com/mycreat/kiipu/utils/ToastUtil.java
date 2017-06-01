package com.mycreat.kiipu.utils;

import android.content.Context;
import android.widget.Toast;
import com.mycreat.kiipu.core.KiipuApplication;

/**
 * ToastUtil
 * @author leaderliang
 */
public class ToastUtil {

    private static Toast toast = null;

    public static boolean isShow = true;

    private ToastUtil()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static void showMessage(final Context context, final String message, final int length) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, message, length);
            } else {
                toast.setText(message);
            }
            if(isShow) {
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast = null;
        }
    }

    public static void showToastShort(int resId) {
        showMessage(KiipuApplication.appContext, KiipuApplication.appContext.getString(resId), Toast.LENGTH_SHORT);
    }

    public static void showToastShort(String message) {
        showMessage(KiipuApplication.appContext, message, Toast.LENGTH_SHORT);
    }

    public static void showToastLong(int resId) {
        showMessage(KiipuApplication.appContext, KiipuApplication.appContext.getString(resId), Toast.LENGTH_LONG);
    }

    public static void showToastLong(String message) {
        showMessage(KiipuApplication.appContext, message, Toast.LENGTH_LONG);
    }
}
