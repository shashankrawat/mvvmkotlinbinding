package com.mvvmkotlinbinding.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.mvvmkotlinbinding.data.app_prefs.UserSession
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class DeviceId
/**
 * Private Constructor
 */
private constructor() {
    /**
     * Called to get the `String` device id for this device. It is made of a SHA-256 hash
     * of unique setting on this device.
     *
     * @return The device id for this device.
     */
    fun getDeviceId(context: Context, userSession: UserSession?): String? {
        var deviceId = userSession?.savedDeviceId
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = createId(context)
            if (TextUtils.isEmpty(deviceId)) {
                return null
            }
            if (deviceId != null) {
                userSession?.saveDeviceId(deviceId)
            }
        }
        return deviceId
    }

    /**
     * Usually only called once when the device id has not been created yet.
     *
     * @return The new device id.
     */
    @SuppressLint("HardwareIds")
    private fun createId(context: Context): String? {
        var id = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val device = Build.DEVICE
        id += device
        try {
            val md = MessageDigest.getInstance("SHA-256")
            return HexCoder.toHex(md.digest(id.toByteArray(charset("UTF-8"))))
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "createId: $e")
        } catch (e: UnsupportedEncodingException) {
            Log.e(TAG, "createId: $e")
        }
        return null
    }

    companion object {
        private const val TAG = "DeviceId"

        /**
         * The singleton of this class
         */
        private var deviceId: DeviceId? = null

        /**
         * Called to get an instance of the DeviceId helper.
         *
         * @return New instance of the DeviceId helper
         */
        val instance: DeviceId?
            get() {
                if (deviceId == null) {
                    synchronized(DeviceId::class.java) {
                        if (deviceId == null) {
                            deviceId = DeviceId()
                        }
                    }
                }
                return deviceId
            }
    }
}