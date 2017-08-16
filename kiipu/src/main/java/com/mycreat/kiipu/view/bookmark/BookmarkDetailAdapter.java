package com.mycreat.kiipu.view.bookmark;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.databinding.BookmarkDetailBinding;
import com.mycreat.kiipu.databinding.BookmarkDetailFooterBinding;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.CustomTabsUtils;
import com.mycreat.kiipu.utils.LogUtil;
import com.mycreat.kiipu.utils.ViewUtils;
import com.mycreat.kiipu.utils.bind.BindView;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 书签详情列表Adapter
 * Created by zhanghaihai on 2017/7/26.
 */
public class BookmarkDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Activity activity;
    private List<Bookmark> bookmarks;
    private final int TYPE_BOOKMARK = 1;
    private final int TYPE_FOOTER = 2;
    private int pbVisibility = View.VISIBLE;
    private int msgVisibility = View.INVISIBLE;
    private String msg = "";
    private FooterHolder footerHolder;

    public BookmarkDetailAdapter(Activity activity, List<Bookmark> bookmarks) {
        this.activity = new WeakReference<>(activity).get() ;
        this.bookmarks = bookmarks;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BOOKMARK:
                LogUtil.d("TYPE_BOOKMARK:");
                BookmarkDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_card_detail_dialog, parent, false);
                return new BookmarkHolder(binding.getRoot(), binding, activity);
            case TYPE_FOOTER:
                LogUtil.d("TYPE_FOOTER:");
                BookmarkDetailFooterBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_card_detail_dialog_footer, parent, false);
                return new FooterHolder(footerBinding.getRoot(), footerBinding);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BookmarkHolder){
            LogUtil.d("BookmarkHolder:" + position + "/"+ getItemCount());
            ((BookmarkHolder)holder).update(bookmarks.get(position));
        }else if(holder instanceof FooterHolder){
            LogUtil.d("FooterHolder:" + position + "/"+ getItemCount());
            ((FooterHolder) holder).update();
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

    private static class BookmarkHolder extends RecyclerView.ViewHolder{
        private BookmarkDetailBinding mBinding;
        private Activity activity;
        @BindView(R.id.wv_ext_detail)
        BookmarkTemplateWebVIew tmplWebView;
        public BookmarkHolder(View itemView, BookmarkDetailBinding nBinding, Activity activity) {
            this(itemView);
            mBinding = nBinding;
            this.activity = activity;
        }
        public BookmarkHolder(View itemView) {
            super(itemView);
            ViewUtils.bindViews(itemView, this);
        }

        public void update(Bookmark bookmark){
            //处理滑动冲突
//            tmplWebView.setOnTouchListener(new View.OnTouchListener() {
//                private float oldX;
//                private float oldY;
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    //按下事件不拦截
//                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                        tmplWebView.requestDisallowInterceptTouchEvent(false);
//                        oldX = event.getX();
//                        oldY = event.getY();
//                        return false;
//                    }else{
//                        float distanceX = event.getX() - oldX;
//                        float distanceY = event.getY() - oldY;
//                        //横向移动大于竖向移动 不拦截
//                        if(Math.abs(distanceX) > Math.abs(distanceY)) {
//                            tmplWebView.requestDisallowInterceptTouchEvent(false);
//                            return false;
//                        }else{
//                            tmplWebView.requestDisallowInterceptTouchEvent(true);
//                            return true;
//                        }
//                    }
//                }
//            });
            if(mBinding != null){
                mBinding.setBookmark(bookmark);
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

    class FooterHolder extends RecyclerView.ViewHolder{

        private BookmarkDetailFooterBinding mBinding;
        public FooterHolder(View itemView, BookmarkDetailFooterBinding nBinding) {
            super(itemView);
            mBinding = nBinding;
        }

        public FooterHolder(View itemView) {
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
        if(footerHolder != null) {
            msgVisibility = View.INVISIBLE;
            pbVisibility = View.VISIBLE;
        }
    }

    public void loadedMore(){
        if(footerHolder != null) {
            msgVisibility = View.VISIBLE;
            pbVisibility = View.INVISIBLE;
        }
    }

    public void addBookMarks(List<Bookmark> nBookmarks){
        bookmarks.addAll(nBookmarks);
    }

}
