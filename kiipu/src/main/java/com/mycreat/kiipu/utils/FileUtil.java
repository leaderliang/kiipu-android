package com.mycreat.kiipu.utils;

import android.content.Context;
import android.os.Environment;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.*;

/**
 * Created by liangyanqiao on 2017/7/6.
 */
public class FileUtil {

    private static boolean isGoOn = true;

    public static File createFile(Context context, String fileName) {
        File file;
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ fileName);
        } else {
            file = new File(context.getCacheDir().getAbsolutePath() + "/test.apk");
        }
        LogUtil.e("FileUtil createFile", "file----->" + file.getAbsolutePath());
        return file;

    }


    public static void writeFileToDisk(Response<ResponseBody> response, File file, ProgressCallBack progressCallBack) {
        long currentLength = 0;
        OutputStream os = null;
        InputStream is = response.body().byteStream();
        long totalLength = response.body().contentLength();
        try {
            os = new FileOutputStream(file);
            int len;
            byte[] buff = new byte[1024];
            while ((len = is.read(buff)) != -1) {
                if (!isGoOn) {
//                    Log.e("writeFileToDisk", "停止下载了  当前进度:--------------->" + currentLength);
                    // 恢复默认进度
                    progressCallBack.onProgress(0, 0);
                    break;
                }
                os.write(buff, 0, len);
                currentLength += len;
//                Log.e("writeFileToDisk", "还在下载了 当前进度:----->" + currentLength);
                progressCallBack.onProgress(currentLength, totalLength);
            }
            // httpCallBack.onLoading(currentLength,totalLength,true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void prepareDownFile(){
        isGoOn = true;
    }

    public static void stopDownFile(){
        isGoOn = false;
    }
}
