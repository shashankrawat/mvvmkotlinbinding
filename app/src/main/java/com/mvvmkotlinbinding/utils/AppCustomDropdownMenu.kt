package com.mvvmkotlinbinding.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.mvvmkotlinbinding.app_common_components.adapters.DropdownAdapter
import com.mvvmkotlinbinding.app_common_components.listeners.OnItemClickedListener
import com.mvvmwithdatabinding.R

class AppCustomDropdownMenu(
    private val context: Context?,
    private val ddViewField: ObservableField<String?>,
    private val ddData: List<String>
) : PopupWindow() {
    private fun setUpView() {
        val view = LayoutInflater.from(context).inflate(R.layout.dropdown_content_view, null)
        val ddRV: RecyclerView = view.findViewById(R.id.dropdown_rv)
        ddRV.setHasFixedSize(true)
        val adapter = DropdownAdapter(ddData, object : OnItemClickedListener<String?> {
            override fun onItemClicked(v: View, data: String?, position: Int) {
                dismiss()
                ddViewField.set(data)
            }

            override fun onItemLongClicked(v: View, data: String?, position: Int): Boolean {
                return false
            }
        })
        ddRV.adapter = adapter
        contentView = view
    }

    init {
        setUpView()
    }
}