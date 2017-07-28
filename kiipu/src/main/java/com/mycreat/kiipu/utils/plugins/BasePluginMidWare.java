package com.mycreat.kiipu.utils.plugins;

/**
 * Created by zhanghaihai on 2017/7/23.
 */
public abstract class BasePluginMidWare {

    /**
     * 指定你要插件执行的操作
     */
    public enum ACTION{

    }

    /**
     * 执行操作
     * @param args
     * @return
     */
    public abstract Result doSomeThing(Object ...args);


    public abstract Object findPlugin(ACTION action);
    /**
     * 用于存储返回结果，code必须给定，若确实不需要可给0
     */
    public class Result{
        public Result(int code) {
            this.code = code;
        }

        public int code;
        public String str1;
        public String str2;
        public int int1;
        public int int2;
        public Object obj;
    }



}
