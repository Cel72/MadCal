package com.cs407.madcal.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cs407.madcal.R
import com.cs407.madcal.utils.StatusBarUtil


class DeailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setImageImmersive(this, isLightStatusBar = true)
        setContentView(R.layout.aty_details_layout)
    }
}