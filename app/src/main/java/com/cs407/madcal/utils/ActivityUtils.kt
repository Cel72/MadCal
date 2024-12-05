package com.cs407.madcal.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher

object ActivityUtils {

    /**
     * 启动目标 Activity
     *
     * @param context 上下文对象
     * @param targetActivity 目标 Activity 的 Class
     * @param extras 携带的参数（可选）
     */
    fun <T : Activity> startActivity(
        context: Context,
        targetActivity: Class<T>,
        extras: Bundle? = null
    ) {
        val intent = Intent(context, targetActivity)
        extras?.let { intent.putExtras(it) } // 如果有额外参数，将其附加到 Intent 中
        context.startActivity(intent)
    }

    /**
     * 扩展函数：通过当前 Context 启动目标 Activity
     *
     * @param targetActivity 目标 Activity 的 Class
     * @param extras 携带的参数（可选）
     */
    inline fun <reified T : Activity> Context.startActivity(
        extras: Bundle? = null
    ) {
        val intent = Intent(this, T::class.java)
        extras?.let { intent.putExtras(it) }
        startActivity(intent)
    }

    /**
     * 使用 ActivityResultLauncher 启动目标 Activity
     *
     * @param context 当前的 Context
     * @param launcher ActivityResultLauncher 对象
     * @param targetActivity 目标 Activity 的 Class
     * @param extras 携带的参数（可选）
     */
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


    /**
     * 扩展函数：通过 ActivityResultLauncher 启动目标 Activity
     *
     * @param context 当前的 Context
     * @param targetActivity 目标 Activity 的 Class
     * @param extras 携带的参数（可选）
     */
    inline fun <reified T : Activity> ActivityResultLauncher<Intent>.launchActivity(
        context: Context,
        extras: Bundle? = null
    ) {
        val intent = Intent(context, T::class.java)
        extras?.let { intent.putExtras(it) }
        launch(intent)
    }
}