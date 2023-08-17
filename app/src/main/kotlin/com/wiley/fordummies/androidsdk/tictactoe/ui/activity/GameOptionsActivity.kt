package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.GameOptionsFragment

/**
 * Created by adamcchampion on 2017/08/05.
 */
@Keep
class GameOptionsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return GameOptionsFragment()
    }
}
