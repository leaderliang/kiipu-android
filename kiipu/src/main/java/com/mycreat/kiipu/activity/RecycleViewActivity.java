package com.mycreat.kiipu.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private List list = new ArrayList();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    public int getLayoutId() {
        return R.layout.layout_recycle_view;
    }


    @Override
    public void onViewClick(View v) {

    }

    @Override
    public void initViews() {
        super.initViews();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_Layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void initData() {
        super.initData();
        for (int i = 0; i < 10; i++) {
            list.add("test");
        }
        RecyclerViewAdapter mRecyclerViewAdapter = new RecyclerViewAdapter(list);
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initViews();
//        swipeToLoadLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeToLoadLayout.setRefreshing(true);
//
//            }
//        });
        initData();
//        initListener();



//        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                // 当不滚动时候
//                if (newState == RecyclerView.SCROLL_STATE_IDLE ){
//                    // 负数表示检测上滑，正数表示下滑；返回 true 表示能在指定的方向滑动，false 反之
//                    if (!ViewCompat.canScrollVertically(recyclerView, 1)){
//                        swipeToLoadLayout.setLoadingMore(true);
//                        swipeToLoadLayout.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                swipeToLoadLayout.setLoadingMore(false);
//                            }
//                        }, 1000);
//                    }
//                }
//            }
//        });




    }



    @Override
    public void onRefresh() {
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);

            }
        }, 2000);
    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        private final List list;

        public RecyclerViewAdapter(List list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_item, parent, false);
           ViewHolder mViewHolder = new ViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTitle.setText("test");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            CardView mCardView;
            TextView mTitle;

            public ViewHolder(View itemView) {
                super(itemView);
                    mCardView = (CardView) itemView.findViewById(R.id.card_view);
                    mTitle = (TextView) itemView.findViewById(R.id.title);
            }

        }
    }

}
