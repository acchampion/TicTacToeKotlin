package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.HelpWebViewFragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity

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
