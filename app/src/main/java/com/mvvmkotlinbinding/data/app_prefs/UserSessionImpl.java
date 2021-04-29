package com.mvvmkotlinbinding.data.app_prefs;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.mvvmkotlinbinding.data.data_beans.UserDataBean;
import com.mvvmkotlinbinding.utils.PreferencesUtil;

public class UserSessionImpl implements UserSession
{
    private final static String TAG = UserSessionImpl.class.getName();
    private static UserSessionImpl prefInstance;
    private PreferencesUtil mPreferencesUtil;
    private Gson mGson;

    private UserSessionImpl(){
    }

    private UserSessionImpl(PreferencesUtil preferencesUtil, Gson gson) {
        this.mPreferencesUtil = preferencesUtil;
        this.mGson = gson;
    }

    public static UserSessionImpl getInstance(PreferencesUtil preferencesUtil, Gson gson)
    {
        if (prefInstance == null) {
            synchronized (UserSessionImpl.class) {
                if (prefInstance == null) {
                    prefInstance = new UserSessionImpl(preferencesUtil, gson);
                }
            }
        }
        return prefInstance;
    }

    @Override
    public void saveIsUserSignedIn(boolean isUserLoggedIn) {
        mPreferencesUtil.savePreferencesBoolean(PREF_IS_SIGNED_IN, isUserLoggedIn);
    }
    @Override
    public boolean isUserLoggedIn() {
        return mPreferencesUtil.getPreferencesBoolean(PREF_IS_SIGNED_IN);
    }

    @Override
    public void saveDeviceId(@NonNull String deviceId) {
        mPreferencesUtil.savePreferences(PREF_DEVICE_ID, deviceId);
    }
    @Override
    public String getSavedDeviceId() {
        return mPreferencesUtil.getPreferences(PREF_DEVICE_ID);
    }

    @Override
    public void saveUserData(UserDataBean userData) {
        mPreferencesUtil.savePreferencesBoolean(PREF_IS_SIGNED_IN, true);
        mPreferencesUtil.savePreferences(PREF_USER_DATA, mGson.toJson(userData));
    }
    @Override
    public UserDataBean getSavedUserData(){
        UserDataBean user = null;
        try {
            user = mGson.fromJson(mPreferencesUtil.getPreferences(PREF_USER_DATA), UserDataBean.class);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        return user;
    }

    @Override
    public void saveUserToken(String token) {
        mPreferencesUtil.savePreferences(PREF_USER_TOKEN, token);
    }
    @Override
    public String getSavedUserToken(){
        return mPreferencesUtil.getPreferences(PREF_USER_TOKEN);
    }

}


