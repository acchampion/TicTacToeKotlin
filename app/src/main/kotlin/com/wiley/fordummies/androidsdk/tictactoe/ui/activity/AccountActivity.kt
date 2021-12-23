package com.wiley.fordummies.androidsdk.tictactoe.ui.activity

import androidx.fragment.app.Fragment
import com.wiley.fordummies.androidsdk.tictactoe.R
import com.wiley.fordummies.androidsdk.tictactoe.ui.fragment.AccountFragment
import com.wiley.fordummies.androidsdk.tictactoe.ui.SingleFragmentActivity

/**
 * Activity for managing user accounts.
 *
 * Created by adamcchampion on 2017/08/05.
 */

class AccountActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return AccountFragment()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }
}
