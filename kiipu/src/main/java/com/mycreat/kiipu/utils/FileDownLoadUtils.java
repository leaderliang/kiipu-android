package com.mycreat.kiipu.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.ProgressBar;
import com.mycreat.kiipu.model.CardTemplateInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.File;
import java.net.MalformedURLException;

/**
 * 带进度的群文件下载
 * <p>
 * Created by leaderliang on 2017/7/6.
 */
public class FileDownLoadUtils {

    private Activity mContext;
    private String mBaseUrl, mFileUrl, mFileName;
    private ProgressBar mProgress;
    private DownLoadFinishCallBack mDownLoadFinishCallBack = null;

    public void FileDownLoad(Activity context, String baseUrl, String fileUrl) {
        this.mContext = context;
        this.mBaseUrl = baseUrl;
        this.mFileUrl = fileUrl;
        startDownload();
    }

    public void FileDownLoad(Activity context, String baseUrl, String fileUrl, ProgressBar progress) {
        this.mContext = context;
        this.mBaseUrl = baseUrl;
        this.mFileUrl = fileUrl;
        this.mProgress = progress;
        startDownload();
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }


//    private void requestDownLoadFile(CardTemplateInfo cardTemplateInfo) {
//        String fileName = cardTemplateInfo.templateName;
//        java.net.URL url = null;
//        try {
//            url = new java.net.URL(cardTemplateInfo.templatePath);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        String mBaseUrl = url != null ? url.getHost() : "";
//        if (TextUtils.isEmpty(mBaseUrl) || TextUtils.isEmpty(url.toString())) {
//            ToastUtil.showToastShort("文件地址异常，暂不支持下载");
//            return;
//        }
//        String mFileUrl = url.toString().replace(mBaseUrl, "");
////         mBaseUrl = "http://surl.qq.com/";
////         mFileUrl = "195D0D?qbsrc=51&asr=4286";
//        if (mFileUrl.contains("http://")) {
//            mBaseUrl = "http://" + mBaseUrl;
//            mFileUrl = mFileUrl.replace("http://", "");
//        } else if (mFileUrl.contains("https://")) {
//            mBaseUrl = "https://" + mBaseUrl;
//            mFileUrl = mFileUrl.replace("https://", "");
//        }
//
//        FileDownLoadUtils mFileDownLoad = new FileDownLoadUtils();
//        mFileDownLoad.setFileName(fileName + ".html");
//        ToastUtil.showToastShort("开始下载...");
//        LogUtil.e("start download----->");
//        isDownloading = true;
//        mFileDownLoad.setOnDownLoadFinishCallBack(new FileDownLoadUtils.DownLoadFinishCallBack() {
//            @Override
//            public void onFinish() {
//                isDownloading = false;
//            }
//        });
//        mFileDownLoad.FileDownLoad(this, mBaseUrl, mFileUrl);
//    }


    private void startDownload() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        DownloadFileCallBack downloadService = retrofit.create(DownloadFileCallBack.class);
        Call<ResponseBody> responseBodyCall = downloadService.getDownLoadFile(mFileUrl);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                LogUtil.d(response.message() + "  length  " + response.body().contentLength() + "  type " + response.body().contentType());
                //建立一个文件
                final File file = FileUtil.createFile(mContext, mFileName);
                //下载文件放子线程
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        FileUtil.prepareDownFile();
                        //保存到本地
                        FileUtil.writeFileToDisk(response, file, new ProgressCallBack() {
                            @Override
                            public void onProgress(final long current, final long total) {
                                /**
                                 * 更新进度条
                                 */
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int percent = (int) ((current * 1.0 / total * 1.0) * 100);
                                        if (mProgress != null) {
                                            mProgress.setProgress(percent);
                                        }
                                        if (percent == 100 && mDownLoadFinishCallBack != null) {
                                            ToastUtil.showToastShort("文件下载完成");
                                            LogUtil.e("download end----->");
                                            mDownLoadFinishCallBack.onFinish();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                LogUtil.d(t.getMessage() + " onFailure -----> " + t.toString());
                ToastUtil.showToastShort("下载失败 " + t.toString());
            }
        });


    }


    public interface DownLoadFinishCallBack {
        void onFinish();
    }

    public void setOnDownLoadFinishCallBack(DownLoadFinishCallBack callBack) {
        this.mDownLoadFinishCallBack = callBack;
    }
}
