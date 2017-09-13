package com.mycreat.kiipu.view;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.utils.GlideUtil;

/**
 * Created by liangyanqiao on 2017/5/22.
 */
public class LeftIvTextView extends RelativeLayout {
    private Context mContext;
    private ImageView mImageView;
    private TextView mTextView_1;
    private TextView mTextView_2;
    private String text;
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
        ViewTreeObserver vto = mTextView_1.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    public void loadImage(String url) {
        GlideUtil.getInstance().loadImage(mImageView, url, R.drawable.default_logo_small, true);
    }

    public void setText(final String text) {
        mTextView_1.setText(text);
        this.text = text;
        adjustTextLayout();
    }

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            adjustTextLayout();
        }
    };

    public void adjustTextLayout(){
        int index ;
        Layout layout = mTextView_1.getLayout();
        if(layout != null && (index = layout.getLineEnd(0)) > 0) {
            String str1 = text.substring(0, index > text.length() ? text.length() : index);
            mTextView_1.setText(str1);
            String str2 = text.substring(index > text.length() ? text.length() : index, text.length());
            mTextView_2.setText(str2);
        }
    }
}
