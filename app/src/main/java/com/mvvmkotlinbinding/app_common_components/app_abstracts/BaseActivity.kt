package com.mvvmkotlinbinding.app_common_components.app_abstracts

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mvvmkotlinbinding.utils.ProgressDialogUtils
import com.mvvmwithdatabinding.R

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mBar: ProgressDialog
    private var progressUtil: ProgressDialogUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun hideKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showSnackBar(view: View?, msg: String?) {
//        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
        val sncbar = Snackbar.make(view!!, msg!!, Snackbar.LENGTH_INDEFINITE)
        sncbar.setAction("OK") { sncbar.dismiss() }
        sncbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                super.onDismissed(transientBottomBar, event)
                Log.e("SNACKBAR", "snack bar dismissed")
            }

            override fun onShown(sb: Snackbar) {
                super.onShown(sb)
                Log.e("SNACKBAR", "snack bar shown")
            }
        })
        sncbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
        sncbar.show()
    }

    fun showToast(msg: String?) {
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        val inflater = layoutInflater
        val layout = inflater.inflate(
            R.layout.custom_toast,
            findViewById<View>(R.id.custom_toast_container) as ViewGroup
        )
        val text = layout.findViewById<View>(R.id.text) as TextView
        text.text = msg
        val toast = Toast(applicationContext)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.show()
    }

    fun showProgressDialog() {
        progressUtil = ProgressDialogUtils()
        mBar = progressUtil!!.getDialog(this)
        progressUtil!!.showDialog(mBar)
    }

    fun dismissProgressDialog() {
        if (progressUtil != null) {
            progressUtil!!.onDismiss(mBar)
        }
        progressUtil = null
    }

    fun setFragment(containerId: Int, fragment: Fragment?, tag: String?, addToStack: Boolean) {
        if (fragment == null) {
            return
        }
        try {
            val fragTransaction = supportFragmentManager.beginTransaction()
            fragTransaction.add(containerId, fragment, tag)
            if (addToStack) fragTransaction.addToBackStack(tag)
            fragTransaction.commit()
        } catch (e: Exception) {
            Log.e(tag, "" + e.toString())
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun checkPermissions(permissions: Array<String?>): Boolean {
        for (permission in permissions) {
            if (checkSelfPermission(permission!!) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}