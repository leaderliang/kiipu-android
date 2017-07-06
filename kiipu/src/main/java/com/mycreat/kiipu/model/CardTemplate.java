package com.mycreat.kiipu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by leaderliang on 2017/7/6.
 * email: leaderliang.dev@gmail.com
 * TODO
 */
public class CardTemplate {

    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("ext")
    private CardInfo cardinfo;
    @Expose
    @SerializedName("tmplInfo")
    private CardTemplateInfo cardTemplateInfo;


}
