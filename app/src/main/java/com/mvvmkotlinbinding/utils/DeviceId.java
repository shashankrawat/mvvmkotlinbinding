package com.mvvmkotlinbinding.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mvvmkotlinbinding.data.app_prefs.UserSession;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeviceId
{
    private static final String TAG = "DeviceId";
    /**
     * The singleton of this class
     */
    private static DeviceId deviceId;

    /**
     * Private Constructor
     */
    private DeviceId() {
    }

    /**
     * Called to get an instance of the DeviceId helper.
     *
     * @return New instance of the DeviceId helper
     */
    public static DeviceId getInstance()
    {
        if (deviceId == null) {
            synchronized (DeviceId.class) {
                if (deviceId == null) {
                    deviceId = new DeviceId();
                }
            }
        }
        return deviceId;
    }

    /**
     * Called to get the {@code String} device id for this device. It is made of a SHA-256 hash
     * of unique setting on this device.
     *
     * @return The device id for this device.
     */
    public String getDeviceId(Context context, UserSession userSession) {
        String deviceId = userSession.getSavedDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = createId(context);
            if (TextUtils.isEmpty(deviceId)) {
                return null;
            }
            if (deviceId != null) {
                userSession.saveDeviceId(deviceId);
            }
        }
        return deviceId;
    }

    /**
     * Usually only called once when the device id has not been created yet.
     *
     * @return The new device id.
     */
    @Nullable
    @SuppressLint("HardwareIds")
    private String createId(Context context) {
        String id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String device = Build.DEVICE;
        id += device;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexCoder.toHex(md.digest(id.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Log.e(TAG, "createId: "+e.toString());
        }
        return null;
    }

}
