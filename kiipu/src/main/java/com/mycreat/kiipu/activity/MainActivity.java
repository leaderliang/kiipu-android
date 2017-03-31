package com.mycreat.kiipu.activity;

import android.os.Bundle;
import android.view.View;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.core.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        User user = new User("YanQiao", "Liang");
//        binding.setUser(user);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_toolbar;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onViewClick(View v) {

    }

}
