package com.mycreat.kiipu.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * tip: @SerializedName("id") 括号里的值必须和返回的字段一一对应
 * TODO
 */
public class BookmarkExt{
    @Expose
    @SerializedName("topics")
    public List<Topic> topics;

    @Expose
    @SerializedName("userlink")
    public String userLink;
    @Expose
    @SerializedName("username")
    public String userName;
    @Expose
    @SerializedName("avatar")
    public String avatar;

    public class Topic{
        @Expose
        @SerializedName("id")
        public String id;
        @Expose
        @SerializedName("name")
        public String name;
    }

}
