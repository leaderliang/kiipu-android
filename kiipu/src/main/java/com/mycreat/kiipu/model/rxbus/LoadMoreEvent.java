package com.mycreat.kiipu.model.rxbus;

import android.support.annotation.IntDef;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.rxbus.RxBus;

import java.util.List;

/**
 * 用于详情弹窗加载更多
 * Created by zhanghaihai on 2017/8/29.
 */
public class LoadMoreEvent {
    public int action;
    public List<Bookmark> bookmarks;

    public LoadMoreEvent(int action, List<Bookmark> bookmarks) {
        this.action = action;
        this.bookmarks = bookmarks;
    }

    public void post(){
        RxBus.Companion.getDefault().post(this);
    }
}
