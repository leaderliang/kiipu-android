package com.mycreat.kiipu.model;

import android.support.v7.widget.RecyclerView;
import com.mycreat.kiipu.view.bookmark.BookmarkDetailAdapter;

import java.util.List;

/**
 * DataBinding model for BookmarkDetailDialog
 * Created by zhanghaihai on 2017/8/16.
 */
public class BookmarkDialog {

    private BookmarkDetailAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Bookmark> bookmarks;
    private int currentPosition;

    public BookmarkDetailAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BookmarkDetailAdapter adapter) {
        this.adapter = adapter;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }
}
