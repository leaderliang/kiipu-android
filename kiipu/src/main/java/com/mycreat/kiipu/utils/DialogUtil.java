package com.mycreat.kiipu.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.mycreat.kiipu.R;

/**
 * DialogUtil
 *
 * @author leaderliang
 */
public class DialogUtil {

    public interface ButtonCallBack{
        void buttonCallBack(Button bt);
    }

    public static void showEditDialog(final Context context,
                                      DialogInterface.OnClickListener positiveButtonClick,
                                      DialogInterface.OnClickListener negativeButtonClick,
                                      TextWatcher textWatcher,
                                      ButtonCallBack callBack) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_edit_dialog, null);
        final EditText editText = (EditText) view.findViewById(R.id.et_name);
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
        editText.addTextChangedListener(textWatcher);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("名称");
        builder.setPositiveButton("确定", positiveButtonClick);
        builder.setNegativeButton("取消", negativeButtonClick);
        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        // 获取PositiveButton 重点在这里
        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if(callBack != null){
            callBack.buttonCallBack(btn);
        }
    }

}


