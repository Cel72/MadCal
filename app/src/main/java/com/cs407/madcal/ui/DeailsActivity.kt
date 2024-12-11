package com.cs407.madcal.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.cs407.madcal.R
import com.cs407.madcal.model.NewsItem
import com.cs407.madcal.utils.CacheUtils
import com.cs407.madcal.utils.NewsItemUtils.setImageUrl
import com.cs407.madcal.utils.StatusBarUtil


class DeailsActivity : AppCompatActivity() {
    companion object{
        var newsItem = NewsItem(0,"","","")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setImageImmersive(this, isLightStatusBar = true)
        setContentView(R.layout.aty_details_layout)

        val coverImage: ImageView = findViewById(R.id.coverImage)
        coverImage.setImageUrl(newsItem.photo)

        val title: TextView = findViewById(R.id.title)
        title.text = newsItem.title

        val description: TextView = findViewById(R.id.description)
        description.text = newsItem.description

        val followButton: AppCompatButton = findViewById(R.id.followButton)
        followButton.setOnClickListener {
            switchFollowButton(true)
        }

        switchFollowButton(false)
    }

    private fun switchFollowButton(isSwitch: Boolean){
        val followButton: AppCompatButton = findViewById(R.id.followButton)
        var isFind = false
        CacheUtils.getNewsItemsDate(newsItem.time).forEach {
            if(it.title == newsItem.title){
                isFind = true
            }
        }

        if(isFind){
            followButton.text = "Remove Event"
        }else{
            followButton.text = "Add Event"
        }

        if(isSwitch){
            if(isFind){
                CacheUtils.removeNewsItemsDate(newsItem)
                followButton.text = "Add Event"
            }else{
                CacheUtils.addNewsItemsDate(newsItem)
                followButton.text = "Remove Event"
            }
        }
    }
}