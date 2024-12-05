package com.hi.planet.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hi.planet.R
import com.hi.planet.utils.StatusBarUtil

class DeailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setImageImmersive(this, isLightStatusBar = true)
        setContentView(R.layout.aty_details_layout)
    }
}