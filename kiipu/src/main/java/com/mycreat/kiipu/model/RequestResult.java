package com.mycreat.kiipu.model;

import com.google.gson.annotations.SerializedName;

/**
 * TODO
 * Created by liangyanqiao on 2017/8/30.
 */
public class RequestResult<T> {

    @SerializedName("code")
    public int code;

    @SerializedName("msg")
    public String msg;

    public T data;

}
