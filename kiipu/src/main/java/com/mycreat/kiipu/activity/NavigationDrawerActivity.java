package com.mycreat.kiipu.activity;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.*;
import android.transition.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.RecycleAdapter;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.retrofit.RetrofitClient;
import com.mycreat.kiipu.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int SPAN_COUNT = 2;
    /**
     * 0    pull
     * 1    load more
     * */
    private int REFRESH_TYPE = 0;
    private FloatingActionButton mFloatingActionButton;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;
//    private LinearLayoutManager  mLinearLayoutManager;
//    private GridLayoutManager mGirdLayoutManager;
    private String itemId = "";
    private List<Bookmark> mBookmarkList = new ArrayList<>();
    private RecycleAdapter adapter;
    protected ProgressBar mProgress;
    private SwipeRefreshLayout mSwipeRefreshLayout;


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
        mRecyclerView = initViewById(R.id.recyclerView);
        mProgress = initViewById(R.id.pb_view);
        mSwipeRefreshLayout = initViewById(R.id.refresh_view);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        adapter = new RecycleAdapter(NavigationDrawerActivity.this,mBookmarkList);
        mRecyclerView.setAdapter(adapter);

        // 创建线性布局
//        mLinearLayoutManager = new LinearLayoutManager(this);
//        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 创建 GridLayout 布局
//        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,OrientationHelper.VERTICAL);
//        recyclerView_one.setLayoutManager(staggeredGridLayoutManager);
        // GirdLayoutManage other style
//        mGirdLayoutManager=new GridLayoutManager(this,spanCount);
//        mRecyclerView.setLayoutManager(mGirdLayoutManager);

        // StaggeredGridLayoutManager管理RecyclerView的布局   http://blog.csdn.net/zhangphil/article/details/47604581
        mLayoutManager = new StaggeredGridLayoutManager(
                SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);



    }

    public void initListener() {
        setOnClick(mFloatingActionButton);
        // 左侧菜单显示隐藏事件监听，左侧菜单点击选中 selector
        navigationView.setNavigationItemSelectedListener(this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                REFRESH_TYPE = 0; // PULL
                initData();
            }
        });

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE  && recyclerView.){

//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    public void initData() {
//        showProgressDialog(this);

        RetrofitService mRetrofitService = RetrofitClient.getInstance().create(RetrofitService.class);
        Call<List<Bookmark>> call =  mRetrofitService.getBookmarkList(10,itemId);
        call.enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                mBookmarkList = response.body();
                if(REFRESH_TYPE == 0){
                    adapter.addItem(mBookmarkList);
                }else if(REFRESH_TYPE == 1){ // load more
                    adapter.addMoreItem(mBookmarkList);
                }

                mProgress.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
//                Snackbar.make(mFloatingActionButton, "response success", Snackbar.LENGTH_LONG).setDuration(3000).show();
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                mProgress.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                Snackbar.make(mFloatingActionButton, "response fail", Snackbar.LENGTH_LONG)
                        .setDuration(4000)
                        .show();
            }
        });
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()){
            case R.id.floating_action_bt:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Perform anything for the action selected
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setTitle ("Hello Dialog")
//                                      .setIcon(R.drawable.ic_menu_camera)
                                        .setCancelable(false)
                                        .setMessage ("Is this material design?")
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
                startActivity(new Intent(this,MainActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }else{
                startActivity(new Intent(this,MainActivity.class));
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
}
