package com.mvvmkotlinbinding.app_common_components.app_abstracts;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mvvmkotlinbinding.data.app_prefs.UserSession;
import com.mvvmkotlinbinding.data.app_prefs.UserSessionImpl;
import com.mvvmkotlinbinding.utils.PreferencesUtil;

import static android.content.Context.MODE_PRIVATE;
import static com.mvvmkotlinbinding.utils.PreferencesUtil.PREFERENCE_NAME;

abstract public class BaseViewModel extends AndroidViewModel
{
    private UserSession mUserSession;
    private Gson mGson;
    private PreferencesUtil mPreferencesUtil;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        getPreferencesUtil(application);
    }


    // get GSON Object
    public Gson getGsonBuilder() {
        if (mGson == null)
            mGson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .setVersion(1.0)
                    .setLenient()
                    .create();
        return mGson;
    }

    // get shared preference
    private void getPreferencesUtil(Application application) {
        if (mPreferencesUtil == null)
            mPreferencesUtil = new PreferencesUtil(application.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE));
    }


    // get User sessions
    public UserSession getUserSession() {
        if (mUserSession == null) {
            mUserSession = UserSessionImpl.getInstance(mPreferencesUtil, getGsonBuilder());
        }
        return mUserSession;
    }
}
