package com.wiley.fordummies.androidsdk.tictactoe

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * Activity for adjusting app settings.
 *
 * Created by adamcchampion on 2017/08/13.
 */

class SettingsActivity : AppCompatActivity() {
    private val OPT_NAME = "name"
    private val OPT_NAME_DEF = "Player"
    private val OPT_PLAY_FIRST = "human_starts"
    private val OPT_PLAY_FIRST_DEF = true

    protected fun createFragment(): Fragment {
        return SettingsFragment()
    }

    protected val layoutResId: Int
        @LayoutRes
        get() = R.layout.activity_fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null) {
            val preferenceFragment = createFragment()
            fm.beginTransaction()
                    .replace(R.id.fragment_container, preferenceFragment)
                    .commit()
        }

        PreferenceManager.setDefaultValues(this, R.xml.settings, false)
    }
}
