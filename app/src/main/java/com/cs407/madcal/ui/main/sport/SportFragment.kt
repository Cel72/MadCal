package com.cs407.madcal.ui.main.sport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.madcal.databinding.FragmentDashboardBinding
import com.cs407.madcal.ui.main.music.adapter.MusicAdapter
import com.cs407.madcal.utils.NewsItemUtils

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

        NewsItemUtils.getItems("sport"){
            val adapter = MusicAdapter(it, activity as Context)
            binding.rvMusic.adapter = adapter
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}