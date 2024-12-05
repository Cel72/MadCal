package com.hi.planet

import android.app.Application
import com.hi.planet.ui.main.MainActivity


class MainApplication : Application() {

    companion object {
        lateinit var instance: MainApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}