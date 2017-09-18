package com.mycreat.kiipu.view.bookmark;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.databinding.BookmarkDetailBinding;
import com.mycreat.kiipu.databinding.BookmarkDetailFooterBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarkDialogEndItem;
import com.mycreat.kiipu.model.BookmarkDialogItem;
import com.mycreat.kiipu.model.rxbus.LoadMoreEvent;
import com.mycreat.kiipu.rxbus.RxBus;
import com.mycreat.kiipu.utils.*;
import com.mycreat.kiipu.utils.bind.BindView;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 书签详情列表Adapter
 * Created by zhanghaihai on 2017/7/26.
 */
public class BookmarkDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, BookmarkTemplateWebVIew.OnScrollChangedListener{
    private Activity activity;
    private List<Bookmark> bookmarks;
    private final int TYPE_BOOKMARK = 1;
    private final int TYPE_FOOTER = 2;

    private String msg = "";
    private EndViewHolder endViewHolder;
    BookmarkDialogEndItem bookmarkDialogEndItem;
    RecyclerView recyclerView;
    private Lock loadMoreLock = new ReentrantLock();
    public BookmarkDetailAdapter(Activity activity, RecyclerView recyclerView) {
        this.activity = new WeakReference<>(activity).get() ;
        bookmarks = new ArrayList<>();
        bookmarkDialogEndItem = new BookmarkDialogEndItem();
        this.recyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BOOKMARK:
                BookmarkDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_card_detail_dialog, parent, false);
                return new BookmarkHolder(binding.getRoot(), binding, activity, this);
            case TYPE_FOOTER:
                BookmarkDetailFooterBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_card_detail_dialog_footer, parent, false);
                return new EndViewHolder(footerBinding.getRoot(), footerBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BookmarkHolder){
            ((BookmarkHolder)holder).update(bookmarks.get(position), this);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() - 1){
            LogUtil.d(position + "/"+ getItemCount());
            return TYPE_FOOTER;
        }
        else {
            LogUtil.d(position + "/"+ getItemCount());
            return TYPE_BOOKMARK;
        }
    }

    @Override
    public int getItemCount() {
        return bookmarks.size() + 1;
    }

    @Override
    public void onClick(View v) {
        RxBus.Companion.getDefault().post(new BookmarkDetailDialog.DialogEvent());
    }

    @Override
    public void onScrollChanged(int dx, int dy) {
        if(Math.abs(dx) < Math.abs(dy)){
            if(recyclerView.getLayoutManager() instanceof BookmarkDetailDialog.CustomLinearLayoutManager) {
                BookmarkDetailDialog.CustomLinearLayoutManager customLinearLayoutManager = (BookmarkDetailDialog.CustomLinearLayoutManager) recyclerView.getLayoutManager();
                customLinearLayoutManager.setCanScrollHorizontally(false);
            }else{
                BookmarkDetailDialog.CustomLinearLayoutManager customLinearLayoutManager = (BookmarkDetailDialog.CustomLinearLayoutManager) recyclerView.getLayoutManager();
                customLinearLayoutManager.setCanScrollHorizontally(true);
            }
        }else{
            BookmarkDetailDialog.CustomLinearLayoutManager customLinearLayoutManager = (BookmarkDetailDialog.CustomLinearLayoutManager) recyclerView.getLayoutManager();
            customLinearLayoutManager.setCanScrollHorizontally(true);
        }
    }

    private static class BookmarkHolder extends RecyclerView.ViewHolder {
        private BookmarkDetailBinding mBinding;
        private Activity activity;
        @BindView(R.id.wv_ext_detail)
        BookmarkTemplateWebVIew tmplWebView;
        @BindView(R.id.bd_bar_close_btn)
        ImageView closeBtn;
        BookmarkTemplateWebVIew.OnScrollChangedListener onScrollChangedListener;
        public BookmarkHolder(View itemView, BookmarkDetailBinding nBinding, Activity activity, BookmarkTemplateWebVIew.OnScrollChangedListener onScrollChangedListener) {
            this(itemView);
            mBinding = nBinding;
            this.activity = activity;
            this.onScrollChangedListener = onScrollChangedListener;
            ViewUtils.bindViews(itemView, this);
            tmplWebView.setOnScrollChangeListener(onScrollChangedListener);
        }
        public BookmarkHolder(View itemView) {
            super(itemView);
        }

        public void update(Bookmark bookmark, View.OnClickListener onClickListener){
            if(mBinding != null){
                closeBtn.setOnClickListener(onClickListener);
                final BookmarkDialogItem item = new BookmarkDialogItem();
                item.vibRantColor.set(StringUtils.isEmpty(bookmark.viewTheme) ?
                        ContextCompat.getColor(KiipuApplication.appContext, R.color.colorPrimary)
                        : Color.parseColor("#" + bookmark.viewTheme));
                item.setBookmark(bookmark);

                mBinding.setItem(item);
                mBinding.setOnLinkClickListener(new BookmarkTemplateWebVIew.OnLinkClickListener() {
                    @Override
                    public void onClick(@NotNull String url, @NotNull Bookmark bookmark) {
                        if(activity != null) {
                            String viewTheme = TextUtils.isEmpty(bookmark.viewTheme) ? Constants.DEFAULT_COLOR_VALUE : bookmark.viewTheme;
                            CustomTabsUtils.showCustomTabsView(activity, url, viewTheme);
                        }
                    }
                });
                mBinding.executePendingBindings();
            }
        }
    }

    class EndViewHolder extends RecyclerView.ViewHolder{

        private BookmarkDetailFooterBinding mBinding;
        public EndViewHolder(View itemView, BookmarkDetailFooterBinding nBinding) {
            super(itemView);
            mBinding = nBinding;
            mBinding.setEndItem(bookmarkDialogEndItem);
            mBinding.executePendingBindings();
        }

        public EndViewHolder(View itemView) {
            super(itemView);
        }

    }

    /**
     * 初始化显示时需要重新判断是否还有更多需要显示
     */
    public void initLoad(){
        loadMoreLock.lock();
        bookmarkDialogEndItem.msgVisibility.set(View.GONE);
        bookmarkDialogEndItem.pbVisibility.set(View.VISIBLE);
        loadMoreLock.unlock();
    }

    /**
     * 没有更多书签了
     */
    public void noMore(){
        loadMoreLock.lock();
        bookmarkDialogEndItem.pbVisibility.set(View.GONE);
        bookmarkDialogEndItem.msgVisibility.set(View.VISIBLE);
        bookmarkDialogEndItem.msg.set(KiipuApplication.appContext.getString(R.string.no_more_bookmark));
        loadMoreLock.unlock();
    }

    public void addBookMarks(List<Bookmark> nBookmarks){
        bookmarks.addAll(nBookmarks);
    }

    public void setBookMarks(List<Bookmark> nBookmarks){
        bookmarks.clear();
        bookmarks.addAll(nBookmarks);
    }
}
