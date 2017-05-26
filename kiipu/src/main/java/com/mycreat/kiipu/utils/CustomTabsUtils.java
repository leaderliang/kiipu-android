package com.mycreat.kiipu.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.view.customtab.CustomTabActivityHelper;
import com.mycreat.kiipu.view.customtab.WebViewFallback;


public class CustomTabsUtils {

    public static void showCustomTabsView(Activity context, String url, String viewTheme) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        // 修改 ActionBar 的颜色
        intentBuilder.setToolbarColor(Color.parseColor("#" + viewTheme));
        // 自定义bottom bar 颜色
        intentBuilder.setSecondaryToolbarColor(Color.parseColor("#d32f2f"));

        // 添加一个分享按钮
        String shareLabel = "分享";
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_share);
//      PendingIntent pendingIntent = createPendingIntent();
//      intentBuilder.setActionButton(icon, shareLabel, pendingIntent);
        // 可以添加最多5个（MAX_TOOLBAR_ITEMS）actions到bottom bar 上，并使用setToolbarItem() 来更新它们
//                intentBuilder.addToolbarItem();
        //是否显示网页标题
        intentBuilder.setShowTitle(true);
        //自定义关闭 Custom tabs 的图标
        intentBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_arrow_back));
        //自定义 Activity 转场 动画
        intentBuilder.setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out);
        intentBuilder.setExitAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out);
        // 最后调用助手类 CustomTabActivityHelper 的 openCustomTab 函数来打开一个网址
        CustomTabActivityHelper.openCustomTab(context, intentBuilder.build(), Uri.parse(url), new
                WebViewFallback());
    }

}
