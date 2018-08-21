package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

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
