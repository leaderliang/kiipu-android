package com.mycreat.kiipu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * tip: @SerializedName("id") 括号里的值必须和返回的字段一一对应
 * TODO
 */
public class Bookmark {

    @Expose
    @SerializedName("id")
    public String id;
    /**
     * type 1 带图片
     * type 2 带文字
     */
    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("data")
    public BookmarksInfo info;
    @Expose
    @SerializedName("create_time")
    public String createOn;
    @Expose
    @SerializedName("update_time")
    public String updateOn;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BookmarksInfo getInfo() {
        return info;
    }

    public void setInfo(BookmarksInfo info) {
        this.info = info;
    }

    public String getCreateOn() {
        return createOn;
    }

    public void setCreateOn(String createOn) {
        this.createOn = createOn;
    }

    public String getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(String updateOn) {
        this.updateOn = updateOn;
    }
}
