package com.mvvmkotlinbinding.data.app_prefs

import com.mvvmkotlinbinding.data.data_beans.UserDataBean

interface UserSession {
    fun saveIsUserSignedIn(isUserLoggedIn: Boolean)
    val isUserLoggedIn: Boolean
    fun saveDeviceId(deviceId: String)
    val savedDeviceId: String?
    fun saveUserData(userData: UserDataBean?)
    val savedUserData: UserDataBean?
    fun saveUserToken(token: String?)
    val savedUserToken: String?

    companion object {
        const val PREF_IS_SIGNED_IN = "is_signed_in"
        const val PREF_DEVICE_ID = "pref_device_id"
        const val PREF_USER_TOKEN = "user_token"
        const val PREF_USER_DATA = "user_data"
    }
}