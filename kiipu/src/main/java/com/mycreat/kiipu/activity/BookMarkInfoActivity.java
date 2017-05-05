package com.mycreat.kiipu.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.utils.DisplayUtils;
import com.mycreat.kiipu.view.DetailScrollView;
import com.mycreat.kiipu.view.SVRootLinearLayout;

public class BookMarkInfoActivity extends BaseActivity {

    private DetailScrollView mScrollView;

    //ScrollView 底部的布局LinearLayout
    private SVRootLinearLayout mSVRootLl;

    //app图片和名字控件
    private ImageView mIconImageView;

    //中间内容布局，内容里的底部和顶部title布局
    private LinearLayout mContentLl;
    //内容里的底部和顶部title布局
    private LinearLayout mBottomLl, mTitleLl;

    //根布局的背景色
    private ColorDrawable mRootCDrawable;
    private int mColorInitAlpha = 150;

    private int mContentTopOffsetNum;
    private int mContentBottomOffsetNum;
    private int mImageLeftOffsetNum;
    private int mImageTopOffsetNum;

    //接收过来的参数
    private int mViewMarginTop;
    private int mImageId;
    private String mAppName;

    private boolean initData;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book_mark_info;
    }

    @Override
    protected void onViewClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.hideActionBar(getWindow());
        initAnimationData();

    }

    @Override
    protected void initViews() {
        super.initViews();
        Intent intent = getIntent();
        mViewMarginTop = intent.getIntExtra("viewMarginTop", 0);

        //设置root节点view的背景透明度
        LinearLayout rootLl = (LinearLayout) findViewById(R.id.ll_root);
        Drawable rootBgDrawable = rootLl.getBackground();
        mRootCDrawable = (ColorDrawable) rootBgDrawable;
        mRootCDrawable.setAlpha(mColorInitAlpha);

        mScrollView = (DetailScrollView) findViewById(R.id.scrollView);
        mSVRootLl = (SVRootLinearLayout) findViewById(R.id.ll_sv_root);
        mIconImageView = (ImageView) findViewById(R.id.imageview_icon);
        mIconImageView.setImageResource(mImageId);

        mContentLl = (LinearLayout) findViewById(R.id.ll_content);
        mBottomLl = (LinearLayout) findViewById(R.id.ll_bottom);
        mTitleLl = (LinearLayout) findViewById(R.id.ll_title);

        mImageTopOffsetNum = getResources().getDimensionPixelOffset(R.dimen.title_view_height);

        //设置初始化的位置
        mSVRootLl.setContentInitMarginTop(mViewMarginTop);
        mContentTopOffsetNum = mViewMarginTop - getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_default_height_material);

        /**
         *  activity 关闭回调
         */
        mSVRootLl.setOnCloseListener(new SVRootLinearLayout.OnCloseListener() {
            @Override
            public void onClose() {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        /**
         *  下拉拖动时候回调修改root背景色的透明度
         */
        mSVRootLl.setOnUpdateBgColorListener(new SVRootLinearLayout.OnUpdateBgColorListener() {
            @Override
            public void onUpdate(float ratio) {
                mRootCDrawable.setAlpha((int) (mColorInitAlpha - mColorInitAlpha * ratio));
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();

    }

    private void initAnimationData() {
        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!initData) {
                    mContentBottomOffsetNum = mScrollView.getMeasuredHeight() - mContentLl.getBottom();
                    mSVRootLl.setInitBottom(mContentLl.getBottom());
                    mSVRootLl.setAnimationStatus(true);
                    mSVRootLl.setLayoutImageView(true);
                    mImageLeftOffsetNum = (DisplayUtils.getScreenWidth(BookMarkInfoActivity.this) - mIconImageView.getWidth()) / 2 - getResources().getDimensionPixelOffset(R.dimen.icon_margin);
                    initData = true;
                    startAnimation();
                }
            }
        });
    }

    private void startAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(400);
        valueAnimator.setStartDelay(100);
        valueAnimator.start();
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float) animation.getAnimatedValue();
                //内容布局顶部偏移量
                int contentTopOffset = (int) (ratio * mContentTopOffsetNum);
                //内容布局底部偏移量
                int contentBottomOffset = (int) (ratio * mContentBottomOffsetNum);
                //图片左边偏移量
                int imageLeftOffset = (int) (ratio * mImageLeftOffsetNum);
                //图片上边偏移量
                int imageTopOffset =  (int) (ratio * mImageTopOffsetNum);
                mSVRootLl.setAllViewOffset(mViewMarginTop - contentTopOffset, contentBottomOffset, imageLeftOffset, imageTopOffset);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSVRootLl.setAnimationStatus(false);
                mBottomLl.setVisibility(View.VISIBLE);
                mTitleLl.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            if(mSVRootLl != null) mSVRootLl.startAnimation(mSVRootLl.getCenterVisibleViewHeight(), false, 0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
