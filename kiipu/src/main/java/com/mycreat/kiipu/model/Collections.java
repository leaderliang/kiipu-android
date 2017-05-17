package com.mycreat.kiipu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * 书签夹
 * tip: @SerializedName("id") 括号里的值必须和返回的字段一一对应
 * TODO
 */
public class Collections {

    @Expose
    @SerializedName("id")
    public String collectionId;
    @Expose
    @SerializedName("name")
    public String collectionName;



}
