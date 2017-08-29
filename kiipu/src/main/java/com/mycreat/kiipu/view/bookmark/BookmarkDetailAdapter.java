package com.mycreat.kiipu.view.bookmark;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.databinding.BookmarkDetailBinding;
import com.mycreat.kiipu.databinding.BookmarkDetailFooterBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarkDialogItem;
import com.mycreat.kiipu.rxbus.RxBus;
import com.mycreat.kiipu.utils.*;
import com.mycreat.kiipu.utils.bind.BindView;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 书签详情列表Adapter
 * Created by zhanghaihai on 2017/7/26.
 */
public class BookmarkDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private Activity activity;
    private List<Bookmark> bookmarks;
    private final int TYPE_BOOKMARK = 1;
    private final int TYPE_FOOTER = 2;
    private int pbVisibility = View.VISIBLE;
    private int msgVisibility = View.INVISIBLE;
    private String msg = "";
    private EndViewHolder endViewHolder;

    public BookmarkDetailAdapter(Activity activity) {
        this.activity = new WeakReference<>(activity).get() ;
        bookmarks = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BOOKMARK:
                BookmarkDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_card_detail_dialog, parent, false);
                return new BookmarkHolder(binding.getRoot(), binding, activity);
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
        }else if(holder instanceof EndViewHolder){
            ((EndViewHolder) holder).update();
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

    private static class BookmarkHolder extends RecyclerView.ViewHolder{
        private BookmarkDetailBinding mBinding;
        private Activity activity;
        @BindView(R.id.wv_ext_detail)
        BookmarkTemplateWebVIew tmplWebView;
        @BindView(R.id.bd_bar_close_btn)
        ImageView closeBtn;

        public BookmarkHolder(View itemView, BookmarkDetailBinding nBinding, Activity activity) {
            this(itemView);
            mBinding = nBinding;
            this.activity = activity;
        }
        public BookmarkHolder(View itemView) {
            super(itemView);
            ViewUtils.bindViews(itemView, this);
        }

        public void update(Bookmark bookmark, View.OnClickListener onClickListener){
            if(mBinding != null){
                closeBtn.setOnClickListener(onClickListener);
                final BookmarkDialogItem item = new BookmarkDialogItem();
                item.vibRantColor.set(ColorUtil.Companion.getColor(R.color.colorPrimary));
                item.setBookmark(bookmark);
                item.setGlideListener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        item.vibRantColor.set(ContextCompat.getColor(KiipuApplication.appContext, R.color.colorPrimary));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Bitmap bm = BitmapUtil.drawable2Bitmap(resource);
                        int color = ColorUtil.Companion.getVibRantColor(bm, ContextCompat.getColor(KiipuApplication.appContext, R.color.colorPrimary));
                        item.vibRantColor.set(color);
                        return false;
                    }


                });
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
        }

        public EndViewHolder(View itemView) {
            super(itemView);
        }

        public void update() {
            mBinding.setMsg(msg);
            mBinding.setMsgVisibility(msgVisibility);
            mBinding.setPbVisibility(pbVisibility);
            mBinding.executePendingBindings();
        }
    }

    public void loadingMore(){
        if(endViewHolder != null) {
            msgVisibility = View.INVISIBLE;
            pbVisibility = View.VISIBLE;
        }
    }

    public void loadedMore(){
        if(endViewHolder != null) {
            msgVisibility = View.VISIBLE;
            pbVisibility = View.INVISIBLE;
        }
    }

    public void addBookMarks(List<Bookmark> nBookmarks){
        bookmarks.addAll(nBookmarks);
    }

}
