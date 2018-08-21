package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

/**
 * Created by adamcchampion on 2017/08/05.
 */

class GameOptionsActivity : SingleFragmentActivity() {
    protected val TAG = javaClass.simpleName

    override fun createFragment(): Fragment {
        return GameOptionsFragment()
    }
}
