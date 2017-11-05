package com.mycreat.kiipu.utils;

import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.mycreat.kiipu.BuildConfig;
import com.mycreat.kiipu.core.KiipuApplication;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 日志打印，支持多参数自动加空格，提供日志保存功能
 * Created by haihai.zhang on 2017/2/22.
 */

public class LogUtil {
    private static final String _TAG = "SUPER_V_PLAYER";
    private static final String _E_LOG_PATH = KiipuApplication.appContext.getCacheDir() + "/error_log";
    private static final File[] logDirs = ContextCompat.getExternalCacheDirs(KiipuApplication.appContext);
    private static final String _D_LOG_PATH = logDirs.length > 0 ? logDirs[0] + "/debug_log" : "";
    private static final String _I_LOG_PATH = logDirs.length > 0 ? logDirs[0] + "/info_log" : "";
    private static Queue<String> cacheLogs = new LinkedBlockingDeque<>();
    private static Queue<String> cacheInfoLogs = new LinkedBlockingDeque<>();

    static {
        if(BuildConfig.DEBUG)
            //每次都删除INFO log
            new File(_I_LOG_PATH + ".last.tmp").delete();
            //保留三份DEBUG log
            new File(_D_LOG_PATH + ".last.tmp").delete();
            new File(_D_LOG_PATH + ".last").renameTo(new File(_D_LOG_PATH + ".last.tmp"));
            new File(_D_LOG_PATH).renameTo(new File(_D_LOG_PATH + ".last"));
            Observable.interval(100, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    String log = cacheLogs.poll();
                    if(log != null ) {
                        try {
                            FileUtil.appendStrToFile(log + "\n", _D_LOG_PATH);
                        } catch (IOException e) {
                            //不做处理
                        }
                    }

                    log = cacheInfoLogs.poll();
                    if(log != null ) {
                        try {
                            FileUtil.appendStrToFile(log + "\n", _I_LOG_PATH);
                        } catch (IOException e) {
                            //不做处理
                        }
                    }
                }
            });
    }

    public static void i(Object ... msg){
        StringBuilder sb = new StringBuilder();
        for(Object m:msg){
            m = m + "";
            sb.append(m).append(" ");
        }
        String endMsg = getStackInfo(new Throwable()) + sb.toString();
        Log.i(_TAG, endMsg);
        cacheInfoLogs.add(endMsg);
    }

    public static void debug(String tag, Object ... msg){
        if(!BuildConfig.DEBUG) return;
        if(msg == null) return;
        StringBuilder sb = new StringBuilder();
        for(Object m:msg){
            m = m + "";
            sb.append(m).append(" ");
        }
        String endMsg = getStackInfo(new Throwable()) + sb.toString();
        Log.d(tag, endMsg);
        cacheLogs.add(endMsg);
    }

    public static void d(Object ... msg){
        debug(_TAG, msg);
    }

    private static String getStackInfo(Throwable throwable){
        if(throwable != null ){
            try{
                StackTraceElement[] s = throwable.getStackTrace();
                if(s != null && s.length > 1){
                    return "[" + s[2].getFileName() + ":" + s[2].getLineNumber() + "] ";
                }else{
                    if(s != null && s.length > 0){
                        return "[" + s[1].getFileName() + ":" + s[1].getLineNumber() + "] ";
                    }else{
                        return "";
                    }
                }
            }catch(Exception e){
                return "";
            }
        }else{
            return "";
        }

    }

    public static void e(Object ...msg) {
        StringBuilder sb = new StringBuilder();
        for(Object m:msg){
            m = m + "";
            sb.append(m).append(" ");
        }
        Log.e(_TAG, getStackInfo(new Throwable()) + sb.toString());
        try {
            File file = new File(_E_LOG_PATH);

            if(file.exists() && file.length() > 10485760 )
                if(file.delete()) return; //文件删除失败，不再追加数据
            FileUtil.appendStrToFile(sb.toString() + "\n", _E_LOG_PATH);
        } catch (IOException e) {
            //不做处理
        }
    }
}
