package com.mycreat.kiipu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by leaderliang on 2017/3/31.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class CardTemplateInfo {

    @Expose
    @SerializedName("path")
    public String templatePath;
    @Expose
    @SerializedName("version")
    public String templateVersion;
    @Expose
    @SerializedName("name")
    public String templateName;


}
