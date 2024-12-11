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

    fun getSpecificTimeNoStartZero(time: Long):String{
        val specificTime = getSpecificTime(time).split("/")
        val year = specificTime[0]
        val month = (if(specificTime[1].startsWith("0")){
            specificTime[1].replace("0","")
        }else{
            specificTime[1]
        })
        val day = (if(specificTime[2].startsWith("0")){
            specificTime[2].replace("0","")
        }else{
            specificTime[2]
        })
        return "$year/$month/$day"
    }

}