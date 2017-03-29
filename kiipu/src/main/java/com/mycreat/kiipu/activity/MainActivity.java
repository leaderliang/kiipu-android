package com.mycreat.kiipu.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.mycreat.kiipu.R;
import com.mycreat.kiipu.databinding.ActivityMainBinding;
import com.mycreat.kiipu.model.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        User user = new User("YanQiao", "Liang");
        binding.setUser(user);
    }
}
