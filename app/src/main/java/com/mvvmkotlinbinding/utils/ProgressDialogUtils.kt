package com.mvvmkotlinbinding.utils

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.mvvmwithdatabinding.R

class ProgressDialogUtils {
    fun getDialog(c: Context?): ProgressDialog {
        val mBar = ProgressDialog(c)
        if (mBar.window != null) {
            mBar.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        mBar.setCanceledOnTouchOutside(false)
        return mBar
    }

    fun showDialog(mBar: ProgressDialog) {
        mBar.show()
        mBar.setContentView(R.layout.custom_progress_bar)
    }

    fun onDismiss(mBar: ProgressDialog?) {
        if (mBar != null && mBar.isShowing) mBar.dismiss()
    }
}