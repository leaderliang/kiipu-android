package com.mycreat.kiipu.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.*;
import com.mycreat.kiipu.R;

/**
 * DialogUtil
 * @author leaderliang
 * dialog 详细介绍可参考 http://www.jianshu.com/p/6caffdbcd5db
 */
public class DialogUtil {

    public interface ButtonCallBack {
        void buttonCallBack(Button bt);
    }

    public static void showEditDialog(final Context context,
                                      TextWatcher textWatcher,
                                      ButtonCallBack callBack,
                                      DialogInterface.OnClickListener NeutralButtonClick,
                                      DialogInterface.OnClickListener positiveButtonClick,
                                      ArrayMap<Object, Object> arrayMap) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_edit_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_name);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        editText.addTextChangedListener(textWatcher);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((String) arrayMap.get("title"));
        if(NeutralButtonClick != null) {
            builder.setNeutralButton("删除", NeutralButtonClick);
        }
        builder.setPositiveButton("确定", positiveButtonClick);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeyBoardUtils.closeKeyboard(editText,context);
            }
        });
        builder.setCancelable(false);
        builder.setView(view);
        String hint = (String) arrayMap.get("hint");
        String content = (String) arrayMap.get("content");
        editText.setHint(hint);
        editText.setText(content);
        editText.setSelection(content.length());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        /* 获取 PositiveButton 重点在这里, 设置没有文字时按钮置灰*/
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (callBack != null) {
            callBack.buttonCallBack(btn);
        }
    }

    public static View showCanSetViewDialog(final Context context,int viewId, DialogInterface.OnClickListener positiveButtonClick){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewId, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("详情");
        builder.setPositiveButton("完成", positiveButtonClick);
        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        return view;
    }

    public static View showCanSetViewDialogFragment(final Context context,int viewId, DialogInterface.OnClickListener positiveButtonClick){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewId, null);

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
////        builder.setTitle("详情");
//        builder.setPositiveButton("完成", positiveButtonClick);
//        builder.setCancelable(false);
//        builder.setView(view);
//        AlertDialog dialog = builder.create();
//        dialog.show();


        return view;
    }

    public static void showCommonDialog(Context context,
                                        int iconId,
                                        String title,
                                        String message,
                                        String positiveStr,
                                        String negativeStr,
                                        boolean isCancelable,
                                        DialogInterface.OnClickListener positiveButtonClick,
                                        DialogInterface.OnClickListener negativeButtonClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setIcon(iconId)
                .setMessage(message)
                .setCancelable(isCancelable)
                .setPositiveButton(positiveStr, positiveButtonClick)
                .setNegativeButton(negativeStr, negativeButtonClick);
        builder.show();
    }

    public static void showCommonDialog(Context context,
                                        String title,
                                        String message,
                                        String positiveStr,
                                        String negativeStr,
                                        boolean isCancelable,
                                        DialogInterface.OnClickListener positiveButtonClick,
                                        DialogInterface.OnClickListener negativeButtonClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(isCancelable)
                .setPositiveButton(positiveStr, positiveButtonClick)
                .setNegativeButton(negativeStr, negativeButtonClick);
        builder.show();
    }

    public static void showCommonDialog(Context context,
                                        String title,
                                        String message,
                                        boolean isCancelable,
                                        DialogInterface.OnClickListener positiveButtonClick,
                                        DialogInterface.OnClickListener negativeButtonClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(isCancelable)
                .setPositiveButton("确定", positiveButtonClick)
                .setNegativeButton("取消", negativeButtonClick);
        builder.show();
    }

    public static void showCommonDialog(Context context,
                                        String title,
                                        String message,
                                        DialogInterface.OnClickListener positiveButtonClick,
                                        DialogInterface.OnClickListener negativeButtonClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("确定", positiveButtonClick)
                .setNegativeButton("取消", negativeButtonClick);
        builder.show();
    }


    public static ListPopupWindow showPopupWindow(Context mContext, View view, AdapterView.OnItemClickListener listener) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);

        // ListView适配器
        listPopupWindow.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, Constants.ITEMS));
        // 选择item的监听事件
        listPopupWindow.setOnItemClickListener(listener);

        // 对话框的宽高
        listPopupWindow.setWidth(450);
        listPopupWindow.setHeight(450);

        // ListPopupWindow 相对的View
        listPopupWindow.setAnchorView(view);

        // ListPopupWindow 相对按钮横向 和纵向 的距离
        listPopupWindow.setHorizontalOffset(50);
        listPopupWindow.setVerticalOffset(1);

        //  Set whether this window should be modal when shown.
        // If a popup window is modal, it will receive all touch and key input. If the user touches outside the popup window's content area the popup window will be dismissed.
        // modal boolean: true if the popup window should be modal, false otherwise.
        listPopupWindow.setModal(false);

        listPopupWindow.show();
        return listPopupWindow;
    }

}


