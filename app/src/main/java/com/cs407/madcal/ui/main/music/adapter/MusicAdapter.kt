package com.cs407.madcal.ui.main.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.madcal.R
import com.cs407.madcal.ui.CalendarActivity
import com.cs407.madcal.ui.DeailsActivity
import com.cs407.madcal.utils.ActivityUtils
import com.hi.planet.model.NewsItem



class MusicAdapter(private val newsList: List<NewsItem>, private val context:Context) :
    RecyclerView.Adapter<MusicAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.imageView.setImageResource(newsItem.imageResId)
        holder.titleTextView.text = newsItem.title
        holder.descriptionTextView.text = newsItem.description
        holder.btnAdd.setOnClickListener {
            ActivityUtils.startActivity(context, CalendarActivity::class.java)
        }

        holder.itemView.setOnClickListener{
            ActivityUtils.startActivity(context, DeailsActivity::class.java)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val btnAdd: TextView = itemView.findViewById(R.id.btnAdd)
    }
}