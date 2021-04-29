package com.mvvmkotlinbinding.screens.home_screen;

import android.os.Bundle;
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity;
import com.mvvmwithdatabinding.R;

public class HomeActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
