package com.cs407.madcal.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.cs407.madcal.R
import com.cs407.madcal.databinding.AtyMainBinding
import com.cs407.madcal.ui.main.food.FoodsFragment
import com.cs407.madcal.ui.main.music.HomeFragment
import com.cs407.madcal.ui.main.setting.SettingsFragment
import com.cs407.madcal.ui.main.sport.SportFragment
import com.cs407.madcal.utils.StatusBarUtil
import com.cs407.madcal.widgets.CustomBottomNavigation
import com.cs407.madcal.widgets.CustomNavigationItem

class AtyActivity : AppCompatActivity() {

    private lateinit var binding: AtyMainBinding

    companion object {
        var moduleIdx = -1

        /**
         * The module index
         */
        const val MODULEIDX_HOME: Int = 0
        const val MODULEIDX_AMUSEMENT: Int = 1
        const val MODULEIDX_MSG: Int = 2
        const val MODULEIDX_MINE: Int = 3

    }

    private val fragmentsLiveStatus = booleanArrayOf(false, false, false, false)

    /**
     * Fragments
     */
    private var homeFragment: Fragment? = null
    private var amusementFragment: Fragment? = null
    private var msgFragment: Fragment? = null
    private var mineFragment: Fragment? = null

    private lateinit var timer: CountDownTimer
    private var isPreExit = false
    private var isExiting = false

    /**
     * Switch module
     */
    private val changeModuleReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            changeModule(intent.getIntExtra(getString(R.string.data), MODULEIDX_HOME))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setImageImmersive(this, isLightStatusBar = true)
        binding = AtyMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.customBottomNavigation.apply {
            findViewById<CustomNavigationItem>(R.id.nav_home)
                .apply {
                    setIcons(
                        R.drawable.img_4,
                        R.drawable.img_5
                    )
                    setTextColors(
                        ContextCompat.getColor(context, R.color.bottom_nav_selected_text_color),
                        ContextCompat.getColor(context, R.color.bottom_nav_unselected_text_color)
                    )
                    setTitle(ContextCompat.getString(context, R.string.title_home))
                }

            findViewById<CustomNavigationItem>(R.id.nav_amusement)
                .apply {
                    setIcons(
                        R.drawable.img_2,
                        R.drawable.img_3
                    )
                    setTextColors(
                        ContextCompat.getColor(context, R.color.bottom_nav_selected_text_color),
                        ContextCompat.getColor(context, R.color.bottom_nav_unselected_text_color)
                    )
                    setTitle(ContextCompat.getString(context, R.string.title_amusement))
                }

            findViewById<CustomNavigationItem>(R.id.nav_msg)
                .apply {
                    setIcons(
                        R.drawable.img_6,
                        R.drawable.img_7
                    )
                    setTextColors(
                        ContextCompat.getColor(context, R.color.bottom_nav_selected_text_color),
                        ContextCompat.getColor(context, R.color.bottom_nav_unselected_text_color)
                    )
                    setTitle(ContextCompat.getString(context, R.string.title_notifications))
                }

            findViewById<CustomNavigationItem>(R.id.nav_mine)
                .apply {
                    setIcons(
                        R.drawable.img,
                        R.drawable.img_1
                    )
                    setTextColors(
                        ContextCompat.getColor(context, R.color.bottom_nav_selected_text_color),
                        ContextCompat.getColor(context, R.color.bottom_nav_unselected_text_color)
                    )
                    setTitle(ContextCompat.getString(context, R.string.title_mine))
                }
            setDefaultSelected(0)

            setOnNavigationItemSelectedListener(object :
                CustomBottomNavigation.OnNavigationItemSelectedListener {
                override fun onItemSelected(position: Int) {
                    changeModule(position)
                }
            })

            val filter = IntentFilter("com.cs407.madcal.ACTION_CHANGE_MODULE")
            ContextCompat.registerReceiver(
                context,
                changeModuleReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            for (i in fragmentsLiveStatus.indices) {
                removeFragment(i)
            }

            changeModule(MODULEIDX_HOME)
            // Exit
            timer = object : CountDownTimer(5000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    isPreExit = false
                }
            }
        }
    }


    fun changeModule(vararg index: Int) {
        // Main Content
        val transaction = supportFragmentManager.beginTransaction()
        hideCurFragment(transaction, moduleIdx)
        when (index[0]) {
            MODULEIDX_HOME -> homeFragment?.apply {
                transaction.show(this)
            } ?: run {
                homeFragment = HomeFragment().also {
                    transaction.replace(R.id.main_home, it, MODULEIDX_HOME.toString())
                }
            }

            MODULEIDX_AMUSEMENT -> amusementFragment?.apply {
                transaction.show(this)
            } ?: run {
                amusementFragment = SportFragment().also {
                    transaction.replace(R.id.main_amusement, it, MODULEIDX_AMUSEMENT.toString())
                }
            }


            MODULEIDX_MSG -> msgFragment?.apply {
                transaction.show(this)
            } ?: run {
                msgFragment = FoodsFragment().also {
                    transaction.replace(R.id.main_msg, it, MODULEIDX_MSG.toString())
                }
            }

            MODULEIDX_MINE -> mineFragment?.apply {
                transaction.show(this)
            } ?: run {
                mineFragment = SettingsFragment().also {
                    transaction.replace(R.id.main_mine, it, MODULEIDX_MINE.toString())
                }
            }

            else -> {}
        }
        transaction.commitAllowingStateLoss()
        moduleIdx = index[0]
    }

    /**
     * 关闭fragment
     *
     * @param transaction
     * @param idx
     */
    private fun hideCurFragment(transaction: FragmentTransaction, idx: Int) {
        when (idx) {
            MODULEIDX_HOME -> homeFragment?.apply {
                transaction.hide(this)
            }

            MODULEIDX_AMUSEMENT -> amusementFragment?.apply {
                transaction.hide(this)
            }

            MODULEIDX_MSG -> msgFragment?.apply {
                transaction.hide(this)
            }

            MODULEIDX_MINE -> mineFragment?.apply {
                transaction.hide(this)
            }

            else -> {}
        }
    }


    private fun removeFragment(moduleIdx: Int) {
        val manager = supportFragmentManager
        val f = manager.findFragmentByTag(moduleIdx.toString())
        if (f != null) {
            manager.beginTransaction().remove(f).commit()
        }
    }


    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (!isPreExit) {
            isPreExit = true
            timer.start()
        } else {
            isExiting = true
            timer.cancel()
            finish()
        }
    }
}