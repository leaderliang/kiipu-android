package com.mycreat.kiipu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.LogUtil;
import com.mycreat.kiipu.utils.StringUtils;
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


    private EditText etShareContent;

    private String resultUrl;

    private JSONObject jsonObject;

    private String extraText;

    private ImageView imgBack;

    private TextView tvSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useBaseLayout = false;
        setContentView(R.layout.activity_add_bookmark);
        initViews();
        initData();
        initListener();
    }


    @Override
    protected void initViews() {
        super.initViews();
        setBaseTitle("添加书签");
        setBackBtn();
        setFloatingVisibile(false);

        imgBack = initViewById(R.id.img_back);
        tvSave = initViewById(R.id.tv_save);
        etShareContent = initViewById(R.id.et_share_content);
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
                addBookmark(intent); // 处理发送来的文字
            }
        }


    }

    @Override
    protected void initListener() {
        super.initListener();
        imgBack.setOnClickListener(this);
        tvSave.setOnClickListener(this);
    }

    private void addBookmark(Intent intent) {
        extraText = intent.getExtras().get(Intent.EXTRA_TEXT).toString();
        if (StringUtils.isEmpty(extraText)) {
            return;
        }
        String strRegex = "[a-z]+:\\/\\/\\S+";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(extraText);
        while (matcher.find()) {
            resultUrl = matcher.group();
            if(!StringUtils.isEmpty(resultUrl) && extraText.contains(resultUrl)){
                String contentText = extraText.replace(resultUrl,"");
                etShareContent.setText(contentText);
            }
            LogUtil.d("resultUrl-----" + resultUrl);
        }
    }

    @Override
    protected void onViewClick(View v) {

        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_save:
                requestAddBookmark(etShareContent.getText().toString());
                break;
        }
    }

    private void requestAddBookmark(String str) {
        if(StringUtils.isEmpty(resultUrl)){
//            Snackbar.make(mFloatingActionButton, "保存的网页数据异常，请稍后重试~", Snackbar.LENGTH_LONG).show();
            ToastUtil.showToastShort("保存的网页数据异常，请稍后重试~");
            return;
        }
        if(StringUtils.isEmpty(str)){
//            Snackbar.make(mFloatingActionButton, "您还没有输入任何内容呦~", Snackbar.LENGTH_LONG).show();
            ToastUtil.showToastShort("您还没有输入任何内容呦~");
            return;
        }
        try {
            jsonObject = new JSONObject();
            jsonObject.put("url", resultUrl);
            jsonObject.put("note", str);
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
                    ToastUtil.showToastShort("添加成功，请在 kiipu 中查看");
//                    Snackbar.make(mFloatingActionButton, "添加成功，请在 kiipu 中查看", Snackbar.LENGTH_LONG).show();
                } else {
                    LogUtil.d("result title is null");
                    ToastUtil.showToastShort("添加失败，请稍后重试");
                }
                finish();
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
//                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG).show();
                ToastUtil.showToastShort(t.getMessage());
                finish();
            }
        });
    }


}
