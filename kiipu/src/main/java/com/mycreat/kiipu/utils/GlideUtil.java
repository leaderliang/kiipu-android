package com.mycreat.kiipu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * GlideUtil
 * Created by liangyanqiao on 2017/5/30.
 */
public class GlideUtil {

    private static GlideUtil mInstance;

    private Context mContext = KiipuApplication.appContext;

    private GlideUtil() {
    }

    public static GlideUtil getInstance() {
        if (mInstance == null) {
            synchronized (GlideUtil.class) {
                if (mInstance == null) {
                    mInstance = new GlideUtil();
                }
            }
        }
        return mInstance;
    }


    /**
     * 常规加载图片
     *
     * @param mContext
     * @param imageView 图片容器
     * @param imgUrl    图片地址
     * @param isFade    是否需要动画 淡入淡出动画，显得更加平滑
     * 在非主线程中调用，有可能会报 You cannot start a load for a destroyed activity 这样的异常，如果需要使用，可将上下问设置为 getApplicationmContext
     */
    public void loadImage(ImageView imageView,
                          String imgUrl, boolean isFade) {
        if (isFade) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.drawable.default_item_cover) // 占位图
                    .error(R.drawable.default_item_cover)
                    .crossFade()
                    .priority(Priority.NORMAL) //下载的优先级
                    //all:缓存源资源和转换后的资源 none:不作任何磁盘缓存
                    //source:缓存源资源   result：缓存转换后的资源
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存策略 禁用掉Glide的缓存功能,默认是打开的
                    .centerCrop() // 取图片的中间区域
//                    .skipMemoryCache(true)// 跳过内存缓存
//                    .thumbnail(0.1f) // 用原图的1/10作为缩略图
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.drawable.default_item_cover) // 占位图
                    .error(R.drawable.default_item_cover)
                    .priority(Priority.NORMAL) //下载的优先级
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存策略 禁用掉Glide的缓存功能,默认是打开的
                    .centerCrop() // 取图片的中间区域
                    .dontAnimate()
                    .into(imageView);
        }
    }

    public void loadImage(ImageView imageView,
                          String imgUrl, int defaultImg, boolean isFade) {
        if (isFade) {
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(defaultImg) // 占位图
                    .error(defaultImg)
                    .crossFade()
                    .priority(Priority.NORMAL) //下载的优先级
                    //all:缓存源资源和转换后的资源 none:不作任何磁盘缓存
                    //source:缓存源资源   result：缓存转换后的资源
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存策略 禁用掉Glide的缓存功能,默认是打开的
                    .centerCrop() // 取图片的中间区域
//                    .skipMemoryCache(true)// 跳过内存缓存
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(defaultImg) // 占位图
                    .error(defaultImg)
                    .priority(Priority.NORMAL) // 下载的优先级
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存策略 禁用掉Glide的缓存功能,默认是打开的
                    .centerCrop() // 取图片的中间区域
                    .dontAnimate()
                    .into(imageView);
        }
    }

    /**
     * 加载缩略图
     *
     * @param mContext
     * @param imageView 图片容器
     * @param imgUrl    图片地址
     */
    public void loadThumbnailImage(ImageView imageView, String imgUrl) {
        Glide.with(mContext)
                .load(imgUrl)
                .error(R.drawable.default_item_cover)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .thumbnail(Constants.THUMB_SIZE)
                .into(imageView);
    }

    /**
     * 加载图片并设置为指定大小
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     * @param withSize
     * @param heightSize
     */
    public void loadOverrideImage( ImageView imageView,
                                  String imgUrl, int withSize, int heightSize) {
        Glide.with(mContext)
                .load(imgUrl)
                .error(R.drawable.default_item_cover)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .override(withSize, heightSize)
                .into(imageView);
    }

    /**
     * 加载图片并对其进行模糊处理
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     */
    public void loadBlurImage( ImageView imageView, String imgUrl) {
        Glide.with(mContext)
                .load(imgUrl)
                .error(R.drawable.default_item_cover)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .bitmapTransform(new BlurTransformation(mContext, Constants.BLUR_VALUE))
                .into(imageView);
    }

    /**
     * 加载圆图
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     */
    public void loadCircleImage( ImageView imageView, String imgUrl, int defaultImg) {
        Glide.with(mContext)
                .load(imgUrl)
                .placeholder(defaultImg)
                .error(defaultImg)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .bitmapTransform(new CropCircleTransformation(mContext))
                .into(imageView);
    }

    /**
     * 图片灰度处理
     *
     * @param imageView
     * @param imgUrl
     * @param defaultImg
     */
    public void loadGrayImage( ImageView imageView, String imgUrl, int defaultImg) {
        Glide.with(mContext)
                .load(imgUrl)
                .placeholder(defaultImg)
                .error(defaultImg)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .bitmapTransform(new GrayscaleTransformation(mContext)) // 图片灰度处理
                .into(imageView);
    }

    /**
     * 加载模糊的圆角的图片
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     */
    public void loadBlurCircleImage( ImageView imageView, String imgUrl) {
        Glide.with(mContext)
                .load(imgUrl)
                .error(R.drawable.default_item_cover)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .bitmapTransform(
                        new BlurTransformation(mContext, Constants.BLUR_VALUE),
                        new CropCircleTransformation(mContext))
                .into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     */
    public void loadCornerImage( ImageView imageView, String imgUrl) {
        Glide.with(mContext)
                .load(imgUrl)
                .error(R.drawable.default_item_cover)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .bitmapTransform(
                        new RoundedCornersTransformation(
                                mContext, Constants.CORNER_RADIUS, Constants.CORNER_RADIUS))
                .into(imageView);
    }

    /**
     * 加载模糊的圆角图片
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     */
    public void loadBlurCornerImage( ImageView imageView, String imgUrl) {
        Glide.with(mContext)
                .load(imgUrl)
                .error(R.drawable.default_item_cover)
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .bitmapTransform(
                        new BlurTransformation(mContext, Constants.BLUR_VALUE),
                        new RoundedCornersTransformation(
                                mContext, Constants.CORNER_RADIUS, Constants.CORNER_RADIUS))
                .into(imageView);
    }

    /**
     * 同步加载图片
     *
     * @param mContext
     * @param imgUrl
     * @param target
     */
    public void loadBitmapSync( String imgUrl, SimpleTarget<Bitmap> target) {
        Glide.with(mContext)
                .load(imgUrl)
                .asBitmap()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .into(target);
    }

    /**
     * 加载gif
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     */
    public void loadGifImage( ImageView imageView, String imgUrl) {
        Glide.with(mContext)
                .load(imgUrl)
                .asGif()
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .error(R.drawable.default_item_cover)
                .into(imageView);
    }

    /**
     * 加载gif的缩略图
     *
     * @param mContext
     * @param imageView
     * @param imgUrl
     */
    public void loadGifThumbnailImage( ImageView imageView, String imgUrl) {
        Glide.with(mContext)
                .load(imgUrl)
                .asGif()
                .crossFade()
                .priority(Priority.NORMAL) //下载的优先级
                .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存策略
                .error(R.drawable.default_item_cover)
                .thumbnail(Constants.THUMB_SIZE)
                .into(imageView);
    }

    /**
     * 恢复请求，一般在停止滚动的时候
     *
     * @param mContext
     */
    public void resumeRequests(Context mContext) {
        Glide.with(mContext).resumeRequests();
    }

    /**
     * 暂停请求 正在滚动的时候
     *
     * @param mContext
     */
    public void pauseRequests(Context mContext) {
        Glide.with(mContext).pauseRequests();
    }

    /**
     * 清除磁盘缓存
     *
     * @param mContext
     */
    public void clearDiskCache(final Context mContext) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(mContext).clearDiskCache();//清理磁盘缓存 需要在子线程中执行
            }
        }).start();
    }

    /**
     * 清除内存缓存
     *
     * @param mContext
     */
    public void clearMemory(Context mContext) {
        Glide.get(mContext).clearMemory();//清理内存缓存  可以在UI主线程中进行
    }
}
