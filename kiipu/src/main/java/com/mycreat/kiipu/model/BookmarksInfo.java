package com.mycreat.kiipu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class BookmarksInfo {

    @SerializedName("img")
    public String img;
    @SerializedName("icon")
    public String icon;
    @SerializedName("url")
    public String url;
    @SerializedName("title")
    public String title;
    @SerializedName("note")
    public String introduce;

}
