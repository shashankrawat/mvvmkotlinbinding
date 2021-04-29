package com.mvvmkotlinbinding.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.fragment.app.FragmentManager;

import com.mvvmkotlinbinding.app_common_components.dialogs.MonthYearPickerDialog;
import com.mvvmwithdatabinding.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class CommonUtils
{
    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }


    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static void showDatePicker(Context mContext, ObservableField<String> dateViewField, String outputFormat, Boolean isCurrentDateLimit) {
        // Get Current Date
        // Get Current Date
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateViewField.set(DateFormatingClass.formatDateFromString(
                        DateFormatingClass.DATE_FORMAT_5,
                        outputFormat,
                        dayOfMonth + "-" + (month + 1) + "-" + year
                ));
            }
        }, mYear, mMonth, mDay);


        if(isCurrentDateLimit) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        datePickerDialog.show();
    }


    public static void showMonthYearPickerDialog(ObservableField<String> field, FragmentManager fragmentManager){
        MonthYearPickerDialog monthYearDialog = MonthYearPickerDialog.getInstance();
        monthYearDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                field.set(""+year);
            }
        });

        monthYearDialog.show(fragmentManager, MonthYearPickerDialog.TAG);
    }


    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    Log.e("NAME_FILE",""+cursor.getString(0));
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                Log.e("PDF_NAME_EXP", "" + e.toString());
            }
        }
        Log.e("PDF_NAME_4",""+result);
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static double getFileSize(Uri uri, Context context){
        double fileSize = 0;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    String fileSizeStr = cursor.getString(sizeIndex);
                    if(!TextUtils.isEmpty(fileSizeStr)){
                        fileSize = Double.parseDouble(fileSizeStr)/1024;
                    }
                    Log.e("FILE_SIZE",""+fileSize);
                }
            } catch (Exception e) {
                Log.e("FILE_NAME_EXP", "" + e.toString());
            }
        }
        return fileSize;
    }


    public static String convertToString(Uri uri, Context context){
        String fileString = null;
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            Log.e("BASE64_0",""+in);
            byte [] bytes = getBytes(in);
            Log.e("BASE64_0",""+bytes);
            Log.d("BASE64_1", "onActivityResult: bytes size="+bytes.length);
            Log.d("BASE64_2", ""+ Base64.encodeToString(bytes,Base64.DEFAULT));
            fileString = Base64.encodeToString(bytes,Base64.DEFAULT);

        } catch (Exception e) {
            Log.e("BASE64_3", "ERROR: " + e.toString());
        }

        return fileString;
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    public static File createFile(Context context, String suffix) throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String fileName = context.getString(R.string.app_name) + timeStamp + "_";

        return File.createTempFile(
                fileName,  /* prefix */
                suffix,         /* suffix */
                getStorageDir(context)      /* directory */
        );
    }

    private static File getStorageDir(Context context){
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (storageDir != null) {
            Log.e("STORAGE_PATH",""+storageDir.getAbsolutePath());
        }
        return storageDir;
    }
}
