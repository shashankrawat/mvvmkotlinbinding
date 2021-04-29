package com.mvvmkotlinbinding.screens.login_screen.view_model

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseForm
import com.mvvmkotlinbinding.data.data_beans.LoginBean
import com.mvvmkotlinbinding.utils.AppConstants

class SignInForm : BaseForm() {
    @JvmField
    var loginFields = LoginBean()
    val loginData = MutableLiveData<LoginBean>()
    val fBSignData = MutableLiveData<JsonObject>()
    val instaSignData = MutableLiveData<JsonObject>()

    fun isEmailIdValid(email: String?): Boolean {
        return if (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setErrorData("", 0)
            true
        } else {
            setErrorData("Please enter a valid Email.", AppConstants.ErrorEmail)
            false
        }
    }

    fun isPasswordValid(pwd: String?): Boolean {
        return if (!TextUtils.isEmpty(pwd) && pwd!!.length >= 6) {
            setErrorData("", 0)
            true
        } else {
            setErrorData("Password must be of atleast 6 letter", AppConstants.ErrorPassword)
            false
        }
    }

    fun setFBSignInData(fbData: JsonObject) {
        fBSignData.value = fbData
    }

    fun setInstaSignInData(fbData: JsonObject) {
        instaSignData.value = fbData
    }

    fun onSignInClick() {
        if (isEmailIdValid(loginFields.email) && isPasswordValid(loginFields.password)) {
            loginData.value = loginFields
        }
    }
}