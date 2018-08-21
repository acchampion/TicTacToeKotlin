package com.wiley.fordummies.androidsdk.tictactoe

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import android.util.Log
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
        (activity as AppCompatActivity).supportActionBar?.apply {
            subtitle = resources.getString(R.string.settings)
        }
    }
}
