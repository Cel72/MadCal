package com.cs407.madcal.ui.main.music

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cs407.madcal.databinding.FragmentHomeBinding
import com.cs407.madcal.ui.main.music.adapter.MusicAdapter
import com.cs407.madcal.utils.NewsItemUtils


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

        NewsItemUtils.getItems("music"){
            val adapter = MusicAdapter(it, activity as Context)
            binding.rvMusic.adapter = adapter
        }
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