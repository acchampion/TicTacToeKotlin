package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.SensorsFragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity

/**
 * Device sensors activity
 *
 * Created by adamcchampion on 2017/08/14.
 */

class SensorsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return SensorsFragment()
    }
}
