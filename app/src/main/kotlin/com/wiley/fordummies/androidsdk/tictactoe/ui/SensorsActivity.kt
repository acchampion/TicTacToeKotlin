package com.wiley.fordummies.androidsdk.tictactoe.ui

import androidx.fragment.app.Fragment

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
