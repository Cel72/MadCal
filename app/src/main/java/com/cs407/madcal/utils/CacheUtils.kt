package com.cs407.madcal.utils

import android.content.Context
import android.content.SharedPreferences
import com.cs407.madcal.MainApplication
import com.cs407.madcal.model.NewsItem
import com.cs407.madcal.model.NewsItemDate

object CacheUtils{

    private val sharedPreferences: SharedPreferences
        get() = MainApplication.instance.getSharedPreferences("cache_" + MainApplication.instance.packageName, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getNewsItemsDate(): List<NewsItemDate> {
        val saveString = sharedPreferences.getString("NewsItems", null)?:return emptyList()
        val list = mutableListOf<NewsItemDate>()
        saveString.split("\n").forEach {
            if (it == "") return@forEach
            val split = it.split("----NewsItemDateSplit----")
            if (split.size > 1) {
                list.add(NewsItemDate(split[0].toLong(), split[1]))
            }
        }
        return list
    }

    fun addNewsItemsDate(item: NewsItem){
        val list = getNewsItemsDate().toMutableList()
        list.add(NewsItemDate(item.time, item.title))
        var saveString = ""
        list.forEach {
            saveString += "${it.time}----NewsItemDateSplit----${it.title}\n"
        }
        editor.putString("NewsItems", saveString)
        editor.apply()
    }

    fun removeNewsItemsDate(item: NewsItem){
        val list = getNewsItemsDate().toMutableList()
        list.removeIf { it.title == item.title }
        var saveString = ""
        list.forEach {
            saveString += "${it.time}----NewsItemDateSplit----${it.title}\n"
        }
        editor.putString("NewsItems", saveString)
        editor.apply()
    }

    fun getNewsItemsDate(time: Long): List<NewsItemDate> {
        val list = mutableListOf<NewsItemDate>()
        val targetTime = TimeUtils.getSpecificTime(time)
        getNewsItemsDate().forEach {
            if(targetTime == TimeUtils.getSpecificTime(it.time)){
                list.add(NewsItemDate(it.time, it.title))
            }
        }
        return list
    }
}