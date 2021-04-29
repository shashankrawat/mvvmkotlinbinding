package com.mvvmkotlinbinding.utils

import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import com.mvvmkotlinbinding.app_common_components.dialogs.MonthYearPickerDialog
import com.mvvmkotlinbinding.app_common_components.dialogs.MonthYearPickerDialog.Companion.instance
import com.mvvmwithdatabinding.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

object CommonUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val activeNetwork = connectivity.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnected
        }
        return false
    }

    fun toCamelCase(init: String?): String? {
        if (init == null) return null
        val ret = StringBuilder(init.length)
        for (word in init.split(" ").toTypedArray()) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word[0]))
                ret.append(word.substring(1).toLowerCase())
            }
            if (ret.length != init.length) ret.append(" ")
        }
        return ret.toString()
    }

    fun showDatePicker(
        mContext: Context?,
        dateViewField: ObservableField<String?>,
        outputFormat: String?,
        isCurrentDateLimit: Boolean
    ) {
        // Get Current Date
        // Get Current Date
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(mContext!!, { view, year, month, dayOfMonth ->
            dateViewField.set(
                DateFormatingClass.formatDateFromString(
                    DateFormatingClass.DATE_FORMAT_5,
                    outputFormat,
                    dayOfMonth.toString() + "-" + (month + 1) + "-" + year
                )
            )
        }, mYear, mMonth, mDay)
        if (isCurrentDateLimit) {
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }
        datePickerDialog.show()
    }

    fun showMonthYearPickerDialog(
        field: ObservableField<String?>,
        fragmentManager: FragmentManager?
    ) {
        val monthYearDialog = instance
        monthYearDialog.setListener { view, year, month, dayOfMonth -> field.set("" + year) }
        monthYearDialog.show(fragmentManager!!, MonthYearPickerDialog.TAG)
    }

    fun getFileName(uri: Uri, context: Context): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        Log.e("NAME_FILE", "" + cursor.getString(0))
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            } catch (e: Exception) {
                Log.e("PDF_NAME_EXP", "" + e.toString())
            }
        }
        Log.e("PDF_NAME_4", "" + result)
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }

    fun getFileSize(uri: Uri, context: Context): Double {
        var fileSize = 0.0
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, null, null, null, null).use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        val fileSizeStr = cursor.getString(sizeIndex)
                        if (!TextUtils.isEmpty(fileSizeStr)) {
                            fileSize = fileSizeStr.toDouble() / 1024
                        }
                        Log.e("FILE_SIZE", "" + fileSize)
                    }
                }
            } catch (e: Exception) {
                Log.e("FILE_NAME_EXP", "" + e.toString())
            }
        }
        return fileSize
    }

    fun convertToString(uri: Uri?, context: Context): String? {
        var fileString: String? = null
        try {
            val `in` = context.contentResolver.openInputStream(uri!!)
            Log.e("BASE64_0", "" + `in`)
            val bytes = getBytes(`in`)
            Log.e("BASE64_0", "" + bytes)
            Log.d("BASE64_1", "onActivityResult: bytes size=" + bytes.size)
            Log.d("BASE64_2", "" + Base64.encodeToString(bytes, Base64.DEFAULT))
            fileString = Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            Log.e("BASE64_3", "ERROR: $e")
        }
        return fileString
    }

    @Throws(IOException::class)
    private fun getBytes(inputStream: InputStream?): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream!!.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    @Throws(IOException::class)
    fun createFile(context: Context, suffix: String?): File {
        // Create an image file name
        val timeStamp = System.currentTimeMillis().toString()
        val fileName = context.getString(R.string.app_name) + timeStamp + "_"
        return File.createTempFile(
            fileName,  /* prefix */
            suffix,  /* suffix */
            getStorageDir(context) /* directory */
        )
    }

    private fun getStorageDir(context: Context): File? {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (storageDir != null) {
            Log.e("STORAGE_PATH", "" + storageDir.absolutePath)
        }
        return storageDir
    }
}