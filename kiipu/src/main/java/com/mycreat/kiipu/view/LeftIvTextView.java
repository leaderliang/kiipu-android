package com.mycreat.kiipu.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.mycreat.kiipu.R;

/**
 * Created by liangyanqiao on 2017/5/22.
 */
public class LeftIvTextView extends RelativeLayout{
    private Context mContext;
    private ImageView mImageView;
    private TextView mTextView_1;
    private TextView mTextView_2;


    public LeftIvTextView(Context context) {
        this(context, null);
    }

    public LeftIvTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftIvTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View view = View.inflate(mContext, R.layout.view_left_textview, this);
        mImageView = (ImageView) view.findViewById(R.id.iv_icon);
        mTextView_1 = (TextView) view.findViewById(R.id.tv_num1);
        mTextView_2 = (TextView) view.findViewById(R.id.tv_num2);
    }

    public void loadImage(String url) {
        Glide.with(mContext)
                .load(url)
                .placeholder(R.mipmap.ic_launcher) // 占位图
                .error(R.drawable.error) // 加载失败占位图
                .into(mImageView);
    }

    public void setText(final String string) {
        mTextView_1.setText(string);
        ViewTreeObserver vto = mTextView_1.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout layout = mTextView_1.getLayout();
                int index = layout.getLineEnd(0);
                String str1 = string.substring(0, index>string.length() ? string.length() : index);
                String str2 = string.substring(index>string.length() ? string.length() : index, string.length());
                mTextView_1.setText(str1);
                mTextView_2.setText(str2);
            }
        });

    }
}
