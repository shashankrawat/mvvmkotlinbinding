package com.mvvmkotlinbinding.app_common_components.app_abstracts

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mvvmkotlinbinding.utils.AppCustomDropdownMenu
import java.lang.ref.WeakReference

abstract class BaseFragment : Fragment() {
    private var activity: BaseActivity? = null
    private lateinit var mContext: WeakReference<Context>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = WeakReference(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity = getActivity() as BaseActivity?
    }

    fun hideKeyBoard() {
        if (activity != null) {
            activity!!.hideKeyBoard()
        }
    }

    fun showToast(msg: String?) {
        if (activity != null) {
            activity!!.showToast(msg)
        }
    }

    fun showSnackBar(view: View?, msg: String?) {
        Snackbar.make(view!!, msg!!, Snackbar.LENGTH_SHORT).show()
    }

    fun showProgressDialog() {
        if (activity != null) {
            activity!!.showProgressDialog()
        }
    }

    fun dismissProgressDialog() {
        if (activity != null) {
            activity!!.dismissProgressDialog()
        }
    }

    fun setFragment(containerID: Int, fragment: Fragment?, tag: String?, addToStack: Boolean) {
        if (activity != null) {
            activity!!.setFragment(containerID, fragment, tag, addToStack)
        }
    }

    fun onBackPressed() {
        if (activity != null) {
            activity!!.onBackPressed()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkPermissions(permissions: Array<String?>?): Boolean {
        return if (activity != null) {
            activity!!.checkPermissions(permissions!!)
        } else {
            false
        }
    }

    fun showDropDown(
        anchor: View,
        ddViewField: ObservableField<String?>,
        ddData: List<String>,
        isWidthFull: Boolean
    ) {
        val popupWindow = AppCustomDropdownMenu(mContext!!.get(), ddViewField, ddData)
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        if (isWidthFull) {
            popupWindow.width = anchor.width
        } else {
            popupWindow.width = anchor.width * 2
        }
        popupWindow.isFocusable = true
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(anchor)
    }
}