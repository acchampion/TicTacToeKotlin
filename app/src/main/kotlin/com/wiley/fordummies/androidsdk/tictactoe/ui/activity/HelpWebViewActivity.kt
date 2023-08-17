package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.HelpWebViewFragment

/**
 * Activity to show a WebView for Tic-Tac-Toe on Wikipedia.
 *
 * Created by adamcchampion on 2017/08/14.
 */
@Keep
class HelpWebViewActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return HelpWebViewFragment()
    }
}
