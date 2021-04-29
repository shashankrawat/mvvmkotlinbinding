package com.mvvmkotlinbinding.app_common_components.app_abstracts

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.mvvmkotlinbinding.data.data_beans.ErrorBean

abstract class BaseForm {
    private val errorFields = ErrorBean()
    val errorData = MutableLiveData<ErrorBean>()
    fun setErrorData(msg: String?, errorType: Int) {
        errorFields.errorMsg = msg
        errorFields.errorOf = errorType
        errorData.value = errorFields
    }

    fun getVisibility(textValue: String?): Int {
        return if (!TextUtils.isEmpty(textValue)) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun getVisibility(isEnabled: Boolean): Int {
        return if (isEnabled) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun getVisibilityInvisible(isEnabled: Boolean): Int {
        return if (isEnabled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}