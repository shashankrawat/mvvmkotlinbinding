package com.mvvmkotlinbinding.data.app_prefs

import android.util.Log
import com.google.gson.Gson
import com.mvvmkotlinbinding.data.data_beans.UserDataBean
import com.mvvmkotlinbinding.utils.PreferencesUtil

class UserSessionImpl : UserSession {
    private var mPreferencesUtil: PreferencesUtil? = null
    private var mGson: Gson? = null

    private constructor() {}
    private constructor(preferencesUtil: PreferencesUtil?, gson: Gson?) {
        mPreferencesUtil = preferencesUtil
        mGson = gson
    }

    override fun saveIsUserSignedIn(isUserLoggedIn: Boolean) {
        mPreferencesUtil!!.savePreferencesBoolean(UserSession.PREF_IS_SIGNED_IN, isUserLoggedIn)
    }

    override val isUserLoggedIn: Boolean
        get() = mPreferencesUtil!!.getPreferencesBoolean(UserSession.PREF_IS_SIGNED_IN)

    override fun saveDeviceId(deviceId: String) {
        mPreferencesUtil!!.savePreferences(UserSession.PREF_DEVICE_ID, deviceId)
    }

    override val savedDeviceId: String?
        get() = mPreferencesUtil!!.getPreferences(UserSession.PREF_DEVICE_ID)

    override fun saveUserData(userData: UserDataBean?) {
        mPreferencesUtil!!.savePreferencesBoolean(UserSession.PREF_IS_SIGNED_IN, true)
        mPreferencesUtil!!.savePreferences(UserSession.PREF_USER_DATA, mGson!!.toJson(userData))
    }

    override val savedUserData: UserDataBean?
        get() {
            var user: UserDataBean? = null
            try {
                user = mGson!!.fromJson(
                    mPreferencesUtil!!.getPreferences(UserSession.PREF_USER_DATA),
                    UserDataBean::class.java
                )
            } catch (ex: Exception) {
                Log.e(TAG, ex.toString())
            }
            return user
        }

    override fun saveUserToken(token: String?) {
        mPreferencesUtil!!.savePreferences(UserSession.PREF_USER_TOKEN, token)
    }

    override val savedUserToken: String?
        get() = mPreferencesUtil!!.getPreferences(UserSession.PREF_USER_TOKEN)

    companion object {
        private val TAG = UserSessionImpl::class.java.name
        private var prefInstance: UserSessionImpl? = null
        fun getInstance(preferencesUtil: PreferencesUtil?, gson: Gson?): UserSessionImpl? {
            if (prefInstance == null) {
                synchronized(UserSessionImpl::class.java) {
                    if (prefInstance == null) {
                        prefInstance = UserSessionImpl(preferencesUtil, gson)
                    }
                }
            }
            return prefInstance
        }
    }
}