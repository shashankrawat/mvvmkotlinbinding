package com.mvvmkotlinbinding.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateFormatingClass
{
    public static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_2 = "dd, MMMM yyyy h:mm a";
    public static final String DATE_FORMAT_3 = "yyyy-MM-dd";
    public static final String DATE_FORMAT_4 = "dd, MMM yyyy";
    public static final String DATE_FORMAT_5 = "dd-MM-yyyy";
    public static final String DATE_FORMAT_6 = "dd/MM/yyyy";
    public static final String DATE_TIME_IMAGE_NAME = "YYYYMMddHHmmssSSS";
    public static final String TIME_FORMAT_1 = "h:mm a";

    /**
     * Just a locking object for synchronized method calls.
     */

    public static String formatDateFromString(String inputFormat, String outputFormat, String inputDate)
    {
        Date parsed;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {
        Log.e("Error", "ParseException - dateFormat");
        }
        return outputDate;
    }

    public static Long getTimeDifference(String inputTime, String inputFormat)
    {
        SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat, Locale.getDefault());
        Date date;
        Long timeDiff = 0L;
        try {
            date = inputSDF.parse(inputTime);
            long currentTimeInMS = Calendar.getInstance().getTimeInMillis();
            timeDiff = date.getTime() - currentTimeInMS;
        } catch (ParseException e) {
        e.printStackTrace();
        }
        return timeDiff;
    }

    public static String getRemainingDiscountTime(Long millsec)
    {
        long sec = TimeUnit.MILLISECONDS.toSeconds(millsec) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millsec));
        long min = TimeUnit.MILLISECONDS.toMinutes(millsec) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millsec)
        );
        long hrs = TimeUnit.MILLISECONDS.toHours(millsec) - TimeUnit.DAYS.toHours(
                TimeUnit.MILLISECONDS.toDays(millsec)
        );
        long days = TimeUnit.MILLISECONDS.toDays(millsec);
        return String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds", days, hrs, min, sec);
    }

    /*fun getFutureDate(inputFormat: String?, outputFormat: String?, inputDate: String?): String
    {
        var parsed: Date? = null
        var outputDate = ""
        val df_input = SimpleDateFormat(inputFormat, Locale.getDefault())
        val df_output = SimpleDateFormat(outputFormat, Locale.getDefault())
        try {
            parsed = df_input.parse(inputDate)
            val c: Calendar = Calendar.getInstance()
            c.time = parsed
            c.add(Calendar.DAY_OF_YEAR, 7);
            outputDate = df_output.format(c.time)
        } catch (e: ParseException) {
        Log.e("Error", "ParseException - dateFormat")
        }
        return outputDate
    }*/

    /*fun isABackDate(inputFormat: String, inputDate: String): Boolean
    {
        var isBackDate = false
        val sdf = SimpleDateFormat(inputFormat, Locale.getDefault())
        try {
            val date = sdf.parse(inputDate)
            if (System.currentTimeMillis() > date.time) {
                //Entered date is backdated from current date
                isBackDate = true
            }
        } catch (e: ParseException) {
        Log.e("Error", "ParseException - dateFormat : $e")

        }
        return isBackDate
    }*/

    public static String getCurrentDate(String outputFormat){
        String currentDate = "";
        SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat, Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        try {
            currentDate = outputSDF.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentDate;
    }

    public static String getAge(String inputDate, String inputFormat){

        int selectedMonth, selectedYear;
        int currentMonth, currentYear;
        int ageYears;
        String age = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());

        try {
            Date d = df_input.parse(inputDate);
            Calendar cal = Calendar.getInstance();
            currentMonth = cal.get(Calendar.MONTH)+1;
            currentYear = cal.get(Calendar.YEAR);
            Log.e("AGE_1" , ""+currentYear+", "+currentMonth);

            cal.setTime(d);
            selectedMonth = cal.get(Calendar.MONTH)+1;
            selectedYear = cal.get(Calendar.YEAR);

            Log.e("AGE_2" , ""+selectedYear+", "+selectedMonth);

            ageYears = currentYear - selectedYear;
            if(currentMonth < selectedMonth) {
                ageYears = ageYears - 1;
            }

            age = ""+ageYears;

            Log.e("AGE_4" , ""+age);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "ParseException - dateFormat"+" : " + e.toString());
        }

        return age;
    }
}
