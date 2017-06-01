package com.mycreat.kiipu.utils;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;


/**
 * Created by liangyanqiao on 2017/5/27.
 */
public class ClipboardUtils {

    public static final String MIME_TYPE_CONTACT = "vnd.android.cursor.item/vnd.leaderliang.contact";
    private static ClipboardManager clipboard = null;

    /**
     * 从Clipboard 中取数据
     */
    public static String get(Context context) {
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item;
        //无数据时直接返回
        if (!clipboard.hasPrimaryClip()) {
//            ToastUtil.showToastShort("剪贴板中无数据");
            return "";
        }

        //如果是文本信息
        if (clipboard.getPrimaryClipDescription().hasMimeType(
                ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            ClipData cdText = clipboard.getPrimaryClip();
            item = cdText.getItemAt(0);
            //此处是TEXT文本信息
            if (item.getText() == null) {
//                ToastUtil.showToastShort("剪贴板中无数据");
                return "";
            } else {
                return item.getText().toString();
            }
            //如果是INTENT
        } else if (clipboard.getPrimaryClipDescription().hasMimeType(
                ClipDescription.MIMETYPE_TEXT_INTENT)) {
            //此处是INTENT
            item = clipboard.getPrimaryClip().getItemAt(0);
            Intent intent = item.getIntent();
            context.startActivity(intent);
            //........

            //如果是URI
        } else if (clipboard.getPrimaryClipDescription().hasMimeType(
                ClipDescription.MIMETYPE_TEXT_URILIST)) {
            //此处是URI内容
            ContentResolver cr = context.getContentResolver();
            ClipData cdUri = clipboard.getPrimaryClip();
            item = cdUri.getItemAt(0);
            Uri uri = item.getUri();
            if (uri != null) {
                String mimeType = cr.getType(uri);
                if (mimeType != null) {
                    if (mimeType.equals(MIME_TYPE_CONTACT)) {
                        Cursor pasteCursor = cr.query(uri, null, null, null, null);
                        if (pasteCursor != null) {
                            if (pasteCursor.moveToFirst()) {
                                //此处对数据进行操作就可以了,前提是有权限
                            }
                        }
                        pasteCursor.close();
                    }
                }
            }
        }
        return "";
    }
}
