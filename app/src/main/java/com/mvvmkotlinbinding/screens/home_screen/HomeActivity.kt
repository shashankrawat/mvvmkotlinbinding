package com.mvvmkotlinbinding.screens.home_screen

import android.os.Bundle
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseActivity
import com.mvvmwithdatabinding.R

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}