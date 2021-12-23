package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.HelpFragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity

/**
 * Created by adamcchampion on 2017/08/14.
 */

class HelpActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return HelpFragment()
    }
}
