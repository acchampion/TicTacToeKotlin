package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.AudioFragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity

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
