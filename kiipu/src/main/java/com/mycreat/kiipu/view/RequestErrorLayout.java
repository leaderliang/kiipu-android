package com.mycreat.kiipu.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mycreat.kiipu.R;

/**
 * RequestErrorLayout
 */
public class RequestErrorLayout extends RelativeLayout {

    private ImageView mImage;

    private TextView mText;

    public RequestErrorLayout(Context context) {
        this(context, null);
    }

    public RequestErrorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RequestErrorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mImage = (ImageView) findViewById(R.id.request_error_image);
        mText = (TextView) findViewById(R.id.request_error_text);
    }

    public void setErrorText(int id) {
        mText.setText(id);
    }

    public void setErrorText(String text) {
        mText.setText(text);
    }


}
