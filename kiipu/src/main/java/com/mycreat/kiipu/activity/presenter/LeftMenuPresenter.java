package com.mycreat.kiipu.activity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.activity.LoginActivity;
import com.mycreat.kiipu.core.KiipuApplication;
import com.mycreat.kiipu.model.Collections;
import com.mycreat.kiipu.model.UserInfo;
import com.mycreat.kiipu.utils.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于展示首页左侧侧滑菜单
 *
 * @author zhanghaihai
 * @date 2017/11/4
 */
public class LeftMenuPresenter implements DialogInterface.OnClickListener, View.OnClickListener {
    /**
     * 用于为每个收藏夹自动生成一个临时的唯一ID
     */
    private AtomicInteger transientId = new AtomicInteger(1);

    private View mFloatingActionButton;
    private ActionBar toolbar;
    private Button finalButton;
    private ImageView mIvUserHeader;
    private TextView mTvUserName;
    private NavigationView navigationView;
    private View headerView;
    private Button mBtLogOut;
    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener;
    private Context context;

    private UserInfo mUserInfo;
    private String inputContent;
    private Collections collection;

    /**
     * 缓存临时ID和收藏夹ID的关系
     */
    private Map<Collections, Integer> collectionTIdMap = new ConcurrentHashMap<>();
    private String collectionId = Constants.ALL_COLLECTION;
    private String userAccessToken;
    private List<Collections> mCollectionList;


    private MenuItem.OnMenuItemClickListener onMenuItemClickListener;


    public LeftMenuPresenter(NavigationView navigationView, NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener, MenuItem.OnMenuItemClickListener onMenuItemClickListener, String userAccessToken, List<Collections> mCollectionList, View mFloatingActionButton, ActionBar toolbar, Context context, Button finalButton) {
        this.navigationView = navigationView;
        this.navigationItemSelectedListener = navigationItemSelectedListener;
        this.onMenuItemClickListener = onMenuItemClickListener;
        this.userAccessToken = userAccessToken;
        this.mCollectionList = mCollectionList;
        this.mFloatingActionButton = mFloatingActionButton;
        this.toolbar = toolbar;
        this.context = context;
        this.finalButton = finalButton;
        headerView = getHeaderView(0);
        mIvUserHeader = (ImageView) headerView.findViewById(R.id.iv_user_icon);

        mTvUserName = (TextView) headerView.findViewById(R.id.tv_user_name);
        mBtLogOut = (Button) headerView.findViewById(R.id.bt_log_out);
    }

    public View getHeaderView(int id) {
        return navigationView.getHeaderView(id);
    }

    public MenuItem findItem( int id) {
        return navigationView.getMenu().findItem(id);
    }

    public void setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener listener) {
        navigationView.setNavigationItemSelectedListener(listener);
    }

    /**
     * 修改书签夹名字
     */
    public void modifyCollectionsName() {
        String title = toolbar.getTitle().toString();
        if (!StringUtils.isEmpty(title)) {
            ArrayMap<Object, Object> arrayMap = new ArrayMap<>();
            arrayMap.put("title", StringUtils.getString(R.string.modify_collection));
            arrayMap.put("hint", StringUtils.getString(R.string.input_collection_name));
            arrayMap.put("content", title);
            editDialog(Constants.MODIFY_COLLECTION_NAME, Constants.INPUT_MAX_LENGTH, this, arrayMap);
        }
    }

    /**
     * 删除书签夹
     *
     * @param collectionsId 书签夹 id
     */
    private void deleteCollections(String collectionsId) {
        Call<Collections> call = KiipuApplication.mRetrofitService.deleteCollections(userAccessToken, collectionsId);
        call.enqueue(new Callback<Collections>() {
            @Override
            public void onResponse(Call<Collections> call, Response<Collections> response) {
                Collections collections = collection;
                if(collections == null ) return;
                /*移除侧滑菜单书签夹*/
                navigationView.getMenu().removeItem(collectionTIdMap.get(collections));
                collectionTIdMap.remove(collections);
                mCollectionList.remove(collections);
                /*设置选中 收件箱 */
                navigationView.getMenu().getItem(1).setChecked(true);

                navigationItemSelectedListener.onNavigationItemSelected(navigationView.getMenu().getItem(1));
            }

            @Override
            public void onFailure(Call<Collections> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 动态添加左侧菜单
     *
     * @param mCollectionList
     * @param isRequestMenu   true 请求书签；false 添加或修改书签
     *                        navigationView.setItemIconTintList(resources.getColorStateList(R.drawable.nav_menu_text_color, null)); 设置本地资源图片
     */
    public void updateLeftMenu(List<Collections> mCollectionList, final boolean isRequestMenu) {
        MenuItem itemMenuItem = navigationView.getMenu().findItem(R.id.item_collection);
        itemMenuItem.setTitle(StringUtils.getString(R.string.collection_box));
        int count = mCollectionList.size();
        int groupId = itemMenuItem.getGroupId();

        //重新设置Menu
        for(int id: collectionTIdMap.values()){
            navigationView.getMenu().removeItem(id);
        }

        //添加书签夹按钮只添加一次
        if(navigationView.getMenu().findItem(Short.MAX_VALUE) == null)
            navigationView.getMenu()
                    .add(groupId, Short.MAX_VALUE, Short.MAX_VALUE, StringUtils.getString(R.string.add_collections))
                    .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_add))
                    .setOnMenuItemClickListener(onMenuItemClickListener);

        //填充书签夹
        for (int i = 0; i < count; i ++ ){
            Collections collections = mCollectionList.get(i);
            int orderId = transientId.incrementAndGet();
            navigationView.getMenu().add(groupId, orderId, orderId, collections.collectionName)
                    .setOnMenuItemClickListener(onMenuItemClickListener);
            collectionTIdMap.put(collections, orderId);
            loadCollectionsIcon(collections, orderId, orderId);
        }

    }

    /**
     * 加载书签夹图片
     * @param collections
     * @param itemId
     * @param orderId
     */
    private void loadCollectionsIcon(Collections collections, final int itemId, int orderId){
        Glide.with(context)
                    .load(collections.menuIcon)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            navigationView.getMenu().findItem(itemId).setIcon(resource);
                        }
                    });
    }

    public Collections getCollection(Integer transientId){
        for(Collections collections: collectionTIdMap.keySet()){
            if(collectionTIdMap.get(collections).equals(transientId)){
                return collections;
            }
        }
        return null;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public void setCollection(Collections collection) {
        this.collection = collection;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        DialogUtil.showCommonDialog(context,
                StringUtils.getString(R.string.confirm_delete_current_collections),
                StringUtils.getString(R.string.delete_collections_content),
                StringUtils.getString(R.string.confirm), StringUtils.getString(R.string.cancle),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCollections(collectionId);
                    }
                });
    }

    protected void editDialog(final int toDoTag, int inputLength, DialogInterface.OnClickListener NeutralButtonClick, ArrayMap<Object, Object> arrayMap) {
        DialogUtil.showEditDialog(context, inputLength,
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        inputContent = s.toString().trim();
                        if (StringUtils.isEmpty(inputContent)) {
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
                        if (StringUtils.isEmpty(inputContent)) {
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
                            createCollection(inputContent);
                        }  else if (toDoTag == Constants.MODIFY_COLLECTION_NAME) {
                            modifyCollectionName(inputContent);
                        }
                    }
                }, arrayMap);
    }

    public void showAddCollectionsDialog(){
        ArrayMap<Object, Object> arrayMap = new ArrayMap<>();
        arrayMap.put("title", "名称");
        arrayMap.put("hint", "输入你要创建的书签夹名称");
        arrayMap.put("content", "");// 为了当输入框没有文本时，按钮置灰
        editDialog(Constants.CREATE_COLLECTION, Constants.INPUT_MAX_LENGTH, null, arrayMap);
    }

    /**
     * 修改书签夹名字
     *
     * @param inputName
     */
    private void modifyCollectionName(String inputName) {
        Call<Collections> call = KiipuApplication.mRetrofitService.modifyCollection(userAccessToken, collectionId, inputName);
        call.enqueue(new Callback<Collections>() {
            @Override
            public void onResponse(Call<Collections> call, Response<Collections> response) {
                Collections collections = response.body();
                if (collections != null) {
                    Snackbar.make(mFloatingActionButton, StringUtils.getString(R.string.modify_collection_success), Snackbar.LENGTH_SHORT).show();
                    toolbar.setTitle(collections.collectionName);
                    for (int i = 0; i < mCollectionList.size(); i++) {
                        if (mCollectionList.get(i).collectionId.equals(collections.collectionId)) {
                            mCollectionList.get(i).collectionName = collections.collectionName;
                            break;
                        }
                    }
                    // update left sliding menu
                    updateLeftMenu(mCollectionList, false);
                    return;
                }
                Snackbar.make(mFloatingActionButton, StringUtils.getString(R.string.modify_collection_fail), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Collections> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, StringUtils.getString(R.string.modify_collection_fail) + " " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 获取收藏夹列表
     */
    public void getCollectionList() {
        Call<List<Collections>> call = KiipuApplication.mRetrofitService.getCollectionList(userAccessToken);
        call.enqueue(new Callback<List<Collections>>() {
            @Override
            public void onResponse(Call<List<Collections>> call, Response<List<Collections>> response) {
                mCollectionList.clear();
                mCollectionList.addAll(response.body());
                if (!CollectionUtils.isEmpty(mCollectionList)) {
                    updateLeftMenu(mCollectionList, true);
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
     * 创建书签
     *
     * @param collectionName 书签名
     */
    private void createCollection(String collectionName) {
        if (StringUtils.isEmpty(collectionName)) {
            Snackbar.make(mFloatingActionButton, "书签名不能为空~", Snackbar.LENGTH_LONG)
                    .setDuration(2500)
                    .show();
            return;
        }
        Call<Collections> call = KiipuApplication.mRetrofitService.createCollection(userAccessToken, collectionName);
        call.enqueue(new Callback<Collections>() {
            @Override
            public void onResponse(Call<Collections> call, Response<Collections> response) {
                if (response.body() != null) {
                    Collections collection = response.body();
                    mCollectionList.add(mCollectionList.size(), collection);
                    updateLeftMenu(mCollectionList, false);
                    Snackbar.make(mFloatingActionButton, "创建书签成功啦~", Snackbar.LENGTH_LONG)
                            .show();
                    /*添加成功，打开侧边栏  可延迟打开*/
//                    drawer.openDrawer(Gravity.LEFT);
                } else {
                    Snackbar.make(mFloatingActionButton, "创建书签失败，请稍后重试~", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Collections> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, "创建书签失败，请稍后重试~" + t.getMessage(), Snackbar.LENGTH_LONG)
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
                mBtLogOut.setText(StringUtils.getString(R.string.logout));
                mBtLogOut.setEnabled(true);
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Snackbar.make(mFloatingActionButton, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setDuration(2500)
                        .show();
                mBtLogOut.setEnabled(true);
                mBtLogOut.setText(StringUtils.getString(R.string.retry));
            }
        });

    }
    public void initLeftData() {
        getCollectionList();
        getUserInfo();
        mBtLogOut.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_log_out:
                if(((Button)view).getText().equals(StringUtils.getString(R.string.retry))){
                    initLeftData();
                    mBtLogOut.setText(StringUtils.getString(R.string.refreshing));
                    mBtLogOut.setEnabled(false);
                }else {
                    DialogUtil.showCommonDialog(context, null, StringUtils.getString(R.string.exit_app), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            intentToLogin();
                        }
                    }, null);
                }
                break;
        }
    }


    private void intentToLogin() {
        SharedPreferencesUtil.removeKey(context, Constants.ACCESS_TOKEN);
        SharedPreferencesUtil.removeKey(context, Constants.USER_ID);
        if(context != null && context instanceof Activity) {
            ((Activity) context).finish();
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }
}
