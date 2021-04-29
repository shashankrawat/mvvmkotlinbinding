package com.mvvmkotlinbinding.data.app_prefs;

import androidx.annotation.NonNull;

import com.mvvmkotlinbinding.data.data_beans.UserDataBean;

public interface UserSession
{
    String PREF_IS_SIGNED_IN = "is_signed_in";
    String PREF_DEVICE_ID = "pref_device_id";
    String PREF_USER_TOKEN = "user_token";
    String PREF_USER_DATA = "user_data";

    void saveIsUserSignedIn(boolean isUserLoggedIn);
    boolean isUserLoggedIn();

    void saveDeviceId(@NonNull String deviceId);
    String getSavedDeviceId();

    void saveUserData(UserDataBean userData);
    UserDataBean getSavedUserData();

    void saveUserToken(String token);
    String getSavedUserToken();
}
