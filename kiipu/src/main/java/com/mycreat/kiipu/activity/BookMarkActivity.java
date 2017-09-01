package com.mycreat.kiipu.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.*;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
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
import com.mycreat.kiipu.adapter.BookMarkListAdapter;
import com.mycreat.kiipu.core.AppManager;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.*;
import com.mycreat.kiipu.model.Collections;
import com.mycreat.kiipu.model.rxbus.LoadMoreEvent;
import com.mycreat.kiipu.rxbus.RxBus;
import com.mycreat.kiipu.rxbus.RxBusSubscribe;
import com.mycreat.kiipu.rxbus.ThreadMode;
import com.mycreat.kiipu.utils.*;
import com.mycreat.kiipu.view.bookmark.BookmarkDetailDialog;
import com.mycreat.kiipu.view.bookmark.BookmarkTemplateWebVIew;
import com.mycreat.kiipu.view.KiipuRecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 主书签界面
 *
 * @author leaderliang
 */
public class BookMarkActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    /* 0 pull; 1 load more */
    private int REFRESH_TYPE = Constants.REFRESH_TYPE_PULL;

    private String itemId = "";

    private FloatingActionButton mFloatingActionButton;

    private Toolbar toolbar;

    private DrawerLayout drawer;
    /* left menu */
    private NavigationView navigationView;

    private BookMarkAdapter mGridLayoutAdapter;

    private BookMarkListAdapter listAdapter;

    private ProgressBar mProgress;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private KiipuRecyclerView mRecyclerView;

    private ImageView mIvClose, mIvIcon, mIvDetail;

    private BookmarkTemplateWebVIew extDetail;

    private ImageView mIvUserHeader;

    private TextView mTvTitle, mTvUrl, mTvUserName, mTvIntroduce;

    private UserInfo mUserInfo;

    private View headerView;

    private long nowTime;

    private Button finalButton;

    private String inputName, collectionId = Constants.ALL_COLLECTION, viewTheme;

    private MenuItem menuAllItem;

    private List<Bookmark> mBookmarkList = new ArrayList<>();

    private List<Bookmark> requestData = new ArrayList<>();

    private List<Collections> mCollectionList = new ArrayList<>();

    private int mScrollThreshold;

    private Button mBtLogOut;

    private MenuItem menuSetting;

//    protected RecyclerView.LayoutManager mLayoutManager;

    protected LayoutManagerType mCurrentLayoutManagerType;

    private SearchView mSearchView;

    private ListPopupWindow mListPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        useBaseLayout = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        getInstanceState(savedInstanceState);
        initViews();
        initData();
        initListener();
        RxBus.Companion.getDefault().register(this);

    }

    private void getInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState.getSerializable(Constants.KEY_LAYOUT_MANAGER);
        }
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

        mSwipeRefreshLayout = initViewById(R.id.swipe_refresh_layout);

        mRecyclerView = initViewById(R.id.recyclerView);

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
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor(Constants.DEFAULT_COLOR));
        toolbar.setTitle("");// default null
        toolbar.setLogo(R.drawable.login_logo_text);
        setSupportActionBar(toolbar);
        /* set item all checked default */
        menuAllItem.setChecked(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
//        toggle.syncState(); /* 注释掉可更换 navigationIcon*/

        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
        mRecyclerView.addOnItemTouchListener(new onBookmarkItemClickListener());
    }

    public void initListener() {
        setOnClick(mFloatingActionButton);
        setOnClick(mBtLogOut);
        // 左侧菜单显示隐藏事件监听，左侧菜单点击选中 selector
        navigationView.setNavigationItemSelectedListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
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
                startActivity(new Intent(this, addBookmarkActivity.class));
                break;
            case R.id.bt_log_out:
                DialogUtil.showCommonDialog(this, null, getString(R.string.exit_app), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                }, null);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        MenuItem menuSearch = menu.findItem(R.id.action_search);
        View view = MenuItemCompat.getActionView(menuSearch);
        if(view != null){
            mSearchView = (android.support.v7.widget.SearchView) view;
            // 设置SearchView 的查询回调接口
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setQueryHint(getString(R.string.search_bookmark));
            /* search_src_text 是源码中 searchView 的 id*/
            EditText content = (EditText) mSearchView.findViewById(R.id.search_src_text);
            content.setTextColor(ContextCompat.getColor(this,R.color.white));
            /* 设置光标颜色 */
            try {
                Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
                f.setAccessible(true);
                f.set(content, R.drawable.cursor_color);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        menuSetting = menu.findItem(R.id.action_setting);
        /*set toolbar setting menu default gone */
        menuSetting.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         * noinspection SimplifiableIfStatement
         * */
        switch (item.getItemId()) {
            /*切换布局样式*/
            case R.id.action_more:
                if (mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER) {
                    setRecyclerViewLayoutManager(LayoutManagerType.GRID_LAYOUT_MANAGER);
                } else {
                    setRecyclerViewLayoutManager(LayoutManagerType.LINEAR_LAYOUT_MANAGER);
                }
                return true;
            case R.id.action_setting:
                modifyCollectionsName();
                return true;
            case R.id.action_search:

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
        setEnableLoadMore(false);
        toolbar.setLogo(null);
        REFRESH_TYPE = Constants.REFRESH_TYPE_PULL;
        switch (item.getItemId()) {
            case R.id.nav_all_bookmark:
                // Handle the camera action
//                startActivity(new Intent(this, RecycleViewActivity.class));
                mSwipeRefreshLayout.setRefreshing(true);
                collectionId = Constants.ALL_COLLECTION;
                toolbar.setTitle(null);
                toolbar.setLogo(R.drawable.login_logo_text);
                getBookmarkList();
                // 点击 收件箱 、全部 菜单时隐藏设置按钮
                menuSetting.setVisible(false);
                break;
            case R.id.nav_inbox:
                collectionId = Constants.INBOX;
                toolbar.setTitle(getString(R.string.inbox));
                mSwipeRefreshLayout.setRefreshing(true);
                getBookmarkList();
                // 点击 收件箱 、全部 菜单时隐藏设置按钮
                menuSetting.setVisible(false);
                break;
            default:
                mSwipeRefreshLayout.setRefreshing(false);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        setEnableLoadMore(false);
        REFRESH_TYPE = Constants.REFRESH_TYPE_PULL; // PULL
        getBookmarkList();
    }


    @Override
    public void onLoadMoreRequested() {
        mSwipeRefreshLayout.setRefreshing(false);
        setEnableLoadMore(true);
        REFRESH_TYPE = Constants.REFRESH_TYPE_LOAD_MORE; // LOAD MORE
        getBookmarkList();
    }

    /**
     * 获取书签
     * all bookmarks  传0  或者不传 collection_id
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
                    GlideUtil.getInstance().clearMemory(BookMarkActivity.this);
                    if (REFRESH_TYPE == Constants.REFRESH_TYPE_PULL) {
                        requestData.clear();
                        requestData.addAll(mBookmarkList);
                        if(mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER){
                            mGridLayoutAdapter.setNewData(mBookmarkList);
                        }else{
                            listAdapter.setNewData(mBookmarkList);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else if (REFRESH_TYPE == Constants.REFRESH_TYPE_LOAD_MORE) {// load more
                        requestData.addAll(mBookmarkList);
                        /////// Added by zhanghaihai 通知detail dialog 加载更多完成
                        new LoadMoreEvent(1, mBookmarkList).post();

                        if(mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER){
                            mGridLayoutAdapter.addData(mBookmarkList);
                            mGridLayoutAdapter.loadMoreComplete();// 数据加载完成
                            if (mBookmarkList.size() < Constants.PAGE_SIZE) {// 没有更多数据
                                mGridLayoutAdapter.loadMoreEnd(false);
                            }
                        }else{
                            listAdapter.addData(mBookmarkList);
                            listAdapter.loadMoreComplete();// 数据加载完成
                            if (mBookmarkList.size() < Constants.PAGE_SIZE) {// 没有更多数据
                                listAdapter.loadMoreEnd(false);
                            }
                        }
                    }
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
//                    adapter.loadMoreComplete();
//                    adapter.loadMoreEnd(false);
//                    if (REFRESH_TYPE == Constants.REFRESH_TYPE_PULL) {// when refresh
//                        requestData.clear();
//                        mBookmarkList.clear();
//                        adapter.setNewData(mBookmarkList);
//                        mRequestErrorLayout.setErrorText("暂时还没有书签呦~");
//                        adapter.setEmptyView(mRequestErrorLayout);
//                    }

                    if(mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER){
                        mGridLayoutAdapter.loadMoreComplete();
                        mGridLayoutAdapter.loadMoreEnd(false);
                        if (REFRESH_TYPE == Constants.REFRESH_TYPE_PULL) {// when refresh
                            mGridLayoutAdapter.setNewData(mBookmarkList);
                            mGridLayoutAdapter.setEmptyView(mRequestErrorLayout);
                        }
                    }else{
                        listAdapter.loadMoreComplete();
                        listAdapter.loadMoreEnd(false);
                        if (REFRESH_TYPE == Constants.REFRESH_TYPE_PULL) {// when refresh
                            listAdapter.setNewData(mBookmarkList);
                            listAdapter.setEmptyView(mRequestErrorLayout);
                        }
                    }
                    if (REFRESH_TYPE == Constants.REFRESH_TYPE_PULL) {
                        requestData.clear();
                        mBookmarkList.clear();
                        mRequestErrorLayout.setErrorText("暂时还没有书签呦~");
                    }
                }
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                mProgress.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                loadMoreFail();
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
     * @param collectionName 书签名
     */
    private void createCollection(String collectionName) {
        if(StringUtils.isEmpty(collectionName)){
            ToastUtil.showToastShort("书签名不能为空");
            return;
        }
        Call<Collections> call = KiipuApplication.mRetrofitService.createCollection(userAccessToken, collectionName);
        call.enqueue(new Callback<Collections>() {
            @Override
            public void onResponse(Call<Collections> call, Response<Collections> response) {
                if (response.body() != null) {
                    ToastUtil.showToastShort("创建书签成功啦~");
                    Collections collection = response.body();
                    mCollectionList.add(mCollectionList.size(), collection);
                    addLeftMenu(mCollectionList, false);
                }else{
                    ToastUtil.showToastShort("创建书签失败，请稍后重试~");
                }
            }

            @Override
            public void onFailure(Call<Collections> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, "创建书签失败，请稍后重试~"+ t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    /**
     * delete item
     * @param position 删除索引
     */
    private void requestDeleteItem(final int position) {
        Call<Bookmark> call = KiipuApplication.mRetrofitService.deleteBookmark(userAccessToken, requestData.get(position).id);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                requestData.remove(position);
                removeAdapterData(position);
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
     * @param dataPosition 书签数据索引
     * @param collectionId 书签 id
     */
    private void requestMoveBookmark(final int dataPosition, final int collectionId) {
        String bookmarkId = requestData.get(dataPosition).id;
        Call<Bookmark> call = KiipuApplication.mRetrofitService.moveBookmark(userAccessToken, bookmarkId, mCollectionList.get(collectionId).collectionId);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
                requestData.remove(dataPosition);
                removeAdapterData(dataPosition);
                Snackbar.make(mFloatingActionButton, getString(R.string.move_bookmark_to) + mCollectionList.get(collectionId).collectionName + getString(R.string.success), Snackbar.LENGTH_LONG)
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
     * navigationView.setItemIconTintList(resources.getColorStateList(R.drawable.nav_menu_text_color, null)); 设置本地资源图片
     */
    private void addLeftMenu(List<Collections> mCollectionList, final boolean isRequestMenu) {
        navigationView.getMenu().findItem(R.id.item_collection).setTitle(getString(R.string.collection_box));
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
                            if (isRequestMenu) {
                                navigationView.getMenu().add(0, finalI, finalI, collectionName)
                                        .setIcon(resource)//动态添加menu
                                        .setOnMenuItemClickListener(new OnMenuItemClickListener());
                            } else {
                                navigationView.getMenu().findItem(finalI).setTitle(collectionName)
                                        .setIcon(resource)//动态添加menu
                                        .setOnMenuItemClickListener(new OnMenuItemClickListener());

                            }
                        }
                    });
        }
        /* 添加书签按钮事件操作*/
        MenuItem lastMenu = navigationView.getMenu().findItem(mCollectionList.size());
        if(lastMenu == null){// 因为修改的书签夹时导致的
            navigationView.getMenu().add(0, mCollectionList.size(), mCollectionList.size(), getString(R.string.add_bookmark))
                    .setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_add))
                    .setOnMenuItemClickListener(new OnAddMenuItemClickListener());
        }else if(lastMenu != null && !navigationView.getMenu().findItem(mCollectionList.size()).getTitle().equals(getString(R.string.add_bookmark))) {
            navigationView.getMenu().add(0, mCollectionList.size(), mCollectionList.size(), getString(R.string.add_bookmark))
                    .setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_add))
                    .setOnMenuItemClickListener(new OnAddMenuItemClickListener());
        }
    }

    private void showBookmarkDetailDialog(final int position) {
        List<Bookmark> bookmarks;
        if(listAdapter != null){
            if(mGridLayoutAdapter != null){
                bookmarks = listAdapter.getData().size() > mGridLayoutAdapter.getData().size() ? listAdapter.getData(): mGridLayoutAdapter.getData();
            }else{
                bookmarks = listAdapter.getData();
            }
        }else if(mGridLayoutAdapter != null){
            bookmarks = mGridLayoutAdapter.getData();
        }else{
            bookmarks = new ArrayList<>();
        }
        new BookmarkDetailDialog().show(getSupportFragmentManager(), "bookmark_detail", position,  bookmarks);

    }


    private void showListPopupWindow(View view, final int dataPosition) {
        mListPopupWindow = DialogUtil.showPopupWindow(BookMarkActivity.this, view, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListPopupWindow.dismiss();
                switch (position) {
                    case Constants.POPUP_SHOW_DETAIL:
                        showBookmarkDetailDialog(dataPosition);
                        break;
                    case Constants.POPUP_MOVE_TO:
                        showMoveBookmarkDialog(dataPosition);
                        break;
                    case Constants.POPUP_SHARE:
                    case Constants.POPUP_DELETE:
                        requestDeleteItem(dataPosition);
                        break;
                }
            }
        });
    }

    /**
     * more info
     * @param dataPosition
     * @param which
     */
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
        BottomSheet sheet = getShareActions(requestData.get(position).info.url).title("分享到：").limit(R.integer.no_limit).build();
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
                    removeAdapterData(dataPosition);
                    Snackbar.make(mFloatingActionButton, getString(R.string.move_bookmark_to) + collectionName + getString(R.string.success), Snackbar.LENGTH_LONG)
                            .setDuration(2500)
                            .show();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
            if ((System.currentTimeMillis() - nowTime) > 2000) {
                Toast.makeText(this, getString(R.string.double_back_click_info), Toast.LENGTH_SHORT).show();
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
            ArrayMap<Object, Object> arrayMap = new ArrayMap<>();
            arrayMap.put("title","名称");
            arrayMap.put("hint","输入你要创建的书签名");
            arrayMap.put("content","");// 为了当输入框没有文本时，按钮置灰
            editDialog(Constants.CREATE_COLLECTION, null ,arrayMap);
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
            mSwipeRefreshLayout.setRefreshing(true);
            setEnableLoadMore(false);
            REFRESH_TYPE = Constants.REFRESH_TYPE_PULL;
            getBookmarkList();
            /*set toolbar setting menu visible */
            menuSetting.setVisible(true);
            return false;
        }
    }

    protected void editDialog(final int toDoTag, DialogInterface.OnClickListener NeutralButtonClick, ArrayMap<Object,Object> arrayMap) {
        DialogUtil.showEditDialog(this,
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        inputName = s.toString().trim();
                        if (StringUtils.isEmpty(inputName)) {
                            if (finalButton != null) {
                                finalButton.setEnabled(false);
                            }
                        } else {
                            if (finalButton != null) {
                                finalButton.setEnabled(true);
                            }
                        }
                    }
                }, new DialogUtil.ButtonCallBack() {
                    @Override
                    public void buttonCallBack(Button btn) {
                        finalButton = btn;
                        if (StringUtils.isEmpty(inputName)) {
                            finalButton.setEnabled(false);
                            return;
                        }
                        finalButton.setEnabled(true);
                    }
                }, NeutralButtonClick
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (toDoTag == Constants.CREATE_COLLECTION) {
                            createCollection(inputName);
                        } else if (toDoTag == Constants.MODIFY_COLLECTION_NAME) {
                            modifyCollectionName(inputName);
                        }
                    }
                }, arrayMap);
    }

    /**
     * 修改书签夹名字
     * @param inputName
     */
    private void modifyCollectionName(String inputName) {
        Call<Collections> call = KiipuApplication.mRetrofitService.modifyCollection(userAccessToken, collectionId, inputName);
        call.enqueue(new Callback<Collections>() {
            @Override
            public void onResponse(Call<Collections> call, Response<Collections> response) {
                Collections collections = response.body();
                if (collections != null) {
                    toolbar.setTitle(collections.collectionName);
                    ToastUtil.showToastShort(getString(R.string.modify_collection_success));
                    for (int i = 0; i < mCollectionList.size(); i++) {
                       if(mCollectionList.get(i).collectionId.equals(collections.collectionId)){
                           mCollectionList.get(i).collectionName = collections.collectionName;
                           break;
                       }
                    }
                    // update left sliding menu
                    addLeftMenu(mCollectionList, false);
                    return;
                }
                ToastUtil.showToastShort(getString(R.string.modify_collection_fail));
            }

            @Override
            public void onFailure(Call<Collections> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, getString(R.string.modify_collection_fail)+" "+t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
            }
        });
    }

    /**
     * 修改书签夹名字
     */
    private void modifyCollectionsName() {
        String title = toolbar.getTitle().toString();
        if(!StringUtils.isEmpty(title)){
            ArrayMap<Object, Object> arrayMap = new ArrayMap<>();
            arrayMap.put("title",getString(R.string.modify_collection));
            arrayMap.put("hint",getString(R.string.input_collection_name));
            arrayMap.put("content",title);
            editDialog(Constants.MODIFY_COLLECTION_NAME, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ToastUtil.showToastShort("删除成功啦~");
                }
            }, arrayMap);
        }
    }

    /**
     * 设置布局样式
     * @param layoutManagerType 布局类型
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
             LogUtil.d("scrollPosition-----> "+scrollPosition);
        }
        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, Constants.SPAN_COUNT, GridLayoutManager.VERTICAL, false));
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;

                if(mGridLayoutAdapter == null) {
                    mGridLayoutAdapter = new BookMarkAdapter(mBookmarkList);
                    mGridLayoutAdapter.setOnLoadMoreListener(BookMarkActivity.this, mRecyclerView);
//              mGridLayoutAdapter.openLoadAnimation(new CustomAnimation());//  也可以自定义 Anim
                    mGridLayoutAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
                    mGridLayoutAdapter.setOnItemChildClickListener(new OnItemChildClickListener());
                    mGridLayoutAdapter.setOnItemLongClickListener(new onItemLongClick());
                    mGridLayoutAdapter.setCurrentLayoutManagerType(mCurrentLayoutManagerType);
                }
                setRecyclerParams(6f,6F);
                mRecyclerView.setAdapter(mGridLayoutAdapter);
                break;
            case LINEAR_LAYOUT_MANAGER:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                if(listAdapter == null) {
                    listAdapter = new BookMarkListAdapter(mBookmarkList);
                    listAdapter.setOnLoadMoreListener(BookMarkActivity.this, mRecyclerView);
                    listAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
                    listAdapter.setOnItemChildClickListener(new OnItemChildClickListener());
                    listAdapter.setOnItemLongClickListener(new onItemLongClick());
                    listAdapter.setCurrentLayoutManagerType(mCurrentLayoutManagerType);
                }
                setRecyclerParams(0f,0F);
                mRecyclerView.setAdapter(listAdapter);
                break;
            default:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }
//        mRecyclerView.scrollToPosition(scrollPosition);
    }


    /**
     *
     * LINEAR_LAYOUT_MANAGER 模式下，左右无边距
     * @param paddingLeft
     * @param paddingRight
     */
    private void setRecyclerParams(float paddingLeft, float paddingRight){
        int left = DensityUtils.dp2px(this, paddingLeft);
        int right = DensityUtils.dp2px(this, paddingRight);
        mRecyclerView.setPadding(left, 0, right, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(Constants.KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    private class onBookmarkItemClickListener extends OnItemClickListener {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
            Bookmark bookmark = requestData.get(position);
            viewTheme = TextUtils.isEmpty(bookmark.viewTheme) ? Constants.DEFAULT_COLOR_VALUE : bookmark.viewTheme;
            String url = bookmark.info.url;
//          CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//          builder.setToolbarColor(Color.parseColor("#FFB74D"));
//          CustomTabsIntent customTabsIntent = builder.build();
//          customTabsIntent.launchUrl(BookMarkActivity.this, Uri.parse(url));
            CustomTabsUtils.showCustomTabsView(BookMarkActivity.this, url, viewTheme);
        }
    }

    /**
     * CardView More
     */
    private class OnItemChildClickListener implements BaseQuickAdapter.OnItemChildClickListener {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                case R.id.img_more_info:
                case R.id.ll_more_info:
                    showListPopupWindow(view, position);
                    break;

            }
        }
    }


    private class onItemLongClick implements BaseQuickAdapter.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
            showBookmarkDetailDialog(position);
            return false;
        }
    }

    private void setEnableLoadMore(boolean enable){
        if (mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER){
            listAdapter.setEnableLoadMore(enable);
        }else if(mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER){
            mGridLayoutAdapter.setEnableLoadMore(enable);
        }
    }

    private void loadMoreFail(){
        if (mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER){
            listAdapter.loadMoreFail();
        }else if(mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER){
            mGridLayoutAdapter.loadMoreFail();
        }
    }

    private void removeAdapterData(int position){
        if (mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER){
            listAdapter.remove(position);
        }else if(mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER){
            mGridLayoutAdapter.remove(position);
        }
    }

    private List<Bookmark> getBookmarkDetailData(){
        if (mCurrentLayoutManagerType == LayoutManagerType.LINEAR_LAYOUT_MANAGER){
            listAdapter.getData();
        }else if(mCurrentLayoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER){
            mGridLayoutAdapter.getData();
        }
        return mGridLayoutAdapter.getData();
    }

    /**
     * 当用户在输入法中点击搜索按钮时,或者输入回车时,调用这个方法，发起实际的搜索功能
     * @param query 输入的字段
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(BookMarkActivity.this, "查询" + query, Toast.LENGTH_SHORT).show();
        mSearchView.clearFocus();
        return false;
    }

    /**
     * 每一次输入字符，都会调用这个方法，实现搜索的联想功能
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        Toast.makeText(BookMarkActivity.this, "" + newText, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        GlideUtil.getInstance().clearMemory(BookMarkActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String clipStr = ClipboardUtils.get(this);
        if (!TextUtils.isEmpty(clipStr)) {
//            ToastUtil.showToastShort(this, "粘贴板有数据哦~");
        }
    }

    @RxBusSubscribe(mode = ThreadMode.MAIN)
    public void onEventLoadMore(LoadMoreEvent event){
        switch (event.action){
            case 0:
                onLoadMoreRequested();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.Companion.getDefault().unregister(this);
    }
}
