package com.mycreat.kiipu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;

/**
 * TODO
 * Created by liangyanqiao on 2017/8/30.
 */
public class addBookmarkActivity extends BaseActivity {


    private TextView tvShareContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bookmark);
        initViews();
        initData();
    }


    @Override
    protected void initViews() {
        super.initViews();
        setBaseTitle("添加书签");
        setBackBtn();
        setFloatingVisibile(false);

        tvShareContent = initViewById(R.id.tv_share_content);
    }


    @Override
    protected void initData() {
        super.initData();

        Intent intent = getIntent();
        if (intent == null) return;
        String action = intent.getAction();
        if (action == null) return;
        String type = intent.getType();
        if (type == null) return;
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        if (Intent.ACTION_SEND.equals(action)) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // 处理发送来的文字
            }
        }

    }


    private void handleSendText(Intent intent) {
//        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
//        if (sharedText != null) {// 根据分享的文字更新UI}
        tvShareContent.setText("分享到页面的内容："
                        + "\ntitle: " + intent.getExtras().get(Intent.EXTRA_TITLE)
                        + "\ncontent: " + intent.getExtras().get(Intent.EXTRA_TEXT)
                        + "\nextras toString: " + intent.getExtras().toString()
        );
    }

    @Override
    protected void onViewClick(View v) {

    }


}
