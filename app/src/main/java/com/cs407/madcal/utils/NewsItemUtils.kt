package com.cs407.madcal.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.cs407.madcal.model.NewsItem
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.HttpURLConnection
import java.net.URL

object NewsItemUtils {
    fun getItems(path:String, callback:(List<NewsItem>)->Unit){
        CoroutineScope(Dispatchers.Main).launch {
            val users = Firebase.database.getReference(path).get().await().value as List<HashMap<String,*>>
            callback(
                users.map {
                    NewsItem(
                        time = it["time"] as Long,
                        title = it["title"] as String,
                        description = it["description"] as String,
                        photo = it["photo"] as String
                    )
                }
            )
        }
    }

    fun ImageView.setImageUrl(url: String){
        CoroutineScope(Dispatchers.IO).launch {
            downloadImage(url)?.let {
                CoroutineScope(Dispatchers.Main).launch{
                    this@setImageUrl.setImageBitmap(it)
                }
            }
        }
    }

    private fun downloadImage(url: String): Bitmap? {
        try {
            val urlConnection = URL(url)
            val connection = urlConnection
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            val myBitmap = BitmapFactory.decodeStream(input)
            return myBitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}