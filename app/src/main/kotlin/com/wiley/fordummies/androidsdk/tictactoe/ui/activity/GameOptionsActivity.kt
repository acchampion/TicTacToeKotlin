package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.GameOptionsFragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity

/**
 * Created by adamcchampion on 2017/08/05.
 */

class GameOptionsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return GameOptionsFragment()
    }
}
