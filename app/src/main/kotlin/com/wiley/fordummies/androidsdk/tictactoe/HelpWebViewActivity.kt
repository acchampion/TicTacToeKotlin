package com.wiley.fordummies.androidsdk.tictactoe

import android.support.v4.app.Fragment

/**
 * Activity to show a WebView for Tic-Tac-Toe on Wikipedia.
 *
 * Created by adamcchampion on 2017/08/14.
 */

class HelpWebViewActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return HelpWebViewFragment()
    }
}
