package com.wiley.fordummies.androidsdk.tictactoe

import androidx.fragment.app.Fragment

/**
 * Created by adamcchampion on 2017/08/12.
 */

class VideoActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return VideoFragment()
    }
}
