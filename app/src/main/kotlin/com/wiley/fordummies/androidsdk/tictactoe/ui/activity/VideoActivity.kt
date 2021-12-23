package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.VideoFragment

/**
 * Created by adamcchampion on 2017/08/12.
 */

class VideoActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return VideoFragment()
    }
}
