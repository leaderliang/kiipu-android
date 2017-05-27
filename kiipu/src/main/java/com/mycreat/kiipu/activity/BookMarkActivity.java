package com.mycreat.kiipu.activity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.*;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.BookMarkAdapter;
import com.mycreat.kiipu.adapter.CollectionListAdapter;
import com.mycreat.kiipu.core.AppManager;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.model.Collections;
import com.mycreat.kiipu.model.UserInfo;
import com.mycreat.kiipu.utils.*;
import com.mycreat.kiipu.view.CustomAnimation;
import com.mycreat.kiipu.view.MyBottomSheetDialog;
import com.mycreat.kiipu.view.RequestErrorLayout;
import com.mycreat.kiipu.view.RoundImageView;
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
        implements NavigationView.OnNavigationItemSelectedListener, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private final int SPAN_COUNT = 2;
    /**
     * 0 pull; 1 load more
     */
    private int REFRESH_TYPE = 0;

    private FloatingActionButton mFloatingActionButton;

    private Toolbar toolbar;

    private DrawerLayout drawer;
    //  left menu
    private NavigationView navigationView;

    private String itemId = "";

    private BookMarkAdapter adapter;

    private ProgressBar mProgress;

    private SwipeRefreshLayout swipeToLoadLayout;

    private RecyclerView recyclerView;

    private ImageView mIvClose, mIvIcon, mIvDetail;

    private RoundImageView mIvUserHeader;

    private TextView mTvTitle, mTvUrl, mTvUserName;

    private final int PAGE_SIZE = 10;

    private List<Bookmark> mBookmarkList = new ArrayList<>();

    private List<Bookmark> requestData = new ArrayList<>();

    private List<Collections> mCollectionList = new ArrayList<>();

    private UserInfo mUserInfo;

    private View headerView;

    private long nowTime;

    public Button finalButton;

    private String inputName, collectionId = Constants.ALL_COLLECTION, viewTheme;

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

        mIvUserHeader = (RoundImageView) headerView.findViewById(R.id.iv_user_icon);

        mTvUserName = (TextView) headerView.findViewById(R.id.tv_user_name);

        toolbar = initViewById(R.id.toolbar);

        mFloatingActionButton = initViewById(R.id.floating_action_bt);

        mProgress = initViewById(R.id.pb_view);

        swipeToLoadLayout = initViewById(R.id.swipe_refresh_layout);

        recyclerView = initViewById(R.id.recyclerView);

        swipeToLoadLayout.setColorSchemeColors(Color.parseColor("#FFB74D"));

        swipeToLoadLayout.setRefreshing(true);

        setSupportActionBar(toolbar);

//        navigationView.setItemIconTintList(null);// set menu item default color

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);

        toggle.syncState();
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        // 创建线性布局
        //LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerView.setLayoutManager(mLinearLayoutManager);

        /**
         * spanCount，每列或者每行的item个数，设置为1，就是列表样式  该构造函数默认是竖直方向的网格样式,每列或者每行的item个数，设置为1，就是列表样式
         * 网格样式的方向，水平（OrientationHelper.HORIZONTAL）或者竖直（OrientationHelper.VERTICAL）
         * reverseLayout，是否逆向，true：布局逆向展示，false：布局正向显示
         * GirdLayoutManage other style
         * */
        GridLayoutManager mGirdLayoutManager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mGirdLayoutManager);
        // 创建 瀑布流
        //StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(SPAN_COUNT,OrientationHelper.VERTICAL);
        //mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        // StaggeredGridLayoutManager 管理 RecyclerView的布局  瀑布流   http://blog.csdn.net/zhangphil/article/details/47604581
        //RecyclerView.LayoutManager mLayoutManager;
        //StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new BookMarkAdapter(this);
        adapter.setOnLoadMoreListener(BookMarkActivity.this, recyclerView);
        adapter.openLoadAnimation(new CustomAnimation());
        adapter.setOnItemChildClickListener(new OnItemChildClickListener());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bookmark bookmark = requestData.get(position);
                viewTheme = TextUtils.isEmpty(bookmark.viewTheme) ? "FFB74D" : bookmark.viewTheme;
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
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* menu item click */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
//        mProgress.setVisibility(View.VISIBLE);
        swipeToLoadLayout.setRefreshing(true);
        adapter.setEnableLoadMore(false);
        REFRESH_TYPE = 0;
        if (id == R.id.nav_all_bookmark) { /* all bookmarks  传0  或者不传 collection_id */
            // Handle the camera action
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                getWindow().setExitTransition(new Explode());
//                startActivity(new Intent(this, RecycleViewActivity.class),
//                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//            } else {
//                startActivity(new Intent(this, RecycleViewActivity.class));
//            }
            collectionId = Constants.ALL_COLLECTION;
            toolbar.setTitle(getString(R.string.all));
            getBookmarkList();
        } else if (id == R.id.nav_inbox) {/* all bookmarks  传0  或者不传 collection_id */
            collectionId = Constants.INBOX;
            toolbar.setTitle(getString(R.string.inbox));
            getBookmarkList();
        }else{
            swipeToLoadLayout.setRefreshing(false);
        }
//        else if (id == R.id.nav_slideshow) {
//        } else if (id == R.id.nav_manage) {
//        } else if (id == R.id.nav_share) {
//        } else if (id == R.id.nav_send) {
//        }
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
        Call<List<Bookmark>> call = mKiipuApplication.mRetrofitService.getBookmarkList(userAccessToken, PAGE_SIZE, itemId, collectionId);
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
                        if (mBookmarkList.size() < PAGE_SIZE) {// 没有更多数据
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
        Call<List<Collections>> call = mKiipuApplication.mRetrofitService.getCollectionList(userAccessToken);
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
        Call<UserInfo> call = mKiipuApplication.mRetrofitService.getUserInfo(userAccessToken);
        Log.e("getUserInfo", "userAccessToken " + userAccessToken);
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                mUserInfo = response.body();
//                mIvUserHeader  mTvUserName
                if (mUserInfo != null) {
                    Glide.with(mContext)
                            .load(mUserInfo.avatarUrl)
                            .placeholder(R.drawable.ic_launcher) // 占位图
                            .error(R.drawable.error) // 加载失败占位图
                            .into(mIvUserHeader);
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
        Call<Collections> call = mKiipuApplication.mRetrofitService.creatCollection(userAccessToken, collectionName);
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
        Call<Bookmark> call = mKiipuApplication.mRetrofitService.deleteBookmark(userAccessToken, requestData.get(position).id);
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
    private void requestMoveBookmark(final String bookmarkId, final int collectionId) {
        Call<Bookmark> call = mKiipuApplication.mRetrofitService.moveBookmark(userAccessToken, bookmarkId, mCollectionList.get(collectionId).collectionId);
        call.enqueue(new Callback<Bookmark>() {
            @Override
            public void onResponse(Call<Bookmark> call, Response<Bookmark> response) {
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
        // the first menu view
        navigationView.getMenu().findItem(R.id.nav_share)
                .setTitle(mCollectionList.get(0).collectionName)
                .setOnMenuItemClickListener(new OnMenuItemClickListener());
        if (isRequestMenu) {
            for (int i = 1; i < mCollectionList.size(); i++) {
                navigationView.getMenu().add(0, i, i, mCollectionList.get(i).collectionName + "")
                        .setIcon(getDrawable(R.drawable.ic_menu_share))//动态添加menu
                        .setOnMenuItemClickListener(new OnMenuItemClickListener());
            }
        } else {// 调用添加按钮后，重新设置之前menu的 name
            for (int i = 1; i < mCollectionList.size(); i++) {
                navigationView.getMenu().findItem(i).setTitle(mCollectionList.get(i).collectionName)
                        .setIcon(getDrawable(R.drawable.ic_menu_share))//动态添加menu
                        .setOnMenuItemClickListener(new OnMenuItemClickListener());
            }
        }
        // 添加书签按钮事件操作
        navigationView.getMenu().add(0, mCollectionList.size(), mCollectionList.size(), "添加书签")
                .setIcon(getDrawable(R.drawable.ic_add))
                .setOnMenuItemClickListener(new OnAddMenuItemClickListener());
    }

    private class OnItemChildClickListener implements BaseQuickAdapter.OnItemChildClickListener {

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            switch (view.getId()) {
                case R.id.img_more_info:
                    showListPopupWindow(view, position);
                    break;
            }
        }
    }


    private void showBookmarkDetailDialog(int position) {
        BookmarksInfo mBookmarksInfo = requestData.get(position).info;
        final MyBottomSheetDialog dialog = new MyBottomSheetDialog(BookMarkActivity.this);
        View view = LayoutInflater.from(this).inflate(R.layout.view_bottom_sheet, null);
        mIvClose = (ImageView) view.findViewById(R.id.iv_close);
        mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
        mTvTitle = (TextView) view.findViewById(R.id.tv_title);
        mTvUrl = (TextView) view.findViewById(R.id.tv_url);
        mIvDetail = (ImageView) view.findViewById(R.id.iv_detail);

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Glide.with(mContext)
                .load(mBookmarksInfo.getIcon())
                .placeholder(R.drawable.ic_launcher) // 占位图
                .error(R.drawable.error) // 加载失败占位图
//                .diskCacheStrategy(DiskCacheStrategy.NONE)// 禁用掉Glide的缓存功能,默认是打开的
                .centerCrop() // 取图片的中间区域
//                .fitCenter()
                .into(mIvIcon);
        Glide.with(mContext)
                .load(mBookmarksInfo.getImg())
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.error)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mIvDetail);
        mTvTitle.setText(mBookmarksInfo.getTitle());
        mTvUrl.setText(mBookmarksInfo.getUrl());
        dialog.setContentView(view);
        dialog.show();
    }


    public void showListPopupWindow(View view, final int dataPosition) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);

        // ListView适配器
        listPopupWindow.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, Constants.ITEMS));
        // 选择item的监听事件
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();
                switch (position) {
                    case 0:
                        showBookmarkDetailDialog(dataPosition);
                        break;
                    case 1:
                        showRemoveBookmarkDialog(dataPosition);
                        break;
                    case 2:
                        requestDeleteItem(dataPosition);
                        break;
                    case 3:
                        ToastUtil.showToastShort("分享功能正在后期筹备中...");
                        break;
                }
            }
        });

        // 对话框的宽高
        listPopupWindow.setWidth(500);
        listPopupWindow.setHeight(600);

        // ListPopupWindow 相对的View
        listPopupWindow.setAnchorView(view);

        // ListPopupWindow 相对按钮横向 和纵向 的距离
        listPopupWindow.setHorizontalOffset(50);
        listPopupWindow.setVerticalOffset(1);

        //  Set whether this window should be modal when shown.
        // If a popup window is modal, it will receive all touch and key input. If the user touches outside the popup window's content area the popup window will be dismissed.
        // modal boolean: true if the popup window should be modal, false otherwise.
        listPopupWindow.setModal(false);

        listPopupWindow.show();
    }

    private void showRemoveBookmarkDialog(final int dataPosition) {
        final MyBottomSheetDialog dialog = new MyBottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CollectionListAdapter adapter = new CollectionListAdapter(this);
        adapter.addData(mCollectionList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                dialog.dismiss();
                requestMoveBookmark(requestData.get(dataPosition).id, position);
            }
        });
        dialog.setContentView(view);
        dialog.show();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - nowTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序并清空登录信息", Toast.LENGTH_SHORT).show();
                nowTime = System.currentTimeMillis();
            } else {
                SharedPreferencesUtil.removeKey(mContext, Constants.ACCESS_TOKEN);
                SharedPreferencesUtil.removeKey(mContext, Constants.USER_ID);
                finish();
                AppManager.getAppManager().appExit(this);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
     * 2131493098 为 item（nav_share）在 R 文件中 id
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
        if(!TextUtils.isEmpty(clipStr)){
//            ToastUtil.showToastShort(this, "粘贴板有数据哦~");

        }
    }
}
