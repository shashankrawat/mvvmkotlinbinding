package com.mvvmkotlinbinding.screens.login_screen.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mvvmkotlinbinding.app_common_components.app_abstracts.BaseViewModel
import com.mvvmkotlinbinding.data.data_beans.ErrorBean
import com.mvvmkotlinbinding.data.data_beans.LoginBean
import com.mvvmkotlinbinding.data.network.Resource
import com.mvvmkotlinbinding.screens.login_screen.model.SignInRepo.Companion.get

class SignInViewModel(application: Application) : BaseViewModel(application) {
    val form: SignInForm
    private val fbSignInLD: LiveData<Resource<String>>
    private val instaSignInLD: LiveData<Resource<String>>
    private val loginLD: LiveData<Resource<LoginBean>>
    fun fbSignIn(): LiveData<Resource<String>> {
        return fbSignInLD
    }

    fun instaSignIn(): LiveData<Resource<String>> {
        return instaSignInLD
    }

    fun observeLogin(): LiveData<Resource<LoginBean>> {
        return loginLD
    }

    fun observeErrorData(): LiveData<ErrorBean> {
        return form.errorData
    }

    init {
        form = SignInForm()
        fbSignInLD = Transformations.switchMap(
            form.fBSignData
        ) { input -> get().FBSignIn(input) }
        instaSignInLD = Transformations.switchMap(
            form.instaSignData
        ) { input -> get().InstaSignIn(input) }
        loginLD = Transformations.switchMap(
            form.loginData
        ) { input -> get().signInUser(input) }
    }
}