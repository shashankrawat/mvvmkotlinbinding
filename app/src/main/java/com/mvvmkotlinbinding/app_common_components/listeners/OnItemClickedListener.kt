package com.mvvmkotlinbinding.app_common_components.listeners

import android.view.View

interface OnItemClickedListener<T> {
    /**
     * @param v    The `View` that was clicked
     * @param data The data object that is associated with the `View` that was clicked
     */
    fun onItemClicked(v: View, data: T?, position: Int)

    /**
     * @param v    The `View` that was long clicked
     * @param data The data object that is associated with the `View` that was clicked
     * @return `true` if the long click was consumed, `false` if not.
     */
    fun onItemLongClicked(v: View, data: T?, position: Int): Boolean
}