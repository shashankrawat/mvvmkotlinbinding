package com.mvvmkotlinbinding.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateFormatingClass {
    const val DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT_2 = "dd, MMMM yyyy h:mm a"
    const val DATE_FORMAT_3 = "yyyy-MM-dd"
    const val DATE_FORMAT_4 = "dd, MMM yyyy"
    const val DATE_FORMAT_5 = "dd-MM-yyyy"
    const val DATE_FORMAT_6 = "dd/MM/yyyy"
    const val DATE_TIME_IMAGE_NAME = "YYYYMMddHHmmssSSS"
    const val TIME_FORMAT_1 = "h:mm a"

    /**
     * Just a locking object for synchronized method calls.
     */
    fun formatDateFromString(
        inputFormat: String?,
        outputFormat: String?,
        inputDate: String?
    ): String {
        val parsed: Date
        var outputDate = ""
        val df_input = SimpleDateFormat(inputFormat, Locale.getDefault())
        val df_output = SimpleDateFormat(outputFormat, Locale.getDefault())
        try {
            parsed = df_input.parse(inputDate)
            outputDate = df_output.format(parsed)
        } catch (e: ParseException) {
            Log.e("Error", "ParseException - dateFormat")
        }
        return outputDate
    }

    fun getTimeDifference(inputTime: String?, inputFormat: String?): Long {
        val inputSDF = SimpleDateFormat(inputFormat, Locale.getDefault())
        val date: Date
        var timeDiff = 0L
        try {
            date = inputSDF.parse(inputTime)
            val currentTimeInMS = Calendar.getInstance().timeInMillis
            timeDiff = date.time - currentTimeInMS
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return timeDiff
    }

    fun getRemainingDiscountTime(millsec: Long?): String {
        val sec = TimeUnit.MILLISECONDS.toSeconds(millsec!!) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millsec)
        )
        val min = TimeUnit.MILLISECONDS.toMinutes(millsec) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millsec)
        )
        val hrs = TimeUnit.MILLISECONDS.toHours(millsec) - TimeUnit.DAYS.toHours(
            TimeUnit.MILLISECONDS.toDays(millsec)
        )
        val days = TimeUnit.MILLISECONDS.toDays(millsec)
        return String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds", days, hrs, min, sec)
    }

    fun getFutureDate(inputFormat: String?, outputFormat: String?, inputDate: String?): String
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
    }


    fun isABackDate(inputFormat: String, inputDate: String): Boolean
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
    }


    fun getCurrentDate(outputFormat: String?): String {
        var currentDate = ""
        val outputSDF = SimpleDateFormat(outputFormat, Locale.getDefault())
        val cal = Calendar.getInstance()
        val date = cal.time
        try {
            currentDate = outputSDF.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return currentDate
    }

    fun getAge(inputDate: String?, inputFormat: String?): String {
        val selectedMonth: Int
        val selectedYear: Int
        val currentMonth: Int
        val currentYear: Int
        var ageYears: Int
        var age = ""
        val df_input = SimpleDateFormat(inputFormat, Locale.getDefault())
        try {
            val d = df_input.parse(inputDate)
            val cal = Calendar.getInstance()
            currentMonth = cal[Calendar.MONTH] + 1
            currentYear = cal[Calendar.YEAR]
            Log.e("AGE_1", "$currentYear, $currentMonth")
            cal.time = d
            selectedMonth = cal[Calendar.MONTH] + 1
            selectedYear = cal[Calendar.YEAR]
            Log.e("AGE_2", "$selectedYear, $selectedMonth")
            ageYears = currentYear - selectedYear
            if (currentMonth < selectedMonth) {
                ageYears = ageYears - 1
            }
            age = "" + ageYears
            Log.e("AGE_4", "" + age)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Error", "ParseException - dateFormat : $e")
        }
        return age
    }
}