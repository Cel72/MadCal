package com.cs407.madcal.ui

import android.os.Bundle
import android.widget.CalendarView
import androidx.appcompat.app.AppCompatActivity
import com.cs407.madcal.R
import com.cs407.madcal.utils.StatusBarUtil


class CalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setImageImmersive(this, isLightStatusBar = true)
        setContentView(R.layout.aty_calendar_layout)
        val calendarView: CalendarView = findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            println("Selected Date: $selectedDate")
        }
    }
}