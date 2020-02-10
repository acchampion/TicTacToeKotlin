package com.wiley.fordummies.androidsdk.tictactoe.ui

import androidx.fragment.app.Fragment

/**
 * Activity for handling audio.
 *
 * Created by adamcchampion on 2017/08/12.
 */

class AudioActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return AudioFragment()
    }
}
