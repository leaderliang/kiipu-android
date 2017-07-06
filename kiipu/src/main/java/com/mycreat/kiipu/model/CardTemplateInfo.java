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
    private String templatePath;
    @Expose
    @SerializedName("version")
    private String templateVersion;
    @Expose
    @SerializedName("name")
    private String templateName;


}
