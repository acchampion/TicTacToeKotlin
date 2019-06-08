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

    private fun createFragment(): Fragment {
        return SettingsFragment()
    }

    private val layoutResId: Int
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
