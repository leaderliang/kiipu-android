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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.RecycleAdapter;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.model.Bookmark;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnRefreshListener, OnLoadMoreListener {

    private final int SPAN_COUNT = 2;
    /**
     * 0    pull
     * 1    load more
     */
    private int REFRESH_TYPE = 0;
    private FloatingActionButton mFloatingActionButton;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private RecyclerView.LayoutManager mLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGirdLayoutManager;
    private String itemId = "";
    private List<Bookmark> mBookmarkList = new ArrayList<>();

    private RecycleAdapter adapter;

    protected ProgressBar mProgress;

    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;

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

        swipeToLoadLayout = initViewById(R.id.swipeToLoadLayout);

        recyclerView = initViewById(R.id.swipe_target);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        // 创建线性布局
        //mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //mRecyclerView.setLayoutManager(mLinearLayoutManager);


        /**
         * spanCount，每列或者每行的item个数，设置为1，就是列表样式  该构造函数默认是竖直方向的网格样式,每列或者每行的item个数，设置为1，就是列表样式
         * 网格样式的方向，水平（OrientationHelper.HORIZONTAL）或者竖直（OrientationHelper.VERTICAL）
         * reverseLayout，是否逆向，true：布局逆向展示，false：布局正向显示
         * GirdLayoutManage other style
         * */
        mGirdLayoutManager = new GridLayoutManager(this, SPAN_COUNT, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mGirdLayoutManager);

        // 创建 瀑布流
        //StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(SPAN_COUNT,OrientationHelper.VERTICAL);
        //mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        // StaggeredGridLayoutManager 管理 RecyclerView的布局  瀑布流   http://blog.csdn.net/zhangphil/article/details/47604581
        //StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        //mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new RecycleAdapter(NavigationDrawerActivity.this, mBookmarkList);
        recyclerView.setAdapter(adapter);

    }

    public void initListener() {

        setOnClick(mFloatingActionButton);
        // 左侧菜单显示隐藏事件监听，左侧菜单点击选中 selector
        navigationView.setNavigationItemSelectedListener(this);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

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
        REFRESH_TYPE = 0; // PULL
        getBookmarkList();
    }

    @Override
    public void onLoadMore() {
        REFRESH_TYPE = 1; // LOAD MORE
        getBookmarkList();
    }


    private void getBookmarkList() {
        itemId = REFRESH_TYPE == 0 ? "" : adapter.getLastItemId();

        Call<List<Bookmark>> call = mKiipuApplication.mRetrofitService.getBookmarkList(10, itemId);
        call.enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                mBookmarkList = response.body();
                if (REFRESH_TYPE == 0) {
                    adapter.addItem(mBookmarkList);
                } else if (REFRESH_TYPE == 1) {
                    adapter.addMoreItem(mBookmarkList);
                }
                mProgress.setVisibility(View.GONE);
                swipeToLoadLayout.setRefreshing(false);
                swipeToLoadLayout.setLoadingMore(false);
//                Snackbar.make(mFloatingActionButton, "response success", Snackbar.LENGTH_LONG).setDuration(3000).show();
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                mProgress.setVisibility(View.GONE);
                swipeToLoadLayout.setRefreshing(false);
                Snackbar.make(mFloatingActionButton, "response fail", Snackbar.LENGTH_LONG)
                        .setDuration(4000)
                        .show();
            }
        });
    }
}
