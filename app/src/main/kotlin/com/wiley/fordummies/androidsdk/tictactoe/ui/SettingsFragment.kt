package com.wiley.fordummies.androidsdk.tictactoe.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.wiley.fordummies.androidsdk.tictactoe.R

/**
 * Fragment for adjusting app settings.
 *
 * Created by adamcchampion on 2017/08/13.
 */

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load preferences from XML resource.
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onResume() {
        super.onResume()
		val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.apply {
            subtitle = resources.getString(R.string.settings)
        }
    }
}
