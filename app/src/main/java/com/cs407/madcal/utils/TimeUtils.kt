package com.cs407.madcal.utils

import java.text.SimpleDateFormat
import java.util.Locale

object TimeUtils {
    private fun getStringTime(format: String?, time: Long): String{
        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        return simpleDateFormat.format(time)
    }

    fun getSpecificTime(time: Long): String {
        return getStringTime("yyyy/MM/dd", time)
    }
}