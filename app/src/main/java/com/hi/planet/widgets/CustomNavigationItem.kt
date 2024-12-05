package com.hi.planet.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.hi.planet.R

class CustomNavigationItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val iconView: AppCompatImageView
    private val titleView: TextView

    private var selectedIconRes: Int = 0
    private var unselectedIconRes: Int = 0
    private var selectedTextColor: Int =
        ContextCompat.getColor(context, R.color.bottom_nav_selected_text_color)
    private var unselectedTextColor: Int =
        ContextCompat.getColor(context, R.color.bottom_nav_unselected_text_color)

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.custom_navigation_item, this, true)

        iconView = findViewById(R.id.item_icon)
        titleView = findViewById(R.id.item_title)
    }

    fun setIcons(@DrawableRes unselectedIcon: Int, @DrawableRes selectedIcon: Int) {
        this.unselectedIconRes = unselectedIcon
        this.selectedIconRes = selectedIcon
        iconView.setImageResource(unselectedIcon)
    }

    fun setTextColors(selectedColor: Int, unselectedColor: Int) {
        this.selectedTextColor = selectedColor
        this.unselectedTextColor = unselectedColor
        titleView.setTextColor(unselectedTextColor)
    }

    fun setTitle(text: String) {
        titleView.text = text
    }

     fun setSelectedC(isSelected: Boolean) {
        if (isSelected) {
            iconView.setImageResource(selectedIconRes)
            titleView.setTextColor(selectedTextColor)
        } else {
            iconView.setImageResource(unselectedIconRes)
            titleView.setTextColor(unselectedTextColor)
        }
    }
}