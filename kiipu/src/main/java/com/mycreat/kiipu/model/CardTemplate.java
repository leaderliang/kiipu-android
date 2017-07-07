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
    public String id;
    @Expose
    @SerializedName("ext")
    public CardInfo cardinfo;
    @Expose
    @SerializedName("tmplInfo")
    public CardTemplateInfo cardTemplateInfo;


}
