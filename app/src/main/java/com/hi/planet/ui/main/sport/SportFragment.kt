package com.hi.planet.ui.main.sport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hi.planet.R
import com.hi.planet.databinding.FragmentDashboardBinding
import com.hi.planet.model.NewsItem
import com.hi.planet.ui.main.music.adapter.MusicAdapter

class SportFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvMusic.layoutManager = LinearLayoutManager(activity)

        val newsList = listOf(
            NewsItem(
                R.drawable.sprot_one,
                "Badgers Football Homecoming Game: Wisconsin vs. Minnesota",
                "Cheer on the Wisconsin Badgers as they face off against their historic rivals, the Minnesota Golden Gophers, on October 21st at Camp Randall Stadium. Experience the thrill of college football, ..."
            ),
            NewsItem(
                R.drawable.sport_two,
                "Madison Marathon: Race Through the City",
                "Lace up your running shoes for the Madison Marathon on November 12th! This scenic race will take participants through some of the most beautiful parts of the city, including the University."
            ),
            NewsItem(
                R.drawable.sport_three,
                "Madison Capitols Hockey: Friday Night Showdown",
                "Get ready for a night of fast-paced action as the Madison Capitols take on the Green Bay Gamblers at Bob Suter's Capitol Ice Arena on November 3rd. Watch incredible goals, powerful plays."
            ),
            NewsItem(
                R.drawable.sport_four,
                "Madison Mallards Baseball: Summer Fun at Warner Park",
                "Experience the excitement of Madison Mallards baseball at Warner Park on Saturday, July 15th at 7:05 PM! Join fellow fans for an evening of America's favorite pastime..."
            ), NewsItem(
                R.drawable.sport_three,
                "Madison Jazz Nights: Rhythm & Groove",
                "Dive into the soulful tunes of Madison's vibrant jazz scene! Join us at the High Noon Saloon on October 25th for an unforgettable evening of live performances by the city's top jazz bands."
            ),
            NewsItem(
                R.drawable.sport_two,
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