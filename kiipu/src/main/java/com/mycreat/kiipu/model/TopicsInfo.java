package com.mycreat.kiipu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liangyanqiao on 2017/7/6.
 */
public class TopicsInfo {
    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("id")
    public String id;
}
