package com.mycreat.kiipu.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * tip: @SerializedName("id") 括号里的值必须和返回的字段一一对应
 * TODO
 */
public class Bookmark implements MultiItemEntity {

    @Expose
    @SerializedName("id")
    public String id;
    /**
     * type 1 带图片
     * type 2 带文字
     * type 3 网站
     */
    @Expose
    @SerializedName("type")
    public String type;
    @Expose
    @SerializedName("data")
    public BookmarksInfo info;
    @Expose
    @SerializedName("collection_id")
    public String collectionId;
    @Expose
    @SerializedName("theme")
    public String viewTheme;

    @Expose
    @SerializedName("create_time")
    public String createOn;
    @Expose
    @SerializedName("update_time")
    public String updateOn;

    /*卡片详情模板相关*/
    @Expose
    @SerializedName("ext")
    public BookmarkExt ext;
    /**
     * template 标明了该卡片依赖的模板文件名
     * */
    @Expose
    @SerializedName("tmpl")
    public String tmplName;
    @Expose
    @SerializedName("tmpl_ver")
    public int tmplVersion;


    @Override
    public int getItemType() {
        return Integer.valueOf(type);
    }
}
