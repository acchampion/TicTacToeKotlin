package com.wiley.fordummies.androidsdk.tictactoe.ui

import androidx.fragment.app.Fragment

/**
 * Activity for playing Tic-Tac-Toe game.
 *
 * Created by adamcchampion on 2017/08/19.
 */

class GameSessionActivity : SingleFragmentActivity() {
    private var mGameSessionFragment: GameSessionFragment = recoverFragment()

    override fun createFragment(): Fragment? {
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
