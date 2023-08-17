package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.AudioFragment

/**
 * Activity for handling audio.
 *
 * Created by adamcchampion on 2017/08/12.
 */
@Keep
class AudioActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return AudioFragment()
    }
}
