package com.mvvmkotlinbinding.utils

import android.content.SharedPreferences

class PreferencesUtil(private val mSharedPreferences: SharedPreferences) {
    private val DEFAULT_INT = 0

    /**
     * Shared Preference.
     */
    // Save strings in preference
    fun savePreferences(key: String?, value: String?) {
        val editor = mSharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    // Save boolean values in preference
    fun savePreferencesBoolean(key: String?, value: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    // Save boolean values in preference
    fun savePreferencesLong(key: String?, value: Long) {
        val editor = mSharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    // Save int values in preference
    fun savePreferencesInt(key: String?, value: Int) {
        val editor = mSharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    // Get string values from preference
    fun getPreferences(key: String?): String? {
        return mSharedPreferences.getString(key, null)
    }

    // Get boolean values from preference
    fun getPreferencesBoolean(key: String?): Boolean {
        return mSharedPreferences.getBoolean(key, false) //false is default value
    }

    // Get Long values from preference
    fun getPreferencesLong(key: String?): Long {
        return mSharedPreferences.getLong(key, DEFAULT_INT.toLong()) //false is default value
    }

    // Get int values from preference
    fun getPreferencesInt(key: String?): Int {
        return mSharedPreferences.getInt(key, DEFAULT_INT) //false is default value
    }

    companion object {
        const val PREFERENCE_NAME = "com.mymvvmsample"
    }
}