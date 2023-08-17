package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.SensorsFragment

/**
 * Device sensors activity
 *
 * Created by adamcchampion on 2017/08/14.
 */
@Keep
class SensorsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return SensorsFragment()
    }
}
