package com.cs407.madcal.ui.main.food

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.madcal.R
import com.cs407.madcal.databinding.FragmentNotificationsBinding
import com.cs407.madcal.model.NewsItem
import com.cs407.madcal.ui.main.music.adapter.MusicAdapter

class FoodsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvMusic.layoutManager = LinearLayoutManager(activity)

        val newsList = listOf(
            NewsItem(
                R.drawable.food_one,
                "Taste of Madison: A Culinary Extravaganza",
                "Join us on August 30-31, 2025, at Capitol Square for the Taste of Madison, a free event featuring over 80 local food vendors, a variety of beverages, and live music across multiple stages, ..."
            ),
            NewsItem(
                R.drawable.food_two,
                "Brat Fest: World's Largest Brat Festival",
                "Celebrate Memorial Day weekend, May 23-26, 2025, at Willow Island for Brat Fest, the world's largest bratwurst festival. Enjoy grilled brats, live music, and family-friendly activities. This annual tradition supports local charities and brings the."
            ),
            NewsItem(
                R.drawable.food_three,
                "Madison Vegan Holiday Pop-Up Marke. ",
                "Discover a variety of vegan delights on December 15, 2024, at the Goodman Community Center during the Madison Vegan Holiday Pop-Up Market. Explore plant-based foods, cruelty-free products, and unique gifts from local vendors."
            ),
            NewsItem(
                R.drawable.food_four,
                "Uncork Me Wisconsin: Wine Tasting Event",
                "Indulge in a selection of Wisconsin's finest wines on May 11, 2025, at Breese Stevens Field during Uncork Me Wisconsin. Sample a variety of wines from local wineries..."
            ), NewsItem(
                R.drawable.food_three,
                "Madison Jazz Nights: Rhythm & Groove",
                "Dive into the soulful tunes of Madison's vibrant jazz scene! Join us at the High Noon Saloon on October 25th for an unforgettable evening of live performances by the city's top jazz bands."
            ),
            NewsItem(
                R.drawable.food_four,
                "Live on State: Indie Sounds of Madison",
                "Celebrate the local indie music scene with an outdoor concert at the Memorial Union Terrace on October 28th. Enjoy performances by emerging Madison artists as they share their unique sounds under the stars."
            )
        )

        val adapter = MusicAdapter(newsList, activity as Context)
        binding.rvMusic.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}