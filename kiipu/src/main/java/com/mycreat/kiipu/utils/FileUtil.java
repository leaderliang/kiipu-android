package com.mycreat.kiipu.utils;

import android.content.Context;
import android.os.Environment;
import com.android.annotations.Nullable;
import com.mycreat.kiipu.core.KiipuApplication;
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

    public static int writeToFile(String str, String path) throws IOException {
        File file = new File(path);
        //已存在该文件，切不为空，不可删除修改则直接返回失败Code -1
        if(file.exists() && !file.delete() && file.length() > 0 && !file.canWrite()){
            return -1;
        }

        RandomAccessFile rf = new RandomAccessFile(file, "rw");
        rf.seek(0);//从零开始写
        rf.writeUTF(str);
        rf.close();

        if(file.exists() && file.length() == str.length()){
            return 0;
        }else if(file.exists()){
            return -2;
        }else{
            return -3;
        }

    }

    /**
     *
     * @param path
     * @return
     */
    @Nullable
    public static String readUtfFromFile(String path){
        File file = new File(path);
        if(file.exists() && file.isFile() && file.canRead() && file.length() > 0){
            RandomAccessFile rf = null;
            try {
                rf = new RandomAccessFile(file, "r");
                return rf.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.e(e.getMessage());
                return null;
            }finally {
                if(rf != null){
                    try {
                        rf.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e(e.getMessage());
                    }
                }
            }
        }else{
            return null;
        }
    }

    public static String getTemplateCacheDir(){
        return KiipuApplication.appContext.getFilesDir().getAbsolutePath() + File.separator + "template";
    }
    public static void prepareDownFile(){
        isGoOn = true;
    }

    public static void stopDownFile(){
        isGoOn = false;
    }
}
