package com.hi.planet.ui.main.music

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hi.planet.R

import com.hi.planet.databinding.FragmentHomeBinding
import com.hi.planet.model.NewsItem
import com.hi.planet.ui.main.music.adapter.MusicAdapter


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.rvMusic.layoutManager = LinearLayoutManager(activity)

        val newsList = listOf(
            NewsItem(
                R.drawable.music_one,
                "Madison World Music Festival: A Global Musical Journey",
                "Embark on a cultural adventure at the Madison World Music Festival on September 14th and 15th at the Memorial Union Terrace..."
            ),
            NewsItem(
                R.drawable.music_two,
                "Madison Jazz Nights: Rhythm & Groove",
                "Dive into the soulful tunes of Madison's vibrant jazz scene! Join us at the High Noon Saloon on October 25th for an unforgettable evening of live performances by the city's top jazz bands."
            ),
            NewsItem(
                R.drawable.music_three,
                "Live on State: Indie Sounds of Madison",
                "Celebrate the local indie music scene with an outdoor concert at the Memorial Union Terrace on October 28th. Enjoy performances by emerging Madison artists as they share their unique sounds under the stars."
            ),
            NewsItem(
                R.drawable.music_one,
                "Madison World Music Festival: A Global Musical Journey",
                "Embark on a cultural adventure at the Madison World Music Festival on September 14th and 15th at the Memorial Union Terrace..."
            ), NewsItem(
                R.drawable.music_two,
                "Madison Jazz Nights: Rhythm & Groove",
                "Dive into the soulful tunes of Madison's vibrant jazz scene! Join us at the High Noon Saloon on October 25th for an unforgettable evening of live performances by the city's top jazz bands."
            ),
            NewsItem(
                R.drawable.music_three,
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
        println("onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        println("onDestroy")
        super.onDestroy()
    }

    override fun onDetach() {
        println("onDetach")
        super.onDetach()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        println("onHiddenChanged " + !hidden)
    }
}