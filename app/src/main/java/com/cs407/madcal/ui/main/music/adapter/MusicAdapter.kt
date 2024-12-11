package com.cs407.madcal.ui.main.music.adapter

import android.content.Context

import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cs407.madcal.R

import com.cs407.madcal.model.NewsItem
import com.cs407.madcal.ui.DeailsActivity
import com.cs407.madcal.utils.NewsItemUtils.setImageUrl


class MusicAdapter(
    private val newsList: List<NewsItem>,
    private val context:Context
) : RecyclerView.Adapter<MusicAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]

        holder.imageView.setImageUrl(newsItem.photo)
        holder.titleTextView.text = newsItem.title
        holder.descriptionTextView.text = newsItem.description

        holder.itemView.setOnClickListener{
            DeailsActivity.newsItem = newsList[position]
            context.startActivity(Intent(context, DeailsActivity::class.java))

        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

    }
}