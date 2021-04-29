package com.mvvmkotlinbinding.utils;


import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;

public class HexCoder
{
    private static final String TAG = "HexCoder";
    /**
     * Base {@code String)
     */
    private final static String HEX = "0123456789ABCDEF";

    /**
     * @param string The {@code String} that you want converted to a Hex String.
     * @return Hex String or {@code null} if something went wrong.
     */
    @Nullable
    public static String toHex(@NonNull String string) {
        try {
            return toHex(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "toHex: "+e.toString());
        }
        return null;
    }

    /**
     * @param hex The Hex {@code String} that you want converted to plain text.
     * @return Plain String
     */
    public static String fromHex(@NonNull String hex) {
        return new String(toByte(hex));
    }

    /**
     * Called to turn a HEX {@code String} back into a {@code String}
     *
     * @param hexString The HEX {@code String} to decode
     * @return The decoded EHX {@code String}
     */
    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        }
        return result;
    }

    /**
     * Called to build the completed HEX {@code String}
     *
     * @param buf The {@code byte[]} that we are creating the HEX encoding for.
     * @return The completed HEX {@code String}
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static String toHex(byte[] buf) {
        if (buf == null) {
            return "";
        }
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    /**
     * To append the HEX String together
     *
     * @param sb The {@code StringBuffer}
     * @param b  The {@code byte} data.
     */
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
