package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

/**
 * Created by adamcchampion on 2017/08/12.
 */

class VideoActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return VideoFragment()
    }
}
