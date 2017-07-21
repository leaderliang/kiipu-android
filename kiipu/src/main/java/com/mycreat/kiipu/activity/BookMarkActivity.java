package com.mycreat.kiipu.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.cocosw.bottomsheet.BottomSheet;
import com.cocosw.bottomsheet.BottomSheetHelper;
import com.github.clans.fab.FloatingActionButton;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.BookMarkAdapter;
import com.mycreat.kiipu.core.AppManager;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.*;
import com.mycreat.kiipu.utils.*;
import com.mycreat.kiipu.view.BookmarkTemplateWebVIew;
import com.mycreat.kiipu.view.KiipuRecyclerView;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 主书签界面
 *
 * @author leaderliang
 */
public class BookMarkActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    /* 0 pull; 1 load more */
    private int REFRESH_TYPE = 0;

    private String itemId = "";

    private FloatingActionButton mFloatingActionButton;

    private Toolbar toolbar;

    private DrawerLayout drawer;
    /* left menu */
    private NavigationView navigationView;

    private BookMarkAdapter adapter;

    private ProgressBar mProgress;

    private SwipeRefreshLayout swipeToLoadLayout;

    private KiipuRecyclerView recyclerView;

    private ImageView mIvClose, mIvIcon, mIvDetail;

    private BookmarkTemplateWebVIew extDetail;

    private ImageView mIvUserHeader;

    private TextView mTvTitle, mTvUrl, mTvUserName, mTvIntroduce;

    private UserInfo mUserInfo;

    private View headerView;

    private long nowTime;

    public Button finalButton;

    private String inputName, collectionId = Constants.ALL_COLLECTION, viewTheme;

    private MenuItem menuAllItem;

    private List<Bookmark> mBookmarkList = new ArrayList<>();

    private List<Bookmark> requestData = new ArrayList<>();

    private List<Collections> mCollectionList = new ArrayList<>();

    private int mScrollThreshold;

    private Button mBtLogOut;

    private boolean isDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        useBaseLayout = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        initViews();
        initData();
        initListener();

    }


    public void initViews() {
        /*  main layout*/
        drawer = initViewById(R.id.drawer_layout);
        /* left menu */
        navigationView = initViewById(R.id.nav_view);

        headerView = navigationView.getHeaderView(0);

        menuAllItem = navigationView.getMenu().findItem(R.id.nav_all_bookmark);

        mIvUserHeader = (ImageView) headerView.findViewById(R.id.iv_user_icon);

        mTvUserName = (TextView) headerView.findViewById(R.id.tv_user_name);

        mBtLogOut = (Button) headerView.findViewById(R.id.bt_log_out);

        toolbar = initViewById(R.id.toolbar);

        mProgress = initViewById(R.id.pb_view);

        swipeToLoadLayout = initViewById(R.id.swipe_refresh_layout);

        recyclerView = initViewById(R.id.recyclerView);

        mFloatingActionButton = initViewById(R.id.floating_action_bt);

        mFloatingActionButton.hide(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFloatingActionButton.show(true);
                mFloatingActionButton.setShowAnimation(AnimationUtils.loadAnimation(BookMarkActivity.this, R.anim.show_from_bottom));
                mFloatingActionButton.setHideAnimation(AnimationUtils.loadAnimation(BookMarkActivity.this, R.anim.hide_to_bottom));
            }
        }, 300);

        mScrollThreshold = getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (dy > 0) {
                        mFloatingActionButton.hide(true);
                    } else {
                        mFloatingActionButton.show(true);
                    }
                }
            }

            /**
             *
             * 第二个参数
             * SCROLL_STATE_IDLE 停止滑动
             * SCROLL_STATE_DRAGGING 当屏幕滚动且用户使用的触碰或手指还在屏幕上
             * SCROLL_STATE_SETTLING 由于用户的操作，屏幕产生惯性滑动
             * @param recyclerView
             * @param newState
             */
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }
        });

        swipeToLoadLayout.setColorSchemeColors(Color.parseColor(Constants.DEFAULT_COLOR));
        swipeToLoadLayout.setRefreshing(true);
        toolbar.setTitle("");// default null
        toolbar.setLogo(R.drawable.login_logo_text);
        setSupportActionBar(toolbar);
        /* set item all checked default */
        menuAllItem.setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        // 注释掉可更换 navigationIcon
//        toggle.syncState();

        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);

        GridLayoutManager mGirdLayoutManager = new GridLayoutManager(this, Constants.SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mGirdLayoutManager);

        adapter = new BookMarkAdapter(mBookmarkList);
        adapter.setOnLoadMoreListener(BookMarkActivity.this, recyclerView);
//        adapter.openLoadAnimation(new CustomAnimation());//  也可以自定义 Anim
        adapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bookmark bookmark = requestData.get(position);
                viewTheme = TextUtils.isEmpty(bookmark.viewTheme) ? Constants.DEFAULT_COLOR_VALUE : bookmark.viewTheme;
                String url = bookmark.info.getUrl();
//                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//                builder.setToolbarColor(Color.parseColor("#FFB74D"));
//                CustomTabsIntent customTabsIntent = builder.build();
//                customTabsIntent.launchUrl(BookMarkActivity.this, Uri.parse(url));
                CustomTabsUtils.showCustomTabsView(BookMarkActivity.this, url, viewTheme);
            }
        });
    }


    public void initListener() {
        setOnClick(mFloatingActionButton);
        setOnClick(mBtLogOut);
        // 左侧菜单显示隐藏事件监听，左侧菜单点击选中 selector
        navigationView.setNavigationItemSelectedListener(this);
        swipeToLoadLayout.setOnRefreshListener(this);
    }

    public void initData() {
        //showProgressDialog(this);
        onRefresh();
        getCollectionList();
        getUserInfo();
    }


    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_bt:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUtil.showCommonDialog(BookMarkActivity.this, "Hello Dialog", "Is this material design?",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                            }
                        })
                        .setActionTextColor(Color.parseColor("#0097A7"))
                        .setDuration(4000)
                        .show();
                break;
            case R.id.bt_log_out:
                DialogUtil.showCommonDialog(this, null, "退出 Kiipu!", false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                }, null);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_setting:
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
//                if(recyclerView.getLayoutManager().)
//                LogUtil.e("recyclerView.getLayoutManager()"+recyclerView.getLayoutManager());
                return true;
            case R.id.action_add:
                ToastUtil.showToastShort("添加书签");
                return true;
            case R.id.action_search:
                ToastUtil.showToastShort("搜索书签");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* menu item click */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        mProgress.setVisibility(View.VISIBLE);
        adapter.setEnableLoadMore(false);
        toolbar.setLogo(null);
        REFRESH_TYPE = 0;
        switch (item.getItemId()) {
            case R.id.nav_all_bookmark: /* all bookmarks  传0  或者不传 collection_id */
                // Handle the camera action
//                startActivity(new Intent(this, RecycleViewActivity.class));
                swipeToLoadLayout.setRefreshing(true);
                collectionId = Constants.ALL_COLLECTION;
                toolbar.setTitle(null);
                toolbar.setLogo(R.drawable.login_logo_text);
                getBookmarkList();
                break;
            case R.id.nav_inbox: /* all bookmarks  传0  或者不传 collection_id */
                collectionId = Constants.INBOX;
                toolbar.setTitle(getString(R.string.inbox));
                swipeToLoadLayout.setRefreshing(true);
                getBookmarkList();
                break;
            default:
                swipeToLoadLayout.setRefreshing(false);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRefresh() {
        adapter.setEnableLoadMore(false);
        REFRESH_TYPE = 0; // PULL
        getBookmarkList();
    }


    @Override
    public void onLoadMoreRequested() {
        swipeToLoadLayout.setRefreshing(false);
        adapter.setEnableLoadMore(true);
        REFRESH_TYPE = 1; // LOAD MORE
        getBookmarkList();
    }

    /**
     * 获取书签
     */
    private void getBookmarkList() {
        String lastItemId = mBookmarkList.size() > 0 ? mBookmarkList.get(mBookmarkList.size() - 1).id : "";
        itemId = REFRESH_TYPE == 0 ? "" : lastItemId;
        Call<List<Bookmark>> call = KiipuApplication.mRetrofitService.getBookmarkList(userAccessToken, Constants.PAGE_SIZE, itemId, collectionId);
        call.enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                if (!CollectionUtils.isEmpty(response.body())) {
                    mBookmarkList = response.body();
                    if (REFRESH_TYPE == 0) {
                        requestData.clear();
                        requestData.addAll(mBookmarkList);
                        adapter.setNewData(mBookmarkList);
                        swipeToLoadLayout.setRefreshing(false);
                    } else if (REFRESH_TYPE == 1) {// load more
                        requestData.addAll(mBookmarkList);
                        adapter.addData(mBookmarkList);
                        adapter.loadMoreComplete();// 数据加载完成
                        if (mBookmarkList.size() < Constants.PAGE_SIZE) {// 没有更多数据
                            adapter.loadMoreEnd(false);
                        }
                    }
                } else {
                    swipeToLoadLayout.setRefreshing(false);
                    adapter.loadMoreComplete();
                    adapter.loadMoreEnd(false);
                    if (REFRESH_TYPE == 0) {// when refresh
                        requestData.clear();
                        mBookmarkList.clear();
                        adapter.setNewData(mBookmarkList);
                        mRequestErrorLayout.setErrorText("暂时还没有书签呦~");
                        adapter.setEmptyView(mRequestErrorLayout);
                    }
                }
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                mProgress.setVisibility(View.GONE);
                swipeToLoadLayout.setRefreshing(false);
                adapter.loadMoreFail();
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    /**
     * 获取收藏夹列表
     */
    private void getCollectionList() {
        Call<List<Collections>> call = KiipuApplication.mRetrofitService.getCollectionList(userAccessToken);
        call.enqueue(new Callback<List<Collections>>() {
            @Override
            public void onResponse(Call<List<Collections>> call, Response<List<Collections>> response) {
                mCollectionList = response.body();
                if (!CollectionUtils.isEmpty(mCollectionList)) {
                    addLeftMenu(mCollectionList, true);
                }
            }

            @Override
            public void onFailure(Call<List<Collections>> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }


    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        Call<UserInfo> call = KiipuApplication.mRetrofitService.getUserInfo(userAccessToken);
        Log.e("getUserInfo", "userAccessToken " + userAccessToken);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                mUserInfo = response.body();
//                mIvUserHeader  mTvUserName
                if (mUserInfo != null) {
                    GlideUtil.getInstance().loadCircleImage(mIvUserHeader, mUserInfo.avatarUrl, R.drawable.default_header_icon);
                    mTvUserName.setText(mUserInfo.nickName);
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });

    }

    /**
     * 创建书签
     *
     * @param collectionName
     */
    private void createCollection(String collectionName) {
        Call<Collections> call = KiipuApplication.mRetrofitService.createCollection(userAccessToken, collectionName);
        call.enqueue(new Callback<Collections>() {
            @Override
            public void onResponse(Call<Collections> call, Response<Collections> response) {
                if (response.body() != null) {
                    ToastUtil.showToastShort("创建书签成功~");
                    Collections collection = response.body();
                    mCollectionList.add(0, collection);
                    addLeftMenu(mCollectionList, false);
                }
            }

            @Override
            public void onFailure(Call<Collections> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    /**
     * delete item
     */
    private void requestDeleteItem(final int position) {
        Call<Bookmark> call = KiipuApplication.mRetrofitService.deleteBookmark(userAccessToken, requestData.get(position).id);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                requestData.remove(position);
                adapter.remove(position);
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    /**
     * move bookmark
     */
    private void requestMoveBookmark(final int dataPosition, final int collectionId) {
        String bookmarkId = requestData.get(dataPosition).id;
        Call<Bookmark> call = KiipuApplication.mRetrofitService.moveBookmark(userAccessToken, bookmarkId, mCollectionList.get(collectionId).collectionId);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                requestData.remove(dataPosition);
                adapter.remove(dataPosition);
                Snackbar.make(mFloatingActionButton, "移动书签到 " + mCollectionList.get(collectionId).collectionName + " 成功", Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }

            @Override
            public void onFailure(Call<Bookmark> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }


    /**
     * 动态添加左侧菜单
     *
     * @param mCollectionList
     * @param isRequestMenu
     */
    private void addLeftMenu(List<Collections> mCollectionList, boolean isRequestMenu) {
        navigationView.getMenu().findItem(R.id.item_collection).setTitle("收藏夹");

        if (isRequestMenu) {
            for (int i = 1; i < mCollectionList.size(); i++) {
                final String firstName = mCollectionList.get(0).collectionName;
                final String collectionName = mCollectionList.get(i).collectionName;
                final int finalI = i;
                Glide.with(BookMarkActivity.this)
                        .load(mCollectionList.get(i).menuIcon)
                        .into(new SimpleTarget<GlideDrawable>() { // 图片加载回调的Target实现类
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                // the first menu view
                                navigationView.getMenu().findItem(R.id.nav_share)
                                        .setTitle(firstName)
                                        .setIcon(resource)//动态添加menu
                                        .setOnMenuItemClickListener(new OnMenuItemClickListener());

                                // 图片加载成功时回调的方法
                                navigationView.getMenu().add(0, finalI, finalI, collectionName)
                                        .setIcon(resource)//动态添加menu
                                        .setOnMenuItemClickListener(new OnMenuItemClickListener());
                            }
                        });


            }
        } else {// 调用添加按钮后，重新设置之前menu的 name
            for (int i = 1; i < mCollectionList.size(); i++) {
                final String collectionName = mCollectionList.get(i).collectionName;
                final int finalI = i;
                Glide.with(BookMarkActivity.this)
                        .load(mCollectionList.get(i).menuIcon)
                        .into(new SimpleTarget<GlideDrawable>() { // 图片加载回调的Target实现类
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                // 图片加载成功时回调的方法
                                navigationView.getMenu().findItem(finalI).setTitle(collectionName)
                                        .setIcon(resource)//动态添加menu
                                        .setOnMenuItemClickListener(new OnMenuItemClickListener());
                            }
                        });

            }
        }
        // 添加书签按钮事件操作
        navigationView.getMenu().add(0, mCollectionList.size(), mCollectionList.size(), "添加书签")
                .setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_add))
                .setOnMenuItemClickListener(new OnAddMenuItemClickListener());
    }

    /**
     * CardView More
     */
    private class OnItemChildClickListener implements BaseQuickAdapter.OnItemChildClickListener {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                case R.id.img_more_info:
                    showListPopupWindow(position);
                    break;
            }
        }
    }


    private void showBookmarkDetailDialog(final int position) {
        String htmlPath =  requestData.get(position).tmplName +"/"+requestData.get(position).tmplVersion+".html";
        Call<String> call = KiipuApplication.mRetrofitTemplateService.requestHtml(htmlPath);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogUtil.e("html response.body()----->"+response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });


        View view = DialogUtil.showCanSetViewDialog(this,
                R.layout.view_card_detail_dialog,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        BookmarksInfo mBookmarksInfo = requestData.get(position).info;
        extDetail = (BookmarkTemplateWebVIew) view.findViewById(R.id.wv_ext_detail);
        mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mTvUrl = (TextView) view.findViewById(R.id.tv_url);
        mIvDetail = (ImageView) view.findViewById(R.id.iv_detail);
        mTvIntroduce = (TextView) view.findViewById(R.id.tv_introduce);
        extDetail.setOnLinkClickListener(new BookmarkTemplateWebVIew.OnLinkClickListener() {
            @Override
            public void onClick(@NotNull String url, @NotNull Bookmark bookmark) {
                String viewTheme = TextUtils.isEmpty(bookmark.viewTheme) ? Constants.DEFAULT_COLOR_VALUE : bookmark.viewTheme;
                CustomTabsUtils.showCustomTabsView(BookMarkActivity.this, url, viewTheme);
            }
        });
        GlideUtil.getInstance().loadImage(mIvIcon, mBookmarksInfo.getIcon(), R.drawable.default_logo_small, true);
        mTvTitle.setText(mBookmarksInfo.getTitle());
        mTvUrl.setText(mBookmarksInfo.getUrl());
        if (requestData.get(position).type.equals("1")) {
            mIvDetail.setVisibility(View.VISIBLE);
            mTvIntroduce.setVisibility(View.GONE);
            GlideUtil.getInstance().loadImage(mIvDetail, mBookmarksInfo.getImg(), true);
        } else {
            mIvDetail.setVisibility(View.GONE);
            mTvIntroduce.setVisibility(View.VISIBLE);
            mTvIntroduce.setText(mBookmarksInfo.getIntroduce());
        }

        if(requestData.get(position).tmplName != null){
            extDetail.refresh(requestData.get(position));
        }
    }


    private void showListPopupWindow(final int dataPosition) {

        BottomSheet sheet = new BottomSheet.Builder(this).sheet(R.menu.more_info).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BookMarkActivity.this.onMoreInfoItemClick(dataPosition, which);
            }
        }).build();
        sheet.show();
    }

    private void onMoreInfoItemClick(int dataPosition, int which) {
        switch (which) {
            case R.id.show_detail:
                showBookmarkDetailDialog(dataPosition);
                break;
            case R.id.move_to:
                showMoveBookmarkDialog(dataPosition);
                break;
            case R.id.delete:
                requestDeleteItem(dataPosition);
                break;
            case R.id.share:
                showShareDialog(dataPosition);
                break;
        }
    }

    private void showShareDialog(int position) {
        BottomSheet sheet = getShareActions(requestData.get(position).info.getUrl()).title("分享到：").limit(R.integer.no_limit).build();
        sheet.show();
    }

    private BottomSheet.Builder getShareActions(String text) {
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        return BottomSheetHelper.shareAction(this, shareIntent);
    }

    private void showMoveBookmarkDialog(final int dataPosition) {
        Intent intent = new Intent(this, CollectionActivity.class);
        intent.putExtra("dataPosition", dataPosition);
        intent.putExtra("currentBookmarkId", requestData.get(dataPosition).id);
        startActivityForResult(intent, Constants.REQUEST_MOVE_BOOKMARK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.RESULT_MOVE_BOOKMARK_CODE:
                if (data != null) {
                    int dataPosition = data.getIntExtra("dataPosition", 0);
                    String collectionName = data.getStringExtra("collectionName");
                    requestData.remove(dataPosition);
                    adapter.remove(dataPosition);
                    Snackbar.make(mFloatingActionButton, "移动书签到 " + collectionName + " 成功", Snackbar.LENGTH_LONG)
                            .setDuration(2500)
                            .show();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - nowTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序并清空登录信息", Toast.LENGTH_SHORT).show();
                nowTime = System.currentTimeMillis();
            } else {
                logOutApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void logOutApp() {
        SharedPreferencesUtil.removeKey(mContext, Constants.ACCESS_TOKEN);
        SharedPreferencesUtil.removeKey(mContext, Constants.USER_ID);
        LogUtil.e("when logOutApp ACCESS_TOKEN " + SharedPreferencesUtil.getData(mContext, Constants.ACCESS_TOKEN, ""));
        finish();
        AppManager.getAppManager().appExit(this);
    }

    private void logOut() {
        SharedPreferencesUtil.removeKey(mContext, Constants.ACCESS_TOKEN);
        SharedPreferencesUtil.removeKey(mContext, Constants.USER_ID);
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }


    private class OnAddMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            //@return Return true to consume this click and prevent others from executing
            DialogUtil.showEditDialog(BookMarkActivity.this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createCollection(inputName);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }, new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    inputName = s.toString().trim();
                    if (inputName.isEmpty()) {
                        finalButton.setEnabled(false);
                    } else {
                        finalButton.setEnabled(true);
                    }
                }
            }, new DialogUtil.ButtonCallBack() {
                @Override
                public void buttonCallBack(Button btn) {
                    finalButton = btn;
                    finalButton.setEnabled(false);
                }
            });
            return false;
        }
    }

    /**
     * leftMenu clickListener
     */
    private class OnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            item.setCheckable(true);
            item.setChecked(true);
            toolbar.setTitle(item.getTitle());
            collectionId = (item.getItemId() == R.id.nav_share) ? mCollectionList.get(0).collectionId : mCollectionList.get(item.getItemId()).collectionId;
            swipeToLoadLayout.setRefreshing(true);
            adapter.setEnableLoadMore(false);
            REFRESH_TYPE = 0;
            getBookmarkList();

            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String clipStr = ClipboardUtils.get(this);
        if (!TextUtils.isEmpty(clipStr)) {
//            ToastUtil.showToastShort(this, "粘贴板有数据哦~");

        }
    }





}
