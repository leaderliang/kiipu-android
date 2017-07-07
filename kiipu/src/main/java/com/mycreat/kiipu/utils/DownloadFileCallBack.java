package com.mycreat.kiipu.utils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 文件下载接口
 *
 * Created by leaderliang on 2017/5/1.
 */
public interface DownloadFileCallBack {

    @Streaming //大文件时要加不然会OOM
    @GET
    Call<ResponseBody> getDownLoadFile(@Url String fileUrl);

}
