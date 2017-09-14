package com.mycreat.kiipu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.*;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 * Created by liangyanqiao on 2017/8/30.
 */
public class addBookmarkActivity extends BaseActivity {

    private String resultUrl;

    private JSONObject jsonObject;

    private String extraText;

    private CoordinatorLayout mContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useBaseLayout = false;
        setContentView(R.layout.activity_add_bookmark);
        initViews();
        initData();
//        initListener();
    }

    @Override
    protected void initViews() {
        super.initViews();
        /*去掉 dialog 两侧边距 */
        WindowManager vm = getWindowManager();
        Display dis = vm.getDefaultDisplay();
        android.view.WindowManager.LayoutParams lay = getWindow().getAttributes();
        lay.width = dis.getWidth() * 1;
        getWindow().setAttributes(lay);

        mContainer = initViewById(R.id.container);
    }


    @Override
    protected void initData() {
        super.initData();
        String accessToken = (String) SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN, "");
        if(StringUtils.isEmpty(accessToken)){
            Snackbar.make(mContainer, "请先登录 Kiipu", Snackbar.LENGTH_INDEFINITE)
                    .setAction("登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(addBookmarkActivity.this, LoginActivity.class));
                            finish();
                        }
                    })
                    .setActionTextColor(Color.parseColor("#FFB74D"))
                    .show();
        }else {
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
                    addBookmark(intent); // 处理发送来的文字
                }
            }
        }
    }

//    @Override
//    protected void initListener() {
//        super.initListener();
//        imgBack.setOnClickListener(this);
//        tvSave.setOnClickListener(this);
//    }

    private void addBookmark(Intent intent) {
        extraText = intent.getExtras().get(Intent.EXTRA_TEXT).toString();
        if (StringUtils.isEmpty(extraText)) {
            return;
        }
        String strRegex = "[a-z]+:\\/\\/\\S+";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(extraText);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            resultUrl = matcher.group();
            list.add(resultUrl);
            if(!StringUtils.isEmpty(resultUrl) && extraText.contains(resultUrl)){
                String contentText = extraText.replace(resultUrl,"");
//                etShareContent.setText(contentText);
            }
            LogUtil.d("resultUrl-----" + resultUrl);
        }
        // 判断网络
        Snackbar.make(mContainer, "添加成功，请在 kiipu 中查看", Snackbar.LENGTH_LONG)
                .setDuration(2500)
                .show();

        requestAddBookmark(list.get(0), extraText);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },3000);
    }

    @Override
    protected void onViewClick(View v) {

        switch (v.getId()){
//            case R.id.img_back:
//                finish();
//                break;
//            case R.id.tv_save:
//                requestAddBookmark(etShareContent.getText().toString());
//                break;
        }
    }

    private void requestAddBookmark(String url, String note) {
        if(StringUtils.isEmpty(url)){
            Snackbar.make(mContainer, "保存的网页数据异常，请稍后重试~", Snackbar.LENGTH_LONG).show();
            return;
        }

        try {
            jsonObject = new JSONObject();
            jsonObject.put("url", url);
            jsonObject.put("note", note);
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
//                    Snackbar.make(mContainer, "添加成功，请在 kiipu 中查看", Snackbar.LENGTH_LONG).show();
                } else {
                    LogUtil.d("result title is null");
//                    Snackbar.make(mContainer, "添加失败，请稍后重试", Snackbar.LENGTH_LONG).show();
                }
//                finish();
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
//                Snackbar.make(mContainer, t.getMessage(), Snackbar.LENGTH_LONG).show();
//                finish();
            }
        });
    }


}
