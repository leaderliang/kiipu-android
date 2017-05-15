package com.mycreat.kiipu.activity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.BookMarkAdapter;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.model.BookmarksInfo;
import com.mycreat.kiipu.utils.Constants;
import com.mycreat.kiipu.utils.SharedPreferencesUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;


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

    private NavigationView navigationView;

    private String itemId = "";

    private List<Bookmark> mBookmarkList = new ArrayList<>();

    private List<Bookmark> requestData = new ArrayList<>();

    private BookMarkAdapter adapter;

    private ProgressBar mProgress;

    private SwipeRefreshLayout swipeToLoadLayout;

    private RecyclerView recyclerView;

    private int viewMarginTop;

    private String header;

    private ImageView mIvClose, mIvIcon, mIvDetail;

    private TextView mTvTitle, mTvUrl;

    private final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation_drawer;
    }

    public void initViews() {
        // main layout
        drawer = initViewById(R.id.drawer_layout);
        // left menu
        navigationView = initViewById(R.id.nav_view);

        toolbar = initViewById(R.id.toolbar);

        mFloatingActionButton = initViewById(R.id.floating_action_bt);

        mProgress = initViewById(R.id.pb_view);

        swipeToLoadLayout = initViewById(R.id.swipe_refresh_layout);

        recyclerView = initViewById(R.id.recyclerView);

        setSupportActionBar(toolbar);

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
//      adapter = new RecycleAdapter(BookMarkActivity.this, mBookmarkList);
        adapter.setOnLoadMoreListener(BookMarkActivity.this, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                viewMarginTop = view.getTop() + getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_default_height_material);
                Toast.makeText(mContext, "position " + position + " viewMarginTop " + viewMarginTop, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void initListener() {

        setOnClick(mFloatingActionButton);
        // 左侧菜单显示隐藏事件监听，左侧菜单点击选中 selector
        navigationView.setNavigationItemSelectedListener(this);
        swipeToLoadLayout.setOnRefreshListener(this);
//        swipeToLoadLayout.setOnLoadMoreListener(this);

//        adapter.setOnRecyclerItemClick(new RecycleViewItemClick());

//        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                    REFRESH_TYPE = 0; // PULL
//                    initData();
//            }
//            });
    }

    public void initData() {
        //showProgressDialog(this);
        getBookmarkList();
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.floating_action_bt:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Perform anything for the action selected
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setTitle("Hello Dialog")
//                                      .setIcon(R.drawable.ic_menu_camera)
                                        .setCancelable(false)
                                        .setMessage("Is this material design?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                builder.show();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setExitTransition(new Explode());
                startActivity(new Intent(this, RecycleViewActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(new Intent(this, RecycleViewActivity.class));
            }
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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


    private void getBookmarkList() {
        String lastItemId = mBookmarkList.size() > 0 ? mBookmarkList.get(mBookmarkList.size() - 1).getId() : "";
        itemId = REFRESH_TYPE == 0 ? "" : lastItemId;
        header = "Bearer " + SharedPreferencesUtil.getData(mContext, "accessToken", "");
        Call<List<Bookmark>> call = mKiipuApplication.mRetrofitService.getBookmarkList(header, PAGE_SIZE, itemId);
        call.enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
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
                mProgress.setVisibility(View.GONE);
//              Snackbar.make(mFloatingActionButton, "response success", Snackbar.LENGTH_LONG).setDuration(3000).show();
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                mProgress.setVisibility(View.GONE);
                swipeToLoadLayout.setRefreshing(false);
                adapter.loadMoreFail();
                Snackbar.make(mFloatingActionButton, "response fail", Snackbar.LENGTH_LONG)
                        .setDuration(4000)
                        .show();
            }
        });
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

    public void onShowDetailClick(int position) {
        showBottomSheetDialog(position);
    }

    private void showBottomSheetDialog(int position) {
        BookmarksInfo mBookmarksInfo = requestData.get(position).getInfo();
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
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
                .placeholder(R.mipmap.ic_launcher) // 占位图
                .error(R.drawable.error) // 加载失败占位图
//                .diskCacheStrategy(DiskCacheStrategy.NONE)// 禁用掉Glide的缓存功能,默认是打开的
                .centerCrop() // 取图片的中间区域
//                .fitCenter()
                .into(mIvIcon);
        Glide.with(mContext)
                .load(mBookmarksInfo.getImg())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.error)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mIvDetail);
        mTvTitle.setText(mBookmarksInfo.getTitle());
        mTvUrl.setText(mBookmarksInfo.getUrl());
        dialog.setContentView(view);
        dialog.show();
    }


    public void showListPopupWindow(View view, final int position) {
        final ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);

        // ListView适配器
        listPopupWindow.setAdapter(
                new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, Constants.ITEMS));
        // 选择item的监听事件
        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int pos, long id) {
                listPopupWindow.dismiss();
                showBottomSheetDialog(position);
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

}
