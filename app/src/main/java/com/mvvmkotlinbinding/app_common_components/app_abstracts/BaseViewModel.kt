package com.mvvmkotlinbinding.app_common_components.app_abstracts

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mvvmkotlinbinding.data.app_prefs.UserSession
import com.mvvmkotlinbinding.data.app_prefs.UserSessionImpl
import com.mvvmkotlinbinding.utils.PreferencesUtil

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private var mUserSession: UserSession? = null
    private var mGson: Gson? = null
    private var mPreferencesUtil: PreferencesUtil? = null

    // get GSON Object
    val gsonBuilder: Gson?
        get() {
            if (mGson == null) mGson = GsonBuilder()
                .enableComplexMapKeySerialization()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .setLenient()
                .create()
            return mGson
        }

    // get shared preference
    private fun getPreferencesUtil(application: Application) {
        if (mPreferencesUtil == null) mPreferencesUtil = PreferencesUtil(
            application.getSharedPreferences(
                PreferencesUtil.PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
        )
    }

    // get User sessions
    val userSession: UserSession?
        get() {
            if (mUserSession == null) {
                mUserSession = UserSessionImpl.getInstance(mPreferencesUtil, gsonBuilder)
            }
            return mUserSession
        }

    init {
        getPreferencesUtil(application)
    }
}