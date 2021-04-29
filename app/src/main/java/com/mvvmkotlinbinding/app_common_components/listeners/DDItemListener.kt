package com.mvvmkotlinbinding.app_common_components.listeners

interface DDItemListener<T> {
    fun onItemClicked(data: T?, position: Int, ddType: Int)
}