package com.cs407.madcal.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.cs407.madcal.R

class CustomBottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    interface OnNavigationItemSelectedListener {
        fun onItemSelected(position: Int)
    }

    private var listener: OnNavigationItemSelectedListener? = null
    private var selectedPosition: Int = -1
    private val items = mutableListOf<CustomNavigationItem>()

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.custom_bottom_navigation, this, true)

        items.add(findViewById(R.id.nav_home))
        items.add(findViewById(R.id.nav_amusement))
        items.add(findViewById(R.id.nav_mine))
        items.add(findViewById(R.id.nav_msg))

        setupClickListeners()
    }

    fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener) {
        this.listener = listener
    }

    private fun setupClickListeners() {
        items.forEachIndexed { index, item ->
            item.setOnClickListener {
                selectItem(index)
                listener?.onItemSelected(index)
            }
        }
    }

    private fun selectItem(position: Int) {
        if (position == selectedPosition) return

        if (selectedPosition != -1) {
            items[selectedPosition].setSelectedC(false)
        }

        items[position].setSelectedC(true)
        selectedPosition = position
    }

    fun setDefaultSelected(index: Int) {
        selectItem(index)
    }
}