package com.mycreat.kiipu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * 用户信息
 * tip: @SerializedName("id") 括号里的值必须和返回的字段一一对应
 * TODO
 */
public class UserInfo {

    @Expose
    @SerializedName("_id")
    public String userId;
    @Expose
    @SerializedName("username")
    public String userName;
    @Expose
    @SerializedName("nickname")
    public String nickName;
    @Expose
    @SerializedName("avatar")
    public String avatarUrl;
    @Expose
    @SerializedName("gender")
    public Long gender;
    @Expose
    @SerializedName("address")
    public String address;


}
