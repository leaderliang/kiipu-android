package com.mycreat.kiipu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liangyanqiao on 2017/7/6.
 */
public class CardInfo {

    @Expose
    @SerializedName("topics")
    private List<TopicsInfo> cardInfos;

}
