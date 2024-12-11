package com.cs407.madcal.ui.main.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.cs407.madcal.databinding.FragmentCalendarBinding
import com.cs407.madcal.utils.CacheUtils
import com.cs407.madcal.utils.TimeUtils
import java.util.Calendar


class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        binding.calendarView.setOnCalendarDayClickListener(
            object : OnCalendarDayClickListener{
                override fun onClick(calendarDay: CalendarDay) {
                    val currentTime = "${calendarDay.calendar.get(Calendar.YEAR)}/${calendarDay.calendar.get(Calendar.MONTH)+1}/${calendarDay.calendar.get(Calendar.DAY_OF_MONTH)}"
                    var text = "$currentTime\n"
                    CacheUtils.getNewsItemsDate().forEach {
                        if(TimeUtils.getSpecificTimeNoStartZero(it.time) == currentTime){
                            text += it.title + "\n\n"
                        }
                    }
                    binding.selectText.text = text
                }
            }
        )
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateHighlight(){
        val list = mutableListOf<Calendar>()
        CacheUtils.getNewsItemsDate().forEach {
            val calendar:Calendar = Calendar.getInstance()
            val specificTime = TimeUtils.getSpecificTimeNoStartZero(it.time).split("/")
            calendar.set(specificTime[0].toInt(), specificTime[1].toInt() - 1, specificTime[2].toInt())
            list.add(calendar)
        }
        binding.calendarView.setHighlightedDays(list)
    }

    override fun onResume() {
        super.onResume()
        updateHighlight()
    }
}