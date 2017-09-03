package com.mycreat.kiipu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.LogUtil;
import com.mycreat.kiipu.utils.ToastUtil;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 * Created by liangyanqiao on 2017/8/30.
 */
public class addBookmarkActivity extends BaseActivity {


    private TextView tvShareContent;

    private String resultUrl;

    private JSONObject jsonObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useBaseLayout = false;
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

        addBookmark(intent);


    }

    private void addBookmark(Intent intent) {

        String strRegex = "[a-z]+:\\/\\/\\S+";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(intent.getExtras().get(Intent.EXTRA_TEXT).toString());
        while (matcher.find()) {
            resultUrl = matcher.group();
            LogUtil.d("resultUrl-----" + resultUrl);
            try {
                jsonObject = new JSONObject();
                jsonObject.put("url", resultUrl);
                jsonObject.put("note", intent.getExtras().get(Intent.EXTRA_TEXT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Call<Bookmark> call = KiipuApplication.mRetrofitService.addBookmark(userAccessToken, jsonObject.toString());
            call.enqueue(new Callback<Bookmark>() {
                @Override
                public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                    Bookmark mBookmark = response.body();
                    if (mBookmark != null) {
                        LogUtil.d("result title---" + mBookmark.info.title);
                        ToastUtil.showToastShort("result title---" + mBookmark.info.title);
//                    Snackbar.make(mFloatingActionButton, "result title---"+mBookmark.info.title, Snackbar.LENGTH_LONG).show();
                    } else {
                        LogUtil.d("result title is null");
                        ToastUtil.showToastShort("result title is null");
                    }
                }

                @Override
                public void onFailure(Call<Bookmark> call, Throwable t) {
                    Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
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
