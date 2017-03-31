package com.mycreat.kiipu.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.*;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.adapter.RecycleAdapter;
import com.mycreat.kiipu.core.BaseActivity;
import com.mycreat.kiipu.model.Bookmark;
import com.mycreat.kiipu.retrofit.RetrofitClient;
import com.mycreat.kiipu.retrofit.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;


public class NavigationDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FloatingActionButton fab;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager mGirdLayoutManager;
    private String itemId = "";
    private List<Bookmark> mBookmarkList;
    private RecycleAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_navigation_drawer;
    }

    @Override
    public void initViews() {
        toolbar = initViewById(R.id.toolbar);

        fab = initViewById(R.id.fab);
        // main layout
        drawer = initViewById(R.id.drawer_layout);
        // left menu
        navigationView = initViewById(R.id.nav_view);

        mRecyclerView = initViewById(R.id.recyclerView);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 创建线性布局
//        mLayoutManager = new LinearLayoutManager(this);
//        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(mLayoutManager);

        // 创建 GridLayout 布局
//        StaggeredGridLayoutManager staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,OrientationHelper.VERTICAL);
//        recyclerView_one.setLayoutManager(staggeredGridLayoutManager);

        // 两列
        int spanCount = 2;

//        mGirdLayoutManager=new GridLayoutManager(this,spanCount);
//        mRecyclerView.setLayoutManager(mGirdLayoutManager);


        // StaggeredGridLayoutManager管理RecyclerView的布局   http://blog.csdn.net/zhangphil/article/details/47604581
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(
                spanCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    public void initListener() {
        fab.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void initData() {

        RetrofitService mRetrofitService = RetrofitClient.getInstance().create(RetrofitService.class);
        Call<List<Bookmark>> call =  mRetrofitService.getBookmarkList(10,itemId);
        call.enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                mBookmarkList = response.body();
                adapter = new RecycleAdapter(NavigationDrawerActivity.this,mBookmarkList);
                mRecyclerView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();

                Snackbar.make(fab, "response success", Snackbar.LENGTH_LONG)
                        .setDuration(1000)
                        .show();
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                Snackbar.make(fab, "response fail", Snackbar.LENGTH_LONG)
                        .setDuration(1000)
                        .show();
            }
        });
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()){
            case R.id.fab:
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
            startActivity(new Intent(this,MainActivity.class));
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
