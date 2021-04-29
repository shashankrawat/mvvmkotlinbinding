package com.mvvmkotlinbinding.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.mvvmkotlinbinding.app_common_components.adapters.CustomDropdownAdapter
import com.mvvmkotlinbinding.app_common_components.listeners.DDItemListener
import com.mvvmwithdatabinding.R

class CustomDDMenu<T>(
    private val context: Context,
    private val ddData: List<T>,
    private val listener: DDItemListener<T>,
    private val ddType: Int
) : PopupWindow() {
    private fun setUpView() {
        val view = LayoutInflater.from(context).inflate(R.layout.dropdown_content_view, null)
        val ddRV: RecyclerView = view.findViewById(R.id.dropdown_rv)
        ddRV.setHasFixedSize(true)
        val adapter = CustomDropdownAdapter(ddData, listener, ddType)
        ddRV.adapter = adapter
        contentView = view
    }

    init {
        setUpView()
    }
}