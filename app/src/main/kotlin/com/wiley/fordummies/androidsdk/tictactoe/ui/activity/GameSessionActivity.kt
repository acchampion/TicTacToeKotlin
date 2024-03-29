package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.GameSessionFragment

/**
 * Activity for playing Tic-Tac-Toe game.
 *
 * Created by adamcchampion on 2017/08/19.
 */
@Keep
class GameSessionActivity : SingleFragmentActivity() {
    private var mGameSessionFragment: GameSessionFragment = recoverFragment()

    override fun createFragment(): Fragment {
        return recoverFragment()
    }

    fun getFragmentForTest(): GameSessionFragment {
        recoverFragment()
        return mGameSessionFragment
    }

    fun humanTakesATurn(posX: Int, posY: Int) {
        recoverFragment()
        mGameSessionFragment.humanTakesATurn(posX, posY)
    }

    private fun recoverFragment(): GameSessionFragment {
        val fm = supportFragmentManager
        if (fm.fragments.size == 0) {
            mGameSessionFragment = GameSessionFragment()
        } else {
            for (fragment in fm.fragments) {
                if (fragment is GameSessionFragment) {
                    mGameSessionFragment = fragment
                }
            }
        }
        return mGameSessionFragment
    }
}
