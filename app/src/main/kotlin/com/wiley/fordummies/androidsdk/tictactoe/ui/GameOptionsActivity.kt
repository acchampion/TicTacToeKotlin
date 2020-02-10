package com.wiley.fordummies.androidsdk.tictactoe.ui

import androidx.fragment.app.Fragment

/**
 * Created by adamcchampion on 2017/08/05.
 */

class GameOptionsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return GameOptionsFragment()
    }
}
