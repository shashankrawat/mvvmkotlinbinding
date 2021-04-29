package com.mvvmkotlinbinding.utils;

import android.content.SharedPreferences;

public class PreferencesUtil
{
    private SharedPreferences mSharedPreferences;
    private int DEFAULT_INT= 0;
    public static final String PREFERENCE_NAME = "com.mymvvmsample";

    public PreferencesUtil(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;
    }

    /**
     * Shared Preference.
     **/
    // Save strings in preference
    public void savePreferences(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Save boolean values in preference
    public void savePreferencesBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Save boolean values in preference
    public void savePreferencesLong(String key, long value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    // Save int values in preference
    public void savePreferencesInt(String key, int value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    // Get string values from preference
    public String getPreferences(String key) {
        return mSharedPreferences.getString(key, null);
    }

    // Get boolean values from preference
    public boolean getPreferencesBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);   //false is default value
    }

    // Get Long values from preference
    public long getPreferencesLong(String key) {
        return mSharedPreferences.getLong(key, DEFAULT_INT);   //false is default value
    }

    // Get int values from preference
    public int getPreferencesInt(String key) {
        return mSharedPreferences.getInt(key, DEFAULT_INT);   //false is default value
    }
}
