package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

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
