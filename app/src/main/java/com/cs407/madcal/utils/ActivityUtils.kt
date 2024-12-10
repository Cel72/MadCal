package com.cs407.madcal.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher

object ActivityUtils {


    fun <T : Activity> startActivity(
        context: Context,
        targetActivity: Class<T>,
        extras: Bundle? = null
    ) {
        val intent = Intent(context, targetActivity)
        extras?.let { intent.putExtras(it) }
        context.startActivity(intent)
    }


    inline fun <reified T : Activity> Context.startActivity(
        extras: Bundle? = null
    ) {
        val intent = Intent(this, T::class.java)
        extras?.let { intent.putExtras(it) }
        startActivity(intent)
    }


    fun <T : Activity> launchActivity(
        context: Context,
        launcher: ActivityResultLauncher<Intent>,
        targetActivity: Class<T>,
        extras: Bundle? = null
    ) {
        val intent = Intent(context, targetActivity)
        extras?.let { intent.putExtras(it) }
        launcher.launch(intent)
    }


    inline fun <reified T : Activity> ActivityResultLauncher<Intent>.launchActivity(
        context: Context,
        extras: Bundle? = null
    ) {
        val intent = Intent(context, T::class.java)
        extras?.let { intent.putExtras(it) }
        launch(intent)
    }
}