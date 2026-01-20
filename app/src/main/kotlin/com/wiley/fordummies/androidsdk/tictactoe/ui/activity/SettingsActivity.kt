package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.SettingsFragment

/**
 * Activity for adjusting app settings.
 *
 * Created by adamcchampion on 2017/08/13.
 */
@Keep
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
        enableEdgeToEdge()

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

    override fun onStart() {
        super.onStart()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { v, windowInsets ->
            val sysBarsCutout = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout() or
                        WindowInsetsCompat.Type.systemGestures()
            )
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = sysBarsCutout.left
                topMargin = sysBarsCutout.top
                rightMargin = sysBarsCutout.right
                bottomMargin = sysBarsCutout.bottom
            }
            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED;
        }
    }
}
